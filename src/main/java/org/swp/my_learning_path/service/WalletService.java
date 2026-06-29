package org.swp.my_learning_path.service;

import org.swp.my_learning_path.entity.Wallet;
import org.swp.my_learning_path.entity.WalletTransaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface WalletService {
    Wallet getWalletByUserId(Long userId);
    String createDepositUrl(Long userId, BigDecimal amount, String ipAddress, String callbackUrl);
    boolean processVNPayCallback(Map<String, String> fields);
    void purchaseCourse(Long userId, Long courseId);
    void purchaseCart(Long userId);
    void createWithdrawRequest(Long userId, BigDecimal amount);
    void approveWithdraw(Long transactionId);
    void rejectWithdraw(Long transactionId);
    List<WalletTransaction> getTransactionHistory(Long userId);
    List<WalletTransaction> getAllTransactions();
}
