package org.swp.my_learning_path.controller;

import org.swp.my_learning_path.constant.EAccountStatus;
import org.swp.my_learning_path.constant.EApplicationStatus;
import org.swp.my_learning_path.constant.ERole;
import org.swp.my_learning_path.dto.request.AssignRoleRequest;
import org.swp.my_learning_path.dto.request.CreateUserRequest;
import org.swp.my_learning_path.dto.request.ReviewApplicationRequest;
import org.swp.my_learning_path.dto.request.TagRequest;
import org.swp.my_learning_path.entity.InstructorApplication;
import org.swp.my_learning_path.entity.Tag;
import org.swp.my_learning_path.entity.User;
import org.swp.my_learning_path.service.AdminService;
import org.swp.my_learning_path.service.InstructorApplicationService;
import org.swp.my_learning_path.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final TagService tagService;
    private final InstructorApplicationService applicationService;

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
        model.addAttribute("pendingCount", applicationService.countPending());

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
        model.addAttribute("pendingCount", applicationService.countPending());
        return "pages/admin/user-form";
    }

    @PostMapping("/users/create")
    public String createUser(
            @Valid @ModelAttribute("userForm") CreateUserRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", ERole.values());
            model.addAttribute("statuses", EAccountStatus.values());
            model.addAttribute("isEdit", false);
            model.addAttribute("pageTitle", "Thêm Người dùng Mới");
            model.addAttribute("activePage", "users");
            model.addAttribute("pendingCount", applicationService.countPending());
            return "pages/admin/user-form";
        }

        try {
            adminService.createUser(request);
            redirectAttributes.addFlashAttribute("successMessage", "Đã tạo tài khoản cho người dùng " + request.getFullName() + " thành công!");
            return "redirect:/admin/users";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roles", ERole.values());
            model.addAttribute("statuses", EAccountStatus.values());
            model.addAttribute("isEdit", false);
            model.addAttribute("pageTitle", "Thêm Người dùng Mới");
            model.addAttribute("activePage", "users");
            model.addAttribute("pendingCount", applicationService.countPending());
            return "pages/admin/user-form";
        }
    }

    // =============================================
    // GÁN VAI TRÒ NGƯỜI DÙNG (Assign Role)
    // =============================================
    @GetMapping("/users/{id}/assign-role")
    public String assignRoleForm(@PathVariable("id") Long id, Model model) {
        User user = adminService.getUserById(id);

        AssignRoleRequest roleForm = AssignRoleRequest.builder()
                .role(user.getRole())
                .build();

        model.addAttribute("roleForm", roleForm);
        model.addAttribute("userId", user.getUserId());
        model.addAttribute("userEmail", user.getEmail());
        model.addAttribute("userFullName", user.getFullName());
        model.addAttribute("roles", ERole.values());
        model.addAttribute("pageTitle", "Gán vai trò - " + user.getFullName());
        model.addAttribute("activePage", "users");
        model.addAttribute("pendingCount", applicationService.countPending());
        return "pages/admin/assign-role";
    }

    @PostMapping("/users/{id}/assign-role")
    public String assignRole(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("roleForm") AssignRoleRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            User user = adminService.getUserById(id);
            model.addAttribute("userId", id);
            model.addAttribute("userEmail", user.getEmail());
            model.addAttribute("userFullName", user.getFullName());
            model.addAttribute("roles", ERole.values());
            model.addAttribute("pageTitle", "Gán vai trò");
            model.addAttribute("activePage", "users");
            model.addAttribute("pendingCount", applicationService.countPending());
            return "pages/admin/assign-role";
        }

        try {
            adminService.assignRole(id, request.getRole());
            User user = adminService.getUserById(id);
            String roleName = request.getRole() == ERole.STUDENT ? "Học viên" : (request.getRole() == ERole.INSTRUCTOR ? "Giảng viên" : "Quản trị viên");
            redirectAttributes.addFlashAttribute("successMessage", "Đã gán vai trò mới (" + roleName + ") cho người dùng " + user.getFullName() + " thành công!");
            return "redirect:/admin/users";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            User user = adminService.getUserById(id);
            model.addAttribute("userId", id);
            model.addAttribute("userEmail", user.getEmail());
            model.addAttribute("userFullName", user.getFullName());
            model.addAttribute("roles", ERole.values());
            model.addAttribute("pageTitle", "Gán vai trò");
            model.addAttribute("activePage", "users");
            model.addAttribute("pendingCount", applicationService.countPending());
            return "pages/admin/assign-role";
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
        model.addAttribute("pendingCount", applicationService.countPending());
        return "pages/admin/user-details";
    }

    // =============================================
    // THAO TÁC TRẠNG THÁI
    // =============================================
    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        adminService.toggleUserStatus(id);
        User user = adminService.getUserById(id);
        String action = user.getStatus() == EAccountStatus.ACTIVE ? "mở khóa" : "khóa";
        redirectAttributes.addFlashAttribute("successMessage", "Đã " + action + " tài khoản của " + user.getFullName() + " thành công!");
        return "redirect:/admin/users";
    }

    // =============================================
    // BULK ACTIONS
    // =============================================
    @PostMapping("/users/bulk-lock")
    public String bulkLockUsers(
            @RequestParam(value = "userIds", required = false) List<Long> userIds,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        if (userIds != null && !userIds.isEmpty() && principal != null) {
            adminService.bulkLockUsers(userIds, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Đã khóa thành công " + userIds.size() + " tài khoản đã chọn!");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/bulk-unlock")
    public String bulkUnlockUsers(
            @RequestParam(value = "userIds", required = false) List<Long> userIds,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        if (userIds != null && !userIds.isEmpty() && principal != null) {
            adminService.bulkUnlockUsers(userIds, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Đã mở khóa thành công " + userIds.size() + " tài khoản đã chọn!");
        }
        return "redirect:/admin/users";
    }

    // =============================================
    // QUẢN LÝ ĐƠN XIN GIẢNG VIÊN
    // =============================================
    @GetMapping("/applications")
    public String listApplications(
            @RequestParam(value = "status", required = false) String statusStr,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {

        EApplicationStatus statusFilter = null;
        if (statusStr != null && !statusStr.trim().isEmpty() && !"ALL".equalsIgnoreCase(statusStr)) {
            try {
                statusFilter = EApplicationStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Bỏ qua giá trị không hợp lệ
            }
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<InstructorApplication> appPage = applicationService.getApplications(statusFilter, pageable);

        model.addAttribute("applications", appPage.getContent());
        model.addAttribute("appPage", appPage);
        model.addAttribute("selectedStatus", statusStr != null ? statusStr.toUpperCase() : "ALL");
        model.addAttribute("statuses", EApplicationStatus.values());
        model.addAttribute("pendingCount", applicationService.countPending());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", appPage.getTotalPages());
        model.addAttribute("totalElements", appPage.getTotalElements());
        model.addAttribute("pageTitle", "Duyệt Đơn Giảng Viên");
        model.addAttribute("activePage", "applications");
        return "pages/admin/applications";
    }

    @GetMapping("/applications/{id}")
    public String applicationDetail(@PathVariable("id") Long id, Model model) {
        InstructorApplication application = applicationService.getApplicationById(id);
        model.addAttribute("instructorApp", application);
        model.addAttribute("reviewForm", new ReviewApplicationRequest());
        model.addAttribute("pageTitle", "Chi tiết Đơn – " + application.getUser().getFullName());
        model.addAttribute("activePage", "applications");
        model.addAttribute("pendingCount", applicationService.countPending());
        return "pages/admin/application-detail";
    }

    @PostMapping("/applications/{id}/review")
    public String reviewApplication(
            @PathVariable("id") Long id,
            @ModelAttribute("reviewForm") ReviewApplicationRequest request,
            RedirectAttributes redirectAttributes) {
        try {
            InstructorApplication app = applicationService.getApplicationById(id);
            String userName = app.getUser().getFullName();
            applicationService.reviewApplication(id, request);

            if (request.getDecision() == EApplicationStatus.APPROVED) {
                redirectAttributes.addFlashAttribute("successMessage",
                        "Đã DUYỆT đơn của " + userName + ". Tài khoản đã được nâng cấp thành Giảng viên!");
            } else {
                redirectAttributes.addFlashAttribute("successMessage",
                        "Đã TỪ CHỐI đơn của " + userName + ".");
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/applications";
    }

    // =============================================
    // QUẢN LÝ TAG
    // =============================================
    @GetMapping("/tags")
    public String listTags(Model model) {
        List<Tag> tags = tagService.getAllTags();
        model.addAttribute("tags", tags);
        model.addAttribute("totalTags", tags.size());
        model.addAttribute("tagForm", new TagRequest());
        model.addAttribute("editTag", null);
        model.addAttribute("pageTitle", "Quản lý Tag");
        model.addAttribute("activePage", "tags");
        model.addAttribute("pendingCount", applicationService.countPending());
        return "pages/admin/tags";
    }

    @PostMapping("/tags/create")
    public String createTag(
            @Valid @ModelAttribute("tagForm") TagRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            List<Tag> tags = tagService.getAllTags();
            model.addAttribute("tags", tags);
            model.addAttribute("totalTags", tags.size());
            model.addAttribute("editTag", null);
            model.addAttribute("pageTitle", "Quản lý Tag");
            model.addAttribute("activePage", "tags");
            model.addAttribute("pendingCount", applicationService.countPending());
            return "pages/admin/tags";
        }
        try {
            tagService.createTag(request);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Tạo tag \"" + request.getTagName() + "\" thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/tags";
    }

    @GetMapping("/tags/{id}/edit")
    public String editTagForm(@PathVariable Long id, Model model) {
        Tag tag = tagService.getTagById(id);
        List<Tag> tags = tagService.getAllTags();
        TagRequest form = TagRequest.builder()
                .tagName(tag.getTagName())
                .description(tag.getDescription())
                .build();
        model.addAttribute("tagForm", form);
        model.addAttribute("editTag", tag);
        model.addAttribute("tags", tags);
        model.addAttribute("totalTags", tags.size());
        model.addAttribute("pageTitle", "Sửa Tag");
        model.addAttribute("activePage", "tags");
        model.addAttribute("pendingCount", applicationService.countPending());
        return "pages/admin/tags";
    }

    @PostMapping("/tags/{id}/edit")
    public String updateTag(
            @PathVariable Long id,
            @Valid @ModelAttribute("tagForm") TagRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    bindingResult.getFieldError() != null ?
                            bindingResult.getFieldError().getDefaultMessage() : "Thông tin không hợp lệ");
            return "redirect:/admin/tags";
        }
        try {
            tagService.updateTag(id, request);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Cập nhật tag thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/tags";
    }

    @PostMapping("/tags/{id}/delete")
    public String deleteTag(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Tag tag = tagService.getTagById(id);
            String name = tag.getTagName();
            tagService.deleteTag(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Xóa tag \"" + name + "\" thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/tags";
    }
}
