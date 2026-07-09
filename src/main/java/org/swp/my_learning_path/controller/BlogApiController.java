package org.swp.my_learning_path.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.swp.my_learning_path.constant.EBlogStatus;
import org.swp.my_learning_path.dto.response.BlogDTO;
import org.swp.my_learning_path.security.CustomUserDetails;
import org.swp.my_learning_path.service.BlogService;
import org.swp.my_learning_path.service.S3Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BlogApiController {

    private final BlogService blogService;
    private final S3Service s3Service;

    /**
     * POST /api/blogs/upload-image
     * Nhận file ảnh từ TinyMCE/Quill của học viên/giảng viên và tải lên S3, trả về URL.
     */
    @PostMapping("/blogs/upload-image")
    public ResponseEntity<Map<String, String>> uploadBlogImage(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            String fileUrl = s3Service.uploadFile(file);
            Map<String, String> response = new HashMap<>();
            response.put("location", fileUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Fallback khi không có key S3
            String fakeUrl = "https://picsum.photos/seed/blog_inline_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000) + "/600/400";
            Map<String, String> response = new HashMap<>();
            response.put("location", fakeUrl);
            return ResponseEntity.ok(response);
        }
    }

    /**
     * GET /api/courses/{courseId}/blogs
     * Lấy danh sách bài viết của một khoá học, lọc theo vai trò người dùng hiện tại.
     */
    @GetMapping("/courses/{courseId}/blogs")
    public ResponseEntity<List<BlogDTO>> getBlogs(
            @PathVariable Long courseId,
            @RequestParam(value = "lessonId", required = false) Long lessonId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(blogService.getBlogs(courseId, lessonId, currentUser));
    }

    /**
     * POST /api/courses/{courseId}/blogs
     * Học viên / Giảng viên tạo bài viết mới (multipart/form-data).
     */
    @PostMapping("/courses/{courseId}/blogs")
    public ResponseEntity<BlogDTO> createBlog(
            @PathVariable Long courseId,
            @RequestParam(value = "lessonId", required = false) Long lessonId,
            @RequestParam("title") String title,
            @RequestParam("summary") String summary,
            @RequestParam("content") String content,
            @RequestParam(value = "coverFile", required = false) MultipartFile coverFile,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        BlogDTO created = blogService.createBlog(courseId, lessonId, title, summary, content, coverFile, currentUser);
        return ResponseEntity.ok(created);
    }

    /**
     * PUT /api/blogs/{blogId}/status
     * Giảng viên phê duyệt hoặc thay đổi trạng thái bài viết.
     * Body JSON: { "status": "APPROVED" }
     */
    @PutMapping("/blogs/{blogId}/status")
    public ResponseEntity<BlogDTO> updateBlogStatus(
            @PathVariable Long blogId,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        EBlogStatus newStatus = EBlogStatus.valueOf(body.get("status").toUpperCase());
        return ResponseEntity.ok(blogService.updateBlogStatus(blogId, newStatus, currentUser));
    }

    /**
     * DELETE /api/blogs/{blogId}
     * Giảng viên hoặc chính tác giả xoá bài viết (soft delete).
     */
    @DeleteMapping("/blogs/{blogId}")
    public ResponseEntity<Void> deleteBlog(
            @PathVariable Long blogId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        blogService.deleteBlog(blogId, currentUser);
        return ResponseEntity.ok().build();
    }

    /**
     * POST /api/blogs/{blogId}/view
     * Tăng view count khi người dùng mở đọc bài viết.
     */
    @PostMapping("/blogs/{blogId}/view")
    public ResponseEntity<BlogDTO> incrementView(
            @PathVariable Long blogId) {
        return ResponseEntity.ok(blogService.incrementViewAndGet(blogId));
    }
}
