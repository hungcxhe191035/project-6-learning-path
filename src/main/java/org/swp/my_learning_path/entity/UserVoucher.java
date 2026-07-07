package org.swp.my_learning_path.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.swp.my_learning_path.constant.EVoucherStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_vouchers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserVoucher extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_voucher_id")
    Long userVoucherId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user; // Học viên sở hữu voucher này

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id", nullable = false)
    Voucher voucher; // Liên kết với thông tin chi tiết voucher

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    EVoucherStatus status; // UNUSED / USED

    @Column(name = "received_at", nullable = false)
    LocalDateTime receivedAt; // Thời điểm thu thập voucher vào ví

    @Column(name = "expiry_at", nullable = false)
    LocalDateTime expiryAt; // Thời điểm hết hạn riêng của voucher này trong ví

    @Column(name = "used_at")
    LocalDateTime usedAt; // Thời điểm thực tế đã sử dụng
}
