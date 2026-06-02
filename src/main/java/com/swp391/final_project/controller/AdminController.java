package com.swp391.final_project.controller;

import com.swp391.final_project.constant.EAccountStatus;
import com.swp391.final_project.constant.ERole;
import com.swp391.final_project.entity.User;
import com.swp391.final_project.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
                // Ignore invalid role
            }
        }

        EAccountStatus status = null;
        if (statusStr != null && !statusStr.trim().isEmpty() && !"ALL".equalsIgnoreCase(statusStr)) {
            try {
                status = EAccountStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Ignore invalid status
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

    @GetMapping("/users/create")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", ERole.values());
        model.addAttribute("statuses", EAccountStatus.values());
        model.addAttribute("isEdit", false);
        model.addAttribute("pageTitle", "Thêm Người dùng Mới");
        model.addAttribute("activePage", "users");
        return "pages/admin/user-form";
    }

    @PostMapping("/users/create")
    public String createUser(
            @ModelAttribute("user") User user,
            @RequestParam("plainPassword") String plainPassword,
            Model model) {
        try {
            adminService.createUser(user, plainPassword);
            return "redirect:/admin/users?success=true";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", user);
            model.addAttribute("roles", ERole.values());
            model.addAttribute("statuses", EAccountStatus.values());
            model.addAttribute("isEdit", false);
            model.addAttribute("pageTitle", "Thêm Người dùng Mới");
            model.addAttribute("activePage", "users");
            return "pages/admin/user-form";
        }
    }

    @GetMapping("/users/{id}/edit")
    public String editUserForm(@PathVariable("id") Long id, Model model) {
        User user = adminService.getUserById(id);
        model.addAttribute("user", user);
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
            @ModelAttribute("user") User userDetails,
            @RequestParam(value = "newPassword", required = false) String newPassword,
            Model model) {
        try {
            adminService.updateUser(id, userDetails, newPassword);
            return "redirect:/admin/users?success=true";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", userDetails);
            model.addAttribute("roles", ERole.values());
            model.addAttribute("statuses", EAccountStatus.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("pageTitle", "Chỉnh sửa Người dùng - " + userDetails.getFullName());
            model.addAttribute("activePage", "users");
            return "pages/admin/user-form";
        }
    }

    @GetMapping("/users/{id}")
    public String userDetails(@PathVariable("id") Long id, Model model) {
        User user = adminService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Chi tiết Người dùng - " + user.getFullName());
        model.addAttribute("activePage", "users");
        return "pages/admin/user-details";
    }

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
