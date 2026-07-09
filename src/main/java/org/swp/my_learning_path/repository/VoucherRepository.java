package org.swp.my_learning_path.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swp.my_learning_path.entity.Voucher;

import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {

    // Tìm voucher theo code và chưa bị xóa mềm (delete_flag = false)
    Optional<Voucher> findByCodeAndDeleteFlagFalse(String code);

    // Lấy tất cả voucher chưa bị xóa mềm
    java.util.List<Voucher> findAllByDeleteFlagFalse();

    // Lấy voucher do giảng viên cụ thể tạo ra và chưa bị xóa mềm
    java.util.List<Voucher> findByInstructor_UserIdAndDeleteFlagFalse(Long instructorId);

    // Lấy voucher theo vai trò người tạo
    java.util.List<Voucher> findByCreatorRoleAndDeleteFlagFalse(String creatorRole);
}
