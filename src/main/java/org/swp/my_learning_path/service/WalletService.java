package org.swp.my_learning_path.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.swp.my_learning_path.constant.ETransactionStatus;
import org.swp.my_learning_path.constant.ETransactionType;
import org.swp.my_learning_path.entity.Wallet;
import org.swp.my_learning_path.entity.WalletTransaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface WalletService {
    Wallet getWalletByUserId(Long userId);
    String createDepositUrl(Long userId, BigDecimal amount, String ipAddress, String callbackUrl);
    boolean processVNPayCallback(Map<String, String> fields);
    void purchaseCourse(Long userId, Long courseId, String voucherCode);
    void purchaseCart(Long userId);
    void purchaseCart(Long userId, List<Long> courseIds);
    void createWithdrawRequest(Long userId, BigDecimal amount);
    List<WalletTransaction> getTransactionHistory(Long userId);
    List<WalletTransaction> getAllTransactions();
    Page<WalletTransaction> getAllTransactions(Pageable pageable);
    Page<WalletTransaction> searchTransactions(ETransactionType type, ETransactionStatus status, String search, Pageable pageable);
}
