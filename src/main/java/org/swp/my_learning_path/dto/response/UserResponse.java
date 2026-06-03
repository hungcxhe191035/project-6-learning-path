package org.swp.my_learning_path.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;
import org.swp.my_learning_path.constant.EAccountStatus;
import org.swp.my_learning_path.constant.ERole;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    Long userId;
    String fullName;
    String email;
    String phone;
    ERole role;
    EAccountStatus status;
    // Bank information
    String bankName;
    String bankCode;
    String bankAccountNumber;
    String bankAccountHolder;
    // Avatar
    String avatarUrl;
    // Wallet
    Long walletId;
    BigDecimal balance;
    // Audit fields
    String createdAt;
    String updatedAt;
}

