package org.swp.my_learning_path.service;

import org.swp.my_learning_path.dto.response.VoucherApplyResponse;

public interface VoucherService {

    // Áp dụng mã giảm giá và tính toán chia tiền hoa hồng tạm tính
    VoucherApplyResponse calculateVoucher(String code, Long courseId, Long studentId);

    // Ghi nhận sử dụng voucher khi thanh toán thành công
    void confirmUseVoucher(String code, Long studentId);
}
