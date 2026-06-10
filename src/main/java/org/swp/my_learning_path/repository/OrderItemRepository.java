package org.swp.my_learning_path.repository;

import org.swp.my_learning_path.constant.ETransactionStatus;
import org.swp.my_learning_path.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Tính tổng doanh thu của một Giảng viên cụ thể
    @Query("SELECT SUM(oi.price) FROM OrderItem oi " +
           "JOIN oi.order o " +
           "JOIN oi.course c " +
           "WHERE c.instructor.userId = :instructorId " +
           "AND o.paymentStatus = :status")
    BigDecimal sumRevenueByInstructor(@Param("instructorId") Long instructorId,
                                      @Param("status") ETransactionStatus status);

    // Lấy danh sách các giao dịch bán khóa học của một Giảng viên cụ thể
    @Query("SELECT oi FROM OrderItem oi " +
           "JOIN oi.order o " +
           "JOIN oi.course c " +
           "WHERE c.instructor.userId = :instructorId " +
           "AND o.paymentStatus = :status " +
           "ORDER BY o.createdAt DESC")
    List<OrderItem> findSuccessfulSalesByInstructor(@Param("instructorId") Long instructorId,
                                                    @Param("status") ETransactionStatus status);

    // [MỚI] Lấy doanh thu theo từng tháng trong một năm cụ thể cho Giảng viên
    // Trả về List<Object[]> mỗi phần tử là [thangSo (Integer), tongTien (BigDecimal)]
    @Query("SELECT MONTH(o.createdAt), SUM(oi.price) " +
           "FROM OrderItem oi " +
           "JOIN oi.order o " +
           "JOIN oi.course c " +
           "WHERE c.instructor.userId = :instructorId " +
           "AND o.paymentStatus = :status " +
           "AND YEAR(o.createdAt) = :year " +
           "GROUP BY MONTH(o.createdAt) " +
           "ORDER BY MONTH(o.createdAt)")
    List<Object[]> getMonthlyRevenueForInstructor(@Param("instructorId") Long instructorId,
                                                   @Param("status") ETransactionStatus status,
                                                   @Param("year") int year);
}

