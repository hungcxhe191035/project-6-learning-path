package org.swp.my_learning_path.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.swp.my_learning_path.dto.response.VoucherApplyResponse;
import org.swp.my_learning_path.security.CustomUserDetails;
import org.swp.my_learning_path.service.VoucherService;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
public class VoucherApiController {

    private final VoucherService voucherService;

    // API Áp dụng mã giảm giá và xem trước số tiền thanh toán
    @PostMapping("/apply")
    public ResponseEntity<?> applyVoucher(
            @RequestParam String code,
            @RequestParam Long courseId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(401).body("Vui lòng đăng nhập để áp dụng mã giảm giá!");
        }

        try {
            VoucherApplyResponse response = voucherService.calculateVoucher(
                    code, courseId, userDetails.getUserId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
