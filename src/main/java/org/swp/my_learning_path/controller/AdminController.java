package org.swp.my_learning_path.controller;

import org.swp.my_learning_path.dto.request.TagRequest;
import org.swp.my_learning_path.entity.Tag;
import org.swp.my_learning_path.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final TagService tagService;

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
        return "pages/admin/tags";
    }

    @PostMapping("/tags/create")
    public String createTag(
            @Valid @ModelAttribute("tagForm") TagRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            List<Tag> tags = tagService.getAllTags();
            model.addAttribute("tags", tags);
            model.addAttribute("totalTags", tags.size());
            model.addAttribute("editTag", null);
            model.addAttribute("pageTitle", "Quản lý Tag");
            model.addAttribute("activePage", "tags");
            return "pages/admin/tags";
        }

        try {
            tagService.createTag(request);
            redirectAttributes.addFlashAttribute("successMessage", "Đã thêm tag mới thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/tags";
    }

    @GetMapping("/tags/{id}/edit")
    public String editTagForm(@PathVariable("id") Long id, Model model) {
        Tag tag = tagService.getTagById(id);
        List<Tag> tags = tagService.getAllTags();
        model.addAttribute("tags", tags);
        model.addAttribute("totalTags", tags.size());
        model.addAttribute("tagForm", TagRequest.builder()
                .tagName(tag.getTagName())
                .description(tag.getDescription())
                .build());
        model.addAttribute("editTag", tag);
        model.addAttribute("pageTitle", "Chỉnh sửa Tag");
        model.addAttribute("activePage", "tags");
        return "pages/admin/tags";
    }

    @PostMapping("/tags/{id}/edit")
    public String updateTag(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("tagForm") TagRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            Tag tag = tagService.getTagById(id);
            List<Tag> tags = tagService.getAllTags();
            model.addAttribute("tags", tags);
            model.addAttribute("totalTags", tags.size());
            model.addAttribute("editTag", tag);
            model.addAttribute("pageTitle", "Chỉnh sửa Tag");
            model.addAttribute("activePage", "tags");
            return "pages/admin/tags";
        }

        try {
            tagService.updateTag(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật tag thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/tags";
    }

    @PostMapping("/tags/{id}/delete")
    public String deleteTag(
            @PathVariable("id") Long id,
            RedirectAttributes redirectAttributes) {
        try {
            tagService.deleteTag(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa tag thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/tags";
    }
}
