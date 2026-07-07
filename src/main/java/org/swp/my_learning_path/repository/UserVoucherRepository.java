package org.swp.my_learning_path.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swp.my_learning_path.constant.EVoucherStatus;
import org.swp.my_learning_path.entity.UserVoucher;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserVoucherRepository extends JpaRepository<UserVoucher, Long> {

    // Tìm voucher cụ thể của học viên
    Optional<UserVoucher> findByUser_UserIdAndVoucher_Code(Long userId, String code);

    // Lấy danh sách các voucher khả dụng của học viên (chưa sử dụng, chưa bị xóa)
    List<UserVoucher> findByUser_UserIdAndStatusAndDeleteFlagFalse(Long userId, EVoucherStatus status);
}
