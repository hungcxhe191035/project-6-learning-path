package com.swp391.final_project.dto.response;

import com.swp391.final_project.constant.EAccountStatus;
import com.swp391.final_project.constant.ERole;
import lombok.*;
import lombok.experimental.FieldDefaults;

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

