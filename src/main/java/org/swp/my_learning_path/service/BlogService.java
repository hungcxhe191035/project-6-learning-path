package org.swp.my_learning_path.service;

import org.springframework.web.multipart.MultipartFile;
import org.swp.my_learning_path.constant.EBlogStatus;
import org.swp.my_learning_path.dto.response.BlogDTO;
import org.swp.my_learning_path.security.CustomUserDetails;

import java.util.List;

public interface BlogService {

    /**
     * Lấy danh sách bài viết của một khoá học hoặc bài học cụ thể, lọc theo vai trò và trạng thái.
     */
    List<BlogDTO> getBlogs(Long courseId, Long lessonId, CustomUserDetails currentUser);

    /**
     * Học viên / Giảng viên tạo bài viết mới (status mặc định là PENDING).
     */
    BlogDTO createBlog(Long courseId, Long lessonId, String title, String summary, String content,
                       MultipartFile coverFile, CustomUserDetails currentUser);

    /**
     * Giảng viên thay đổi trạng thái bài viết (APPROVED / PENDING / REJECTED).
     */
    BlogDTO updateBlogStatus(Long blogId, EBlogStatus newStatus, CustomUserDetails currentUser);

    /**
     * Giảng viên hoặc chính tác giả xoá bài viết (soft delete).
     */
    void deleteBlog(Long blogId, CustomUserDetails currentUser);

    /**
     * Tăng view count khi bài viết được mở đọc.
     */
    BlogDTO incrementViewAndGet(Long blogId);
}
