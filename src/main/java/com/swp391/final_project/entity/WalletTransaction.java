package com.swp391.final_project.entity;

import com.swp391.final_project.constant.ETransactionStatus;
import com.swp391.final_project.constant.ETransactionType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Entity
@Table(name = "wallet_transactions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Slf4j
public class WalletTransaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    Long transactionId;

    @Column(name = "amount", precision = 18, scale = 2)
    BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", length = 30)
    ETransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    ETransactionStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    Wallet wallet;
}
