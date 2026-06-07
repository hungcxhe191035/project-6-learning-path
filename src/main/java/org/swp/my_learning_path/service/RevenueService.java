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
}
