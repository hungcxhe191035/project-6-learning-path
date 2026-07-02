package org.swp.my_learning_path.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swp.my_learning_path.constant.ERole;
import org.swp.my_learning_path.constant.ETransactionStatus;
import org.swp.my_learning_path.constant.ETransactionType;
import org.swp.my_learning_path.entity.*;
import org.swp.my_learning_path.repository.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CartItemRepository cartItemRepository;
    private final VNPayService vnpayService;
    private final SystemSettingService systemSettingService;

    @Override
    @Transactional
    public Wallet getWalletByUserId(Long userId) {
        return walletRepository.findByUserUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng!"));
                    Wallet wallet = Wallet.builder()
                            .balance(BigDecimal.ZERO)
                            .user(user)
                            .build();
                    wallet.setDeleteFlag(false);
                    return walletRepository.save(wallet);
                });
    }

    @Override
    @Transactional
    public String createDepositUrl(Long userId, BigDecimal amount, String ipAddress, String callbackUrl) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số tiền nạp phải lớn hơn 0!");
        }

        Wallet wallet = getWalletByUserId(userId);

        // Tạo giao dịch PENDING
        WalletTransaction transaction = WalletTransaction.builder()
                .wallet(wallet)
                .amount(amount)
                .transactionType(ETransactionType.DEPOSIT)
                .status(ETransactionStatus.PENDING)
                .description("Nạp tiền vào ví qua cổng VNPay")
                .build();
        transaction.setDeleteFlag(false);
        walletTransactionRepository.save(transaction);

        // Tạo url thanh toán VNPay
        return vnpayService.createPaymentUrl(transaction.getTransactionId(), amount.longValue(), ipAddress, callbackUrl);
    }

    @Override
    @Transactional
    public boolean processVNPayCallback(Map<String, String> fields) {
        boolean verified = vnpayService.verifyCallback(fields);
        if (!verified) {
            return false;
        }

        String responseCode = fields.get("vnp_ResponseCode");
        String txnRef = fields.get("vnp_TxnRef");
        if (txnRef == null) {
            return false;
        }

        Long txnId = Long.parseLong(txnRef);
        WalletTransaction transaction = walletTransactionRepository.findById(txnId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy giao dịch!"));

        if (transaction.getStatus() == ETransactionStatus.PENDING) {
            if ("00".equals(responseCode)) {
                transaction.setStatus(ETransactionStatus.SUCCESS);
                Wallet wallet = transaction.getWallet();
                wallet.setBalance(wallet.getBalance().add(transaction.getAmount()));
                walletRepository.save(wallet);
                walletTransactionRepository.save(transaction);
                return true;
            } else {
                transaction.setStatus(ETransactionStatus.FAIL);
                walletTransactionRepository.save(transaction);
                return false;
            }
        }
        
        return transaction.getStatus() == ETransactionStatus.SUCCESS;
    }

    @Override
    @Transactional
    public void purchaseCourse(Long userId, Long courseId) {
        User student = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy học viên!"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khoá học!"));

        // Giảng viên không được mua khoá học của chính mình
        if (course.getInstructor() != null && course.getInstructor().getUserId().equals(userId)) {
            throw new IllegalStateException("Bạn là giảng viên của khoá học này, không thể tự mua!");
        }

        // Đã mua rồi thì không mua nữa
        if (enrollmentRepository.existsByStudent_UserIdAndCourse_CourseId(userId, courseId)) {
            throw new IllegalStateException("Bạn đã sở hữu khoá học này rồi!");
        }

        BigDecimal price = course.getCurrentPublishedVersion().getPrice();
        if (price == null) {
            price = BigDecimal.ZERO;
        }

        Wallet studentWallet = getWalletByUserId(userId);
        if (studentWallet.getBalance().compareTo(price) < 0) {
            throw new IllegalStateException("Số dư ví không đủ, vui lòng nạp thêm tiền!");
        }

        // Trừ tiền ví học viên
        studentWallet.setBalance(studentWallet.getBalance().subtract(price));
        walletRepository.save(studentWallet);

        // Tạo đơn hàng
        Order order = Order.builder()
                .user(student)
                .totalAmount(price)
                .paymentStatus(ETransactionStatus.SUCCESS)
                .build();
        order.setDeleteFlag(false);
        orderRepository.save(order);

        // Chi tiết đơn hàng
        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .course(course)
                .price(price)
                .build();
        orderItem.setDeleteFlag(false);
        orderItemRepository.save(orderItem);

        // Tạo giao dịch ví cho học viên
        WalletTransaction studentTx = WalletTransaction.builder()
                .wallet(studentWallet)
                .amount(price)
                .transactionType(ETransactionType.PAYMENT)
                .status(ETransactionStatus.SUCCESS)
                .description("Thanh toán khoá học: " + course.getCurrentPublishedVersion().getTitle())
                .order(order)
                .build();
        studentTx.setDeleteFlag(false);
        walletTransactionRepository.save(studentTx);

        // Tạo Enrollment
        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .build();
        enrollment.setDeleteFlag(false);
        enrollmentRepository.save(enrollment);

        // Chia sẻ doanh thu cho Giảng viên & Admin
        if (price.compareTo(BigDecimal.ZERO) > 0) {
            User admin = userRepository.findByEmail("admin@fcourse.vn")
                    .orElseGet(() -> userRepository.findAll().stream()
                            .filter(u -> u.getRole() == ERole.ADMIN && !u.isDeleteFlag())
                            .findFirst()
                            .orElse(null));

            if (course.getInstructor() != null) {
                int sharePercent = systemSettingService.getSettingValueAsInteger("INSTRUCTOR_REVENUE_SHARE_PERCENT", 80);
                BigDecimal instructorAmount = price.multiply(new BigDecimal(sharePercent))
                        .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);

                Wallet instructorWallet = getWalletByUserId(course.getInstructor().getUserId());
                instructorWallet.setBalance(instructorWallet.getBalance().add(instructorAmount));
                walletRepository.save(instructorWallet);

                WalletTransaction instructorTx = WalletTransaction.builder()
                        .wallet(instructorWallet)
                        .amount(instructorAmount)
                        .transactionType(ETransactionType.PAYMENT)
                        .status(ETransactionStatus.SUCCESS)
                        .description("Doanh thu (" + sharePercent + "%) từ khoá học: " + course.getCurrentPublishedVersion().getTitle() + " (Học viên: " + student.getFullName() + ")")
                        .order(order)
                        .build();
                instructorTx.setDeleteFlag(false);
                walletTransactionRepository.save(instructorTx);

                if (admin != null) {
                    BigDecimal adminAmount = price.subtract(instructorAmount);
                    if (adminAmount.compareTo(BigDecimal.ZERO) > 0) {
                        Wallet adminWallet = getWalletByUserId(admin.getUserId());
                        adminWallet.setBalance(adminWallet.getBalance().add(adminAmount));
                        walletRepository.save(adminWallet);

                        WalletTransaction adminTx = WalletTransaction.builder()
                                .wallet(adminWallet)
                                .amount(adminAmount)
                                .transactionType(ETransactionType.PAYMENT)
                                .status(ETransactionStatus.SUCCESS)
                                .description("Doanh thu admin (" + (100 - sharePercent) + "%) từ khoá học: " + course.getCurrentPublishedVersion().getTitle() + " (Học viên: " + student.getFullName() + ")")
                                .order(order)
                                .build();
                        adminTx.setDeleteFlag(false);
                        walletTransactionRepository.save(adminTx);
                    }
                }
            } else {
                if (admin != null) {
                    Wallet adminWallet = getWalletByUserId(admin.getUserId());
                    adminWallet.setBalance(adminWallet.getBalance().add(price));
                    walletRepository.save(adminWallet);

                    WalletTransaction adminTx = WalletTransaction.builder()
                            .wallet(adminWallet)
                            .amount(price)
                            .transactionType(ETransactionType.PAYMENT)
                            .status(ETransactionStatus.SUCCESS)
                            .description("Doanh thu từ khoá học (Hệ thống): " + course.getCurrentPublishedVersion().getTitle() + " (Học viên: " + student.getFullName() + ")")
                            .order(order)
                            .build();
                    adminTx.setDeleteFlag(false);
                    walletTransactionRepository.save(adminTx);
                }
            }
        }
    }

    @Override
    @Transactional
    public void purchaseCart(Long userId) {
        User student = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy học viên!"));
        List<CartItem> cartItems = cartItemRepository.findByUserUserId(userId);
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Giỏ hàng của bạn đang trống!");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            Course course = item.getCourse();
            // Bỏ qua kiểm tra hoặc kiểm tra xem đã mua chưa
            if (enrollmentRepository.existsByStudent_UserIdAndCourse_CourseId(userId, course.getCourseId())) {
                throw new IllegalStateException("Khoá học '" + course.getCurrentPublishedVersion().getTitle() + "' đã được bạn sở hữu!");
            }
            if (course.getInstructor() != null && course.getInstructor().getUserId().equals(userId)) {
                throw new IllegalStateException("Bạn không thể mua khoá học '" + course.getCurrentPublishedVersion().getTitle() + "' do chính bạn giảng dạy!");
            }
            BigDecimal price = course.getCurrentPublishedVersion().getPrice();
            if (price != null) {
                totalAmount = totalAmount.add(price);
            }
        }

        Wallet studentWallet = getWalletByUserId(userId);
        if (studentWallet.getBalance().compareTo(totalAmount) < 0) {
            throw new IllegalStateException("Số dư ví không đủ để thanh toán toàn bộ giỏ hàng!");
        }

        // Trừ tiền ví học viên
        studentWallet.setBalance(studentWallet.getBalance().subtract(totalAmount));
        walletRepository.save(studentWallet);

        // Tạo đơn hàng
        Order order = Order.builder()
                .user(student)
                .totalAmount(totalAmount)
                .paymentStatus(ETransactionStatus.SUCCESS)
                .build();
        order.setDeleteFlag(false);
        orderRepository.save(order);

        // Tạo giao dịch ví cho học viên
        WalletTransaction studentTx = WalletTransaction.builder()
                .wallet(studentWallet)
                .amount(totalAmount)
                .transactionType(ETransactionType.PAYMENT)
                .status(ETransactionStatus.SUCCESS)
                .description("Thanh toán mua " + cartItems.size() + " khoá học từ giỏ hàng")
                .order(order)
                .build();
        studentTx.setDeleteFlag(false);
        walletTransactionRepository.save(studentTx);

        // Xử lý từng khoá học trong giỏ hàng
        int sharePercent = systemSettingService.getSettingValueAsInteger("INSTRUCTOR_REVENUE_SHARE_PERCENT", 80);
        for (CartItem item : cartItems) {
            Course course = item.getCourse();
            BigDecimal price = course.getCurrentPublishedVersion().getPrice();
            if (price == null) {
                price = BigDecimal.ZERO;
            }

            // Chi tiết đơn hàng
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .course(course)
                    .price(price)
                    .build();
            orderItem.setDeleteFlag(false);
            orderItemRepository.save(orderItem);

            // Tạo Enrollment
            Enrollment enrollment = Enrollment.builder()
                    .student(student)
                    .course(course)
                    .build();
            enrollment.setDeleteFlag(false);
            enrollmentRepository.save(enrollment);

            // Chia sẻ doanh thu cho giảng viên & Admin
            if (price.compareTo(BigDecimal.ZERO) > 0) {
                User admin = userRepository.findByEmail("admin@fcourse.vn")
                        .orElseGet(() -> userRepository.findAll().stream()
                                .filter(u -> u.getRole() == ERole.ADMIN && !u.isDeleteFlag())
                                .findFirst()
                                .orElse(null));

                if (course.getInstructor() != null) {
                    BigDecimal instructorAmount = price.multiply(new BigDecimal(sharePercent))
                            .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);

                    Wallet instructorWallet = getWalletByUserId(course.getInstructor().getUserId());
                    instructorWallet.setBalance(instructorWallet.getBalance().add(instructorAmount));
                    walletRepository.save(instructorWallet);

                    WalletTransaction instructorTx = WalletTransaction.builder()
                            .wallet(instructorWallet)
                            .amount(instructorAmount)
                            .transactionType(ETransactionType.PAYMENT)
                            .status(ETransactionStatus.SUCCESS)
                            .description("Doanh thu (" + sharePercent + "%) từ khoá học: " + course.getCurrentPublishedVersion().getTitle() + " (Học viên: " + student.getFullName() + ")")
                            .order(order)
                            .build();
                    instructorTx.setDeleteFlag(false);
                    walletTransactionRepository.save(instructorTx);

                    if (admin != null) {
                        BigDecimal adminAmount = price.subtract(instructorAmount);
                        if (adminAmount.compareTo(BigDecimal.ZERO) > 0) {
                            Wallet adminWallet = getWalletByUserId(admin.getUserId());
                            adminWallet.setBalance(adminWallet.getBalance().add(adminAmount));
                            walletRepository.save(adminWallet);

                            WalletTransaction adminTx = WalletTransaction.builder()
                                    .wallet(adminWallet)
                                    .amount(adminAmount)
                                    .transactionType(ETransactionType.PAYMENT)
                                    .status(ETransactionStatus.SUCCESS)
                                    .description("Doanh thu admin (" + (100 - sharePercent) + "%) từ khoá học: " + course.getCurrentPublishedVersion().getTitle() + " (Học viên: " + student.getFullName() + ")")
                                    .order(order)
                                    .build();
                            adminTx.setDeleteFlag(false);
                            walletTransactionRepository.save(adminTx);
                        }
                    }
                } else {
                    if (admin != null) {
                        Wallet adminWallet = getWalletByUserId(admin.getUserId());
                        adminWallet.setBalance(adminWallet.getBalance().add(price));
                        walletRepository.save(adminWallet);

                        WalletTransaction adminTx = WalletTransaction.builder()
                                .wallet(adminWallet)
                                .amount(price)
                                .transactionType(ETransactionType.PAYMENT)
                                .status(ETransactionStatus.SUCCESS)
                                .description("Doanh thu từ khoá học (Hệ thống): " + course.getCurrentPublishedVersion().getTitle() + " (Học viên: " + student.getFullName() + ")")
                                .order(order)
                                .build();
                        adminTx.setDeleteFlag(false);
                        walletTransactionRepository.save(adminTx);
                    }
                }
            }
        }

        // Xoá giỏ hàng
        cartItemRepository.deleteAll(cartItems);
    }

    @Override
    @Transactional
    public void createWithdrawRequest(Long userId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số tiền rút phải lớn hơn 0!");
        }

        Wallet wallet = getWalletByUserId(userId);
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Số dư khả dụng trong ví không đủ để rút!");
        }

        // Khấu trừ số dư ví
        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        // Tạo giao dịch SUCCESS ngay lập tức (không cần duyệt)
        WalletTransaction transaction = WalletTransaction.builder()
                .wallet(wallet)
                .amount(amount)
                .transactionType(ETransactionType.WITHDRAW)
                .status(ETransactionStatus.SUCCESS)
                .description("Rút tiền về tài khoản ngân hàng")
                .build();
        transaction.setDeleteFlag(false);
        walletTransactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public void approveWithdraw(Long transactionId) {
        WalletTransaction transaction = walletTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy giao dịch rút tiền!"));

        if (transaction.getTransactionType() != ETransactionType.WITHDRAW) {
            throw new IllegalStateException("Giao dịch không phải loại rút tiền!");
        }

        if (transaction.getStatus() != ETransactionStatus.PENDING) {
            throw new IllegalStateException("Yêu cầu rút tiền này đã được xử lý trước đó!");
        }

        transaction.setStatus(ETransactionStatus.SUCCESS);
        walletTransactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public void rejectWithdraw(Long transactionId) {
        WalletTransaction transaction = walletTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy giao dịch rút tiền!"));

        if (transaction.getTransactionType() != ETransactionType.WITHDRAW) {
            throw new IllegalStateException("Giao dịch không phải loại rút tiền!");
        }

        if (transaction.getStatus() != ETransactionStatus.PENDING) {
            throw new IllegalStateException("Yêu cầu rút tiền này đã được xử lý trước đó!");
        }

        transaction.setStatus(ETransactionStatus.FAIL);
        
        // Hoàn lại tiền vào ví người dùng
        Wallet wallet = transaction.getWallet();
        wallet.setBalance(wallet.getBalance().add(transaction.getAmount()));
        
        walletRepository.save(wallet);
        walletTransactionRepository.save(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WalletTransaction> getTransactionHistory(Long userId) {
        return walletTransactionRepository.findByWalletUserUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WalletTransaction> getAllTransactions() {
        return walletTransactionRepository.findByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WalletTransaction> getAllTransactions(Pageable pageable) {
        return walletTransactionRepository.findByOrderByCreatedAtDesc(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WalletTransaction> searchTransactions(ETransactionType type, ETransactionStatus status, String search, Pageable pageable) {
        String searchParam = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        return walletTransactionRepository.searchTransactions(type, status, searchParam, pageable);
    }
}
