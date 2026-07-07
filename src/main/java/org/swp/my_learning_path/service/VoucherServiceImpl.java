package org.swp.my_learning_path.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swp.my_learning_path.constant.EVoucherStatus;
import org.swp.my_learning_path.dto.response.VoucherApplyResponse;
import org.swp.my_learning_path.entity.Course;
import org.swp.my_learning_path.entity.UserVoucher;
import org.swp.my_learning_path.entity.Voucher;
import org.swp.my_learning_path.repository.CourseRepository;
import org.swp.my_learning_path.repository.UserVoucherRepository;
import org.swp.my_learning_path.repository.VoucherRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final UserVoucherRepository userVoucherRepository;
    private final CourseRepository courseRepository;

    @Override
    @Transactional(readOnly = true)
    public VoucherApplyResponse calculateVoucher(String code, Long courseId, Long studentId) {
        // 1. Tìm kiếm voucher trong hệ thống
        Voucher voucher = voucherRepository.findByCodeAndDeleteFlagFalse(code)
                .orElseThrow(() -> new IllegalArgumentException("Mã giảm giá không tồn tại hoặc đã bị xóa!"));

        // 2. Tìm kiếm khóa học
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Khóa học không tồn tại!"));

        BigDecimal originalPrice = course.getCurrentPublishedVersion() != null
                ? course.getCurrentPublishedVersion().getPrice()
                : BigDecimal.ZERO;

        // 3. KIỂM TRA HẠN DÙNG VÀ LƯỢT DÙNG CHUNG CỦA HỆ THỐNG
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(voucher.getStartDate()) || now.isAfter(voucher.getEndDate())) {
            return VoucherApplyResponse.builder().success(false).message("Mã giảm giá đã hết hạn sử dụng!").build();
        }

        if (voucher.getUsedCount() >= voucher.getLimitUsage()) {
            return VoucherApplyResponse.builder().success(false).message("Mã giảm giá đã hết lượt sử dụng trên hệ thống!").build();
        }

        if (originalPrice.compareTo(voucher.getMinOrderAmount()) < 0) {
            return VoucherApplyResponse.builder().success(false)
                    .message("Khóa học chưa đạt giá trị tối thiểu " + voucher.getMinOrderAmount() + "đ để áp dụng!").build();
        }

        // 4. KIỂM TRA QUYỀN SỞ HỮU TRONG VÍ VOUCHER CỦA HỌC VIÊN
        UserVoucher userVoucher = userVoucherRepository.findByUser_UserIdAndVoucher_Code(studentId, code)
                .orElse(null);

        // Đối với mã đặc biệt cần thu thập (như mã giỏ hàng, hoàn thành khóa học):
        // Học viên phải thu thập voucher vào ví trước thì mới được dùng.
        if (userVoucher != null) {
            if (userVoucher.getStatus() == EVoucherStatus.USED) {
                return VoucherApplyResponse.builder().success(false).message("Bạn đã sử dụng mã giảm giá này rồi!").build();
            }
            if (now.isAfter(userVoucher.getExpiryAt())) {
                return VoucherApplyResponse.builder().success(false).message("Voucher trong ví của bạn đã hết hạn!").build();
            }
        }

        // 5. KIỂM TRA ĐIỀU KIỆN KHÓA HỌC (Nếu là voucher của Giảng viên)
        if ("INSTRUCTOR".equals(voucher.getCreatorRole())) {
            if (voucher.getCourse() == null || !voucher.getCourse().getCourseId().equals(courseId)) {
                return VoucherApplyResponse.builder().success(false)
                        .message("Mã giảm giá này chỉ dành riêng cho khóa học chỉ định của giảng viên!").build();
            }
        }

        // 6. TÍNH SỐ TIỀN THỰC TRẢ (A - D), bảo đảm không âm
        BigDecimal discountAmount = voucher.getDiscountValue();
        BigDecimal actualPaid = originalPrice.subtract(discountAmount).max(BigDecimal.ZERO);

        // 7. CÔNG THỨC PHÂN CHIA DOANH THU 80/20 THEO ĐÚNG YÊU CẦU NGHIỆP VỤ
        BigDecimal instructorShare;
        BigDecimal adminShare;

        if ("ADMIN".equals(voucher.getCreatorRole())) {
            // ADMIN tạo -> Admin gánh 100% giảm giá. Giảng viên nhận đủ 80% giá gốc.
            instructorShare = originalPrice.multiply(new BigDecimal("0.8"));
            adminShare = actualPaid.subtract(instructorShare);
        } else {
            // INSTRUCTOR tạo -> Giảng viên tự gánh 100% giảm giá. Sàn thu đủ 20% giá gốc.
            adminShare = originalPrice.multiply(new BigDecimal("0.2"));
            instructorShare = actualPaid.subtract(adminShare).max(BigDecimal.ZERO);
        }

        return VoucherApplyResponse.builder()
                .success(true)
                .message("Áp dụng mã giảm giá thành công!")
                .code(code)
                .originalPrice(originalPrice)
                .discountAmount(discountAmount)
                .actualPaid(actualPaid)
                .instructorShare(instructorShare)
                .adminShare(adminShare)
                .build();
    }

    @Override
    @Transactional
    public void confirmUseVoucher(String code, Long studentId) {
        Voucher voucher = voucherRepository.findByCodeAndDeleteFlagFalse(code)
                .orElseThrow(() -> new IllegalArgumentException("Mã giảm giá không tồn tại!"));

        // 1. Tăng lượt sử dụng chung
        voucher.setUsedCount(voucher.getUsedCount() + 1);
        voucherRepository.save(voucher);

        // 2. Nếu học viên có voucher này trong ví, cập nhật trạng thái thành USED
        userVoucherRepository.findByUser_UserIdAndVoucher_Code(studentId, code)
                .ifPresent(uv -> {
                    uv.setStatus(EVoucherStatus.USED);
                    uv.setUsedAt(LocalDateTime.now());
                    userVoucherRepository.save(uv);
                });
    }
}
