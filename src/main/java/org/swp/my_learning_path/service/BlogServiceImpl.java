package org.swp.my_learning_path.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.swp.my_learning_path.constant.EBlogStatus;
import org.swp.my_learning_path.constant.EFilePurpose;
import org.swp.my_learning_path.constant.EFileType;
import org.swp.my_learning_path.dto.response.BlogDTO;
import org.swp.my_learning_path.entity.*;
import org.swp.my_learning_path.repository.*;
import org.swp.my_learning_path.security.CustomUserDetails;

import java.io.IOException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final S3Service s3Service;
    private final AppFileService appFileService;

    @Override
    @Transactional(readOnly = true)
    public List<BlogDTO> getBlogs(Long courseId, Long lessonId, CustomUserDetails currentUser) {
        List<Blog> blogs;

        if (currentUser == null) {
            // Khách vãng lai: chỉ bài đã duyệt
            if (lessonId != null) {
                blogs = blogRepository.findByCourseIdAndLessonIdAndStatus(courseId, lessonId, EBlogStatus.APPROVED);
            } else {
                blogs = blogRepository.findByCourseIdAndStatus(courseId, EBlogStatus.APPROVED);
            }
        } else {
            Long userId = currentUser.getUserId();
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Khoá học không tồn tại: " + courseId));

            boolean isInstructor = course.getInstructor() != null
                    && course.getInstructor().getUserId().equals(userId);

            if (isInstructor) {
                // Giảng viên: xem toàn bộ bài viết
                if (lessonId != null) {
                    blogs = blogRepository.findAllByCourseIdAndLessonId(courseId, lessonId);
                } else {
                    blogs = blogRepository.findAllByCourseId(courseId);
                }
            } else {
                boolean isEnrolled = enrollmentRepository
                        .existsByStudent_UserIdAndCourse_CourseId(userId, courseId);
                if (isEnrolled) {
                    // Học viên đã đăng ký: bài đã duyệt + bài của chính mình
                    if (lessonId != null) {
                        blogs = blogRepository.findVisibleToEnrolledUserAndLessonId(courseId, lessonId, userId);
                    } else {
                        blogs = blogRepository.findVisibleToEnrolledUser(courseId, userId);
                    }
                } else {
                    // Học viên chưa đăng ký: chỉ bài đã duyệt
                    if (lessonId != null) {
                        blogs = blogRepository.findByCourseIdAndLessonIdAndStatus(courseId, lessonId, EBlogStatus.APPROVED);
                    } else {
                        blogs = blogRepository.findByCourseIdAndStatus(courseId, EBlogStatus.APPROVED);
                    }
                }
            }
        }

        return blogs.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BlogDTO createBlog(Long courseId, Long lessonId, String title, String summary, String content,
                              MultipartFile coverFile, CustomUserDetails currentUser) {
        if (currentUser == null) {
            throw new RuntimeException("Bạn cần đăng nhập để viết bài.");
        }
        Long userId = currentUser.getUserId();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Khoá học không tồn tại: " + courseId));

        boolean isInstructor = course.getInstructor() != null
                && course.getInstructor().getUserId().equals(userId);
        boolean isEnrolled = enrollmentRepository
                .existsByStudent_UserIdAndCourse_CourseId(userId, courseId);

        if (!isInstructor && !isEnrolled) {
            throw new RuntimeException("Bạn cần đăng ký khoá học trước khi viết bài.");
        }

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại."));

        Lesson lesson = null;
        if (lessonId != null) {
            lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new RuntimeException("Bài học không tồn tại: " + lessonId));
        }

        // Upload ảnh bìa lên S3 nếu có
        AppFile coverAppFile = null;
        if (coverFile != null && !coverFile.isEmpty()) {
            try {
                String coverUrl = s3Service.uploadFile(coverFile);
                String originalName = coverFile.getOriginalFilename() != null
                        ? coverFile.getOriginalFilename() : "cover.jpg";
                coverAppFile = appFileService.saveFileInfo(coverUrl, EFileType.IMAGE, originalName, EFilePurpose.COURSE_THUMBNAIL);
            } catch (Exception e) {
                log.warn("Không có S3 key hoặc upload lỗi, dùng ảnh bìa test thay thế: {}", e.getMessage());
                String coverUrl = "https://picsum.photos/seed/cover_" + System.currentTimeMillis() + "/800/400";
                String originalName = coverFile.getOriginalFilename() != null
                        ? coverFile.getOriginalFilename() : "cover.jpg";
                coverAppFile = appFileService.saveFileInfo(coverUrl, EFileType.IMAGE, originalName, EFilePurpose.COURSE_THUMBNAIL);
            }
        }

        Blog blog = Blog.builder()
                .title(title)
                .summary(summary)
                .content(content)
                .course(course)
                .author(author)
                .cover(coverAppFile)
                .lesson(lesson)
                .status(EBlogStatus.PENDING)
                .views(0)
                .build();

        Blog saved = blogRepository.save(blog);
        log.info("Học viên {} đã tạo bài viết blogId={} cho khoá học {}", userId, saved.getBlogId(), courseId);
        return toDTO(saved);
    }

    @Override
    @Transactional
    public BlogDTO updateBlogStatus(Long blogId, EBlogStatus newStatus, CustomUserDetails currentUser) {
        if (currentUser == null) {
            throw new RuntimeException("Bạn cần đăng nhập.");
        }

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Bài viết không tồn tại: " + blogId));

        Long instructorId = blog.getCourse().getInstructor().getUserId();
        if (!instructorId.equals(currentUser.getUserId())) {
            throw new RuntimeException("Chỉ giảng viên của khoá học mới có quyền phê duyệt bài viết.");
        }

        blog.setStatus(newStatus);
        Blog saved = blogRepository.save(blog);
        log.info("Giảng viên {} đã đổi trạng thái blogId={} sang {}", currentUser.getUserId(), blogId, newStatus);
        return toDTO(saved);
    }

    @Override
    @Transactional
    public void deleteBlog(Long blogId, CustomUserDetails currentUser) {
        if (currentUser == null) {
            throw new RuntimeException("Bạn cần đăng nhập.");
        }

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Bài viết không tồn tại: " + blogId));

        Long userId = currentUser.getUserId();
        Long authorId = blog.getAuthor().getUserId();
        Long instructorId = blog.getCourse().getInstructor().getUserId();

        if (!userId.equals(authorId) && !userId.equals(instructorId)) {
            throw new RuntimeException("Bạn không có quyền xoá bài viết này.");
        }

        blog.setDeleteFlag(true);
        blogRepository.save(blog);
        log.info("Người dùng {} đã xoá blogId={}", userId, blogId);
    }

    @Override
    @Transactional
    public BlogDTO incrementViewAndGet(Long blogId) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Bài viết không tồn tại: " + blogId));
        blog.setViews(blog.getViews() + 1);
        return toDTO(blogRepository.save(blog));
    }

    // ===== Helper =====

    private BlogDTO toDTO(Blog blog) {
        String coverUrl = null;
        if (blog.getCover() != null) {
            coverUrl = blog.getCover().getFileUrl();
        }

        String authorName = blog.getAuthor() != null ? blog.getAuthor().getFullName() : "Ẩn danh";
        String authorLetter = buildAuthorLetter(authorName);

        Long lessonId = null;
        String lessonTitle = null;
        if (blog.getLesson() != null) {
            lessonId = blog.getLesson().getLessonId();
            lessonTitle = blog.getLesson().getTitle();
        }

        return BlogDTO.builder()
                .blogId(blog.getBlogId())
                .title(blog.getTitle())
                .summary(blog.getSummary())
                .content(blog.getContent())
                .status(blog.getStatus())
                .views(blog.getViews())
                .coverUrl(coverUrl)
                .authorId(blog.getAuthor() != null ? blog.getAuthor().getUserId() : null)
                .authorName(authorName)
                .authorLetter(authorLetter)
                .createdAt(blog.getCreatedAt())
                .lessonId(lessonId)
                .lessonTitle(lessonTitle)
                .build();
    }

    private String buildAuthorLetter(String fullName) {
        if (fullName == null || fullName.isBlank()) return "?";
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) {
            return fullName.substring(0, Math.min(2, fullName.length())).toUpperCase();
        }
        return (parts[0].charAt(0) + "" + parts[parts.length - 1].charAt(0)).toUpperCase();
    }
}
