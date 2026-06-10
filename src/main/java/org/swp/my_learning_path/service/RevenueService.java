package org.swp.my_learning_path.service;

import org.swp.my_learning_path.entity.Order;
import org.swp.my_learning_path.entity.OrderItem;

import java.math.BigDecimal;
import java.util.List;

public interface RevenueService {
    // Dành cho Giảng viên
    BigDecimal getInstructorTotalRevenue(Long instructorId);
    List<OrderItem> getInstructorSalesHistory(Long instructorId);

    // Dành cho Admin
    BigDecimal getAdminTotalRevenue();
    List<Order> getAdminSalesHistory();

    // [MỚI] Lấy doanh thu theo từng tháng (12 tháng) trong năm hiện tại
    // Trả về List 12 phần tử BigDecimal, index 0 = Tháng 1, index 11 = Tháng 12
    List<BigDecimal> getInstructorMonthlyRevenue(Long instructorId, int year);
    List<BigDecimal> getAdminMonthlyRevenue(int year);
}
