package org.swp.my_learning_path.repository;

import org.swp.my_learning_path.constant.ETransactionStatus;
import org.swp.my_learning_path.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Lấy danh sách các đơn hàng thành công để làm lịch sử giao dịch cho Admin
    List<Order> findByPaymentStatusOrderByCreatedAtDesc(ETransactionStatus status);

    // Tính tổng doanh thu toàn hệ thống cho Admin
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.paymentStatus = :status")
    BigDecimal sumTotalRevenue(ETransactionStatus status);
}
