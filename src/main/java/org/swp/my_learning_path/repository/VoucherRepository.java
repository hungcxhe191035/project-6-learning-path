package org.swp.my_learning_path.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swp.my_learning_path.entity.Voucher;

import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {

    // Tìm voucher theo code và chưa bị xóa mềm (delete_flag = false)
    Optional<Voucher> findByCodeAndDeleteFlagFalse(String code);
}
