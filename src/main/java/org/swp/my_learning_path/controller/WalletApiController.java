package org.swp.my_learning_path.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.swp.my_learning_path.security.CustomUserDetails;
import org.swp.my_learning_path.service.SystemSettingService;
import org.swp.my_learning_path.service.WalletService;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class WalletApiController {

    private final WalletService walletService;
    private final SystemSettingService systemSettingService;

    @PostMapping("/api/wallet/deposit")
    public ResponseEntity<?> deposit(
            @RequestParam("amount") BigDecimal amount,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest request
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Vui lòng đăng nhập!"));
        }

        try {
            // Lấy IP client
            String ipAddress = request.getRemoteAddr();
            if (ipAddress == null || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                ipAddress = "127.0.0.1";
            }

            // Xây dựng callback url động
            String scheme = request.getScheme();
            String serverName = request.getServerName();
            int serverPort = request.getServerPort();
            String callbackUrl = scheme + "://" + serverName;
            if (serverPort != 80 && serverPort != 443) {
                callbackUrl += ":" + serverPort;
            }
            callbackUrl += "/wallet/callback";

            String depositUrl = walletService.createDepositUrl(userDetails.getUserId(), amount, ipAddress, callbackUrl);
            return ResponseEntity.ok(Map.of("success", true, "redirectUrl", depositUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/api/wallet/withdraw")
    public ResponseEntity<?> withdraw(
            @RequestParam("amount") BigDecimal amount,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Vui lòng đăng nhập!"));
        }

        try {
            walletService.createWithdrawRequest(userDetails.getUserId(), amount);
            return ResponseEntity.ok(Map.of("success", true, "message", "Rút tiền thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/api/wallet/pay/course/{courseId}")
    public ResponseEntity<?> payCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Vui lòng đăng nhập!"));
        }

        try {
            walletService.purchaseCourse(userDetails.getUserId(), courseId);
            return ResponseEntity.ok(Map.of("success", true, "redirect", "/course/" + courseId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/api/wallet/pay/cart")
    public ResponseEntity<?> payCart(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Vui lòng đăng nhập!"));
        }

        try {
            walletService.purchaseCart(userDetails.getUserId());
            return ResponseEntity.ok(Map.of("success", true, "redirect", "/my-learning-path"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/api/admin/transactions/{txId}/approve")
    public ResponseEntity<?> approveWithdraw(
            @PathVariable Long txId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null || !"ADMIN".equals(userDetails.getUser().getRole().name())) {
            return ResponseEntity.status(403).body(Map.of("message", "Không có quyền thực hiện!"));
        }

        try {
            walletService.approveWithdraw(txId);
            return ResponseEntity.ok(Map.of("success", true, "message", "Duyệt yêu cầu rút tiền thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/api/admin/transactions/{txId}/reject")
    public ResponseEntity<?> rejectWithdraw(
            @PathVariable Long txId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null || !"ADMIN".equals(userDetails.getUser().getRole().name())) {
            return ResponseEntity.status(403).body(Map.of("message", "Không có quyền thực hiện!"));
        }

        try {
            walletService.rejectWithdraw(txId);
            return ResponseEntity.ok(Map.of("success", true, "message", "Từ chối yêu cầu rút tiền thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/api/admin/settings/revenue-share")
    public ResponseEntity<?> updateRevenueShare(
            @RequestParam("sharePercent") String sharePercent,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null || !"ADMIN".equals(userDetails.getUser().getRole().name())) {
            return ResponseEntity.status(403).body(Map.of("message", "Không có quyền thực hiện!"));
        }

        try {
            int val = Integer.parseInt(sharePercent);
            if (val < 0 || val > 100) {
                throw new IllegalArgumentException("Tỉ lệ % ăn chia phải từ 0 đến 100!");
            }
            systemSettingService.saveSetting("INSTRUCTOR_REVENUE_SHARE_PERCENT", sharePercent, "Tỉ lệ chia sẻ doanh thu giảng viên (%)");
            return ResponseEntity.ok(Map.of("success", true, "message", "Cập nhật tỉ lệ chia sẻ doanh thu thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
