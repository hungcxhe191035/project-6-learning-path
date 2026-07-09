package org.swp.my_learning_path.service;

import org.swp.my_learning_path.dto.response.VoucherApplyResponse;

public interface VoucherService {

    // Áp dụng mã giảm giá và tính toán chia tiền hoa hồng tạm tính
    VoucherApplyResponse calculateVoucher(String code, Long courseId, Long studentId);

    // Ghi nhận sử dụng voucher khi thanh toán thành công
    void confirmUseVoucher(String code, Long studentId);

    // Lấy tất cả voucher trong hệ thống
    java.util.List<org.swp.my_learning_path.entity.Voucher> getAllVouchers();

    // Lấy danh sách voucher của giảng viên cụ thể
    java.util.List<org.swp.my_learning_path.entity.Voucher> getVouchersByInstructor(Long instructorId);

    // Tạo mới một voucher
    org.swp.my_learning_path.entity.Voucher createVoucher(org.swp.my_learning_path.entity.Voucher voucher);

    // Xóa mềm voucher theo ID
    void deleteVoucher(Long voucherId);
}
