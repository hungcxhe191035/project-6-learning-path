package com.swp391.final_project.controller;

import com.swp391.final_project.constant.EAccountStatus;
import com.swp391.final_project.constant.ERole;
import com.swp391.final_project.dto.request.CreateUserRequest;
import com.swp391.final_project.dto.request.UpdateUserRequest;
import com.swp391.final_project.entity.User;
import com.swp391.final_project.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.security.Principal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // =============================================
    // DANH SÁCH NGƯỜI DÙNG (Search + Filter + Page)
    // =============================================
    @GetMapping("/users")
    public String listUsers(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "role", required = false) String roleStr,
            @RequestParam(value = "status", required = false) String statusStr,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {

        ERole role = null;
        if (roleStr != null && !roleStr.trim().isEmpty() && !"ALL".equalsIgnoreCase(roleStr)) {
            try {
                role = ERole.valueOf(roleStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Bỏ qua giá trị role không hợp lệ
            }
        }

        EAccountStatus status = null;
        if (statusStr != null && !statusStr.trim().isEmpty() && !"ALL".equalsIgnoreCase(statusStr)) {
            try {
                status = EAccountStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Bỏ qua giá trị status không hợp lệ
            }
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("userId").ascending());
        Page<User> userPage = adminService.searchUsers(role, status, search, pageable);

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("userPage", userPage);
        model.addAttribute("search", search);
        model.addAttribute("selectedRole", roleStr != null ? roleStr.toUpperCase() : "ALL");
        model.addAttribute("selectedStatus", statusStr != null ? statusStr.toUpperCase() : "ALL");
        model.addAttribute("pageTitle", "Quản lý Người dùng");
        model.addAttribute("roles", ERole.values());
        model.addAttribute("statuses", EAccountStatus.values());
        model.addAttribute("activePage", "users");
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalElements", userPage.getTotalElements());

        return "pages/admin/users";
    }

    // =============================================
    // TẠO MỚI NGƯỜI DÙNG
    // =============================================
    @GetMapping("/users/create")
    public String createUserForm(Model model) {
        model.addAttribute("userForm", new CreateUserRequest());
        model.addAttribute("roles", ERole.values());
        model.addAttribute("statuses", EAccountStatus.values());
        model.addAttribute("isEdit", false);
        model.addAttribute("pageTitle", "Thêm Người dùng Mới");
        model.addAttribute("activePage", "users");
        return "pages/admin/user-form";
    }

    @PostMapping("/users/create")
    public String createUser(
            @Valid @ModelAttribute("userForm") CreateUserRequest request,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", ERole.values());
            model.addAttribute("statuses", EAccountStatus.values());
            model.addAttribute("isEdit", false);
            model.addAttribute("pageTitle", "Thêm Người dùng Mới");
            model.addAttribute("activePage", "users");
            return "pages/admin/user-form";
        }

        try {
            adminService.createUser(request);
            return "redirect:/admin/users?success=true";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roles", ERole.values());
            model.addAttribute("statuses", EAccountStatus.values());
            model.addAttribute("isEdit", false);
            model.addAttribute("pageTitle", "Thêm Người dùng Mới");
            model.addAttribute("activePage", "users");
            return "pages/admin/user-form";
        }
    }

    // =============================================
    // CHỈNH SỬA NGƯỜI DÙNG
    // =============================================
    @GetMapping("/users/{id}/edit")
    public String editUserForm(@PathVariable("id") Long id, Model model) {
        User user = adminService.getUserById(id);

        // Map entity sang DTO để bind form
        UpdateUserRequest updateForm = UpdateUserRequest.builder()
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .build();

        model.addAttribute("userForm", updateForm);
        model.addAttribute("userId", user.getUserId());
        model.addAttribute("userEmail", user.getEmail()); // hiển thị readonly
        model.addAttribute("roles", ERole.values());
        model.addAttribute("statuses", EAccountStatus.values());
        model.addAttribute("isEdit", true);
        model.addAttribute("pageTitle", "Chỉnh sửa Người dùng - " + user.getFullName());
        model.addAttribute("activePage", "users");
        return "pages/admin/user-form";
    }

    @PostMapping("/users/{id}/edit")
    public String editUser(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("userForm") UpdateUserRequest request,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            User user = adminService.getUserById(id);
            model.addAttribute("userId", id);
            model.addAttribute("userEmail", user.getEmail());
            model.addAttribute("roles", ERole.values());
            model.addAttribute("statuses", EAccountStatus.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("pageTitle", "Chỉnh sửa Người dùng");
            model.addAttribute("activePage", "users");
            return "pages/admin/user-form";
        }

        try {
            adminService.updateUser(id, request);
            return "redirect:/admin/users?success=true";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("userId", id);
            model.addAttribute("roles", ERole.values());
            model.addAttribute("statuses", EAccountStatus.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("pageTitle", "Chỉnh sửa Người dùng");
            model.addAttribute("activePage", "users");
            return "pages/admin/user-form";
        }
    }

    // =============================================
    // CHI TIẾT NGƯỜI DÙNG
    // =============================================
    @GetMapping("/users/{id}")
    public String userDetails(@PathVariable("id") Long id, Model model) {
        User user = adminService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Chi tiết Người dùng - " + user.getFullName());
        model.addAttribute("activePage", "users");
        return "pages/admin/user-details";
    }

    // =============================================
    // THAO TÁC TRẠNG THÁI
    // =============================================
    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable("id") Long id) {
        adminService.toggleUserStatus(id);
        return "redirect:/admin/users?success=true";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable("id") Long id) {
        adminService.deleteUser(id);
        return "redirect:/admin/users?success=true";
    }

    // =============================================
    // BULK ACTIONS
    // =============================================
    @PostMapping("/users/bulk-lock")
    public String bulkLockUsers(
            @RequestParam(value = "userIds", required = false) List<Long> userIds,
            Principal principal) {
        if (userIds != null && !userIds.isEmpty() && principal != null) {
            adminService.bulkLockUsers(userIds, principal.getName());
        }
        return "redirect:/admin/users?success=true";
    }

    @PostMapping("/users/bulk-unlock")
    public String bulkUnlockUsers(
            @RequestParam(value = "userIds", required = false) List<Long> userIds,
            Principal principal) {
        if (userIds != null && !userIds.isEmpty() && principal != null) {
            adminService.bulkUnlockUsers(userIds, principal.getName());
        }
        return "redirect:/admin/users?success=true";
    }

    @PostMapping("/users/bulk-delete")
    public String bulkDeleteUsers(
            @RequestParam(value = "userIds", required = false) List<Long> userIds,
            Principal principal) {
        if (userIds != null && !userIds.isEmpty() && principal != null) {
            adminService.bulkDeleteUsers(userIds, principal.getName());
        }
        return "redirect:/admin/users?success=true";
    }
}
