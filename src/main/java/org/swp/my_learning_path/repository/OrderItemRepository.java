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

    @Query("SELECT oi.course, COUNT(oi) as salesCount FROM OrderItem oi " +
           "WHERE oi.order.paymentStatus = :status " +
           "AND oi.order.createdAt BETWEEN :start AND :end " +
           "GROUP BY oi.course " +
           "ORDER BY salesCount DESC")
    List<Object[]> findTopSellingCoursesInPeriod(
            @Param("status") ETransactionStatus status,
            @Param("start") java.time.LocalDateTime start,
            @Param("end") java.time.LocalDateTime end,
            org.springframework.data.domain.Pageable pageable
    );

    @Query("SELECT oi.course.instructor, SUM(oi.price) as totalRev FROM OrderItem oi " +
           "WHERE oi.order.paymentStatus = :status " +
           "AND oi.order.createdAt BETWEEN :start AND :end " +
           "GROUP BY oi.course.instructor " +
           "ORDER BY totalRev DESC")
    List<Object[]> findTopInstructorsInPeriod(
            @Param("status") ETransactionStatus status,
            @Param("start") java.time.LocalDateTime start,
            @Param("end") java.time.LocalDateTime end,
            org.springframework.data.domain.Pageable pageable
    );
}
