package org.swp.my_learning_path.service;

import org.swp.my_learning_path.constant.ETransactionStatus;
import org.swp.my_learning_path.entity.Order;
import org.swp.my_learning_path.entity.OrderItem;
import org.swp.my_learning_path.repository.OrderItemRepository;
import org.swp.my_learning_path.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RevenueServiceImpl implements RevenueService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public BigDecimal getInstructorTotalRevenue(Long instructorId) {
        BigDecimal revenue = orderItemRepository.sumRevenueByInstructor(instructorId, ETransactionStatus.SUCCESS);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    @Override
    public List<OrderItem> getInstructorSalesHistory(Long instructorId) {
        return orderItemRepository.findSuccessfulSalesByInstructor(instructorId, ETransactionStatus.SUCCESS);
    }

    @Override
    public BigDecimal getAdminTotalRevenue() {
        BigDecimal revenue = orderRepository.sumTotalRevenue(ETransactionStatus.SUCCESS);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    @Override
    public List<Order> getAdminSalesHistory() {
        return orderRepository.findByPaymentStatusOrderByCreatedAtDesc(ETransactionStatus.SUCCESS);
    }

    // [MỚI] Xây dựng mảng 12 BigDecimal cho Giảng viên
    // DB chỉ trả về những tháng có dữ liệu, ta phải điền 0 vào tháng không có
    @Override
    public List<BigDecimal> getInstructorMonthlyRevenue(Long instructorId, int year) {
        List<Object[]> rawData = orderItemRepository.getMonthlyRevenueForInstructor(
                instructorId, ETransactionStatus.SUCCESS, year);
        return buildMonthlyList(rawData);
    }

    // [MỚI] Xây dựng mảng 12 BigDecimal cho Admin
    @Override
    public List<BigDecimal> getAdminMonthlyRevenue(int year) {
        List<Object[]> rawData = orderRepository.getMonthlyRevenueForAdmin(
                ETransactionStatus.SUCCESS, year);
        return buildMonthlyList(rawData);
    }

    // Hàm dùng chung: chuyển kết quả query [tháng, tiền] → mảng 12 phần tử
    // Ví dụ: DB trả về [[3, 500000], [7, 200000]]
    // → [0, 0, 500000, 0, 0, 0, 200000, 0, 0, 0, 0, 0]
    private List<BigDecimal> buildMonthlyList(List<Object[]> rawData) {
        // Tạo mảng 12 tháng, mặc định tất cả = 0
        BigDecimal[] monthlyArray = new BigDecimal[12];
        for (int i = 0; i < 12; i++) {
            monthlyArray[i] = BigDecimal.ZERO;
        }

        // Điền dữ liệu thật từ DB vào đúng vị trí tháng
        for (Object[] row : rawData) {
            int month = ((Number) row[0]).intValue();   // Tháng (1-12)
            BigDecimal amount = (BigDecimal) row[1];    // Tổng tiền tháng đó
            monthlyArray[month - 1] = amount != null ? amount : BigDecimal.ZERO;
        }

        // Chuyển mảng thành List để dễ truyền qua Thymeleaf
        List<BigDecimal> result = new ArrayList<>();
        for (BigDecimal val : monthlyArray) {
            result.add(val);
        }
        return result;
    }
}
