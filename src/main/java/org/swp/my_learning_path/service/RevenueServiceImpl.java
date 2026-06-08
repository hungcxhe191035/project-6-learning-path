package org.swp.my_learning_path.service;

import org.swp.my_learning_path.constant.ETransactionStatus;
import org.swp.my_learning_path.entity.Order;
import org.swp.my_learning_path.entity.OrderItem;
import org.swp.my_learning_path.repository.OrderItemRepository;
import org.swp.my_learning_path.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
}
