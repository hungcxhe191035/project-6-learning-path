package org.swp.my_learning_path.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vouchers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Voucher extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "voucher_id")
    Long voucherId;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    String code; // Mã voucher (VD: WELCOME2026, INSTRUCTOR20)

    @Column(name = "discount_value", nullable = false)
    BigDecimal discountValue; // Số tiền giảm giá cố định (VND)

    @Column(name = "min_order_amount", nullable = false)
    BigDecimal minOrderAmount; // Giá trị khóa học tối thiểu để áp dụng

    @Column(name = "creator_role", nullable = false, length = 20)
    String creatorRole; // "ADMIN" hoặc "INSTRUCTOR"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id")
    User instructor; // Giảng viên tạo mã (nếu là INSTRUCTOR)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    Course course; // Khóa học áp dụng mã (nếu là INSTRUCTOR)

    @Column(name = "limit_usage", nullable = false)
    Integer limitUsage; // Lượt sử dụng tối đa hệ thống

    @Column(name = "used_count", nullable = false)
    Integer usedCount; // Lượt đã sử dụng thực tế

    @Column(name = "start_date", nullable = false)
    LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    LocalDateTime endDate;
}
