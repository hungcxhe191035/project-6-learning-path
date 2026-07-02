package org.swp.my_learning_path.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.swp.my_learning_path.constant.ETransactionStatus;
import org.swp.my_learning_path.constant.ETransactionType;
import org.swp.my_learning_path.entity.WalletTransaction;

import java.util.List;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    List<WalletTransaction> findByWalletUserUserIdOrderByCreatedAtDesc(Long userId);
    List<WalletTransaction> findByOrderByCreatedAtDesc();
    Page<WalletTransaction> findByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT t FROM WalletTransaction t WHERE " +
            "(:type IS NULL OR t.transactionType = :type) AND " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:search IS NULL OR LOWER(t.wallet.user.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            " LOWER(t.wallet.user.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            " LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            " CAST(t.transactionId AS string) LIKE CONCAT('%', :search, '%'))")
    Page<WalletTransaction> searchTransactions(@Param("type") ETransactionType type,
                                               @Param("status") ETransactionStatus status,
                                               @Param("search") String search,
                                               Pageable pageable);
}
