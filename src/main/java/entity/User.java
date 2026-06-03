package entity;

import com.hsf302.final_project.constant.EAccountStatus;
import com.hsf302.final_project.constant.ERole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Slf4j
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    Long userId;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    String email;

    @Column(name = "password", nullable = false, length = 255)
    String password;

    @Column(name = "full_name", columnDefinition = "NVARCHAR(255)")
    String fullName;

    @Column(name = "phone", length = 20)
    String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    ERole role; // STUDENT / INSTRUCTOR / ADMIN

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    EAccountStatus status; // ACTIVE / PENDING_VERIFICATION / INACTIVE

    // Thông tin ngân hàng
    @Column(name = "bank_name", columnDefinition = "NVARCHAR(255)")
    String bankName;
    // Vietcombank / Techcombank / MB Bank

    @Column(name = "bank_code", length = 50)
    String bankCode;
    // VCB / TCB / MBB

    @Column(name = "bank_account_number", length = 100)
    String bankAccountNumber;

    @Column(name = "bank_account_holder", columnDefinition = "NVARCHAR(255)")
    String bankAccountHolder;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avatar_file_id")
    AppFile avatar;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    Wallet wallet;

    // ---- Reset Password ----
    @Column(name = "reset_token", length = 100)
    String resetToken;

    @Column(name = "reset_token_expiry")
    java.time.LocalDateTime resetTokenExpiry;
}
