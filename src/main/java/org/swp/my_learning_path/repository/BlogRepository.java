package org.swp.my_learning_path.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.swp.my_learning_path.constant.EBlogStatus;
import org.swp.my_learning_path.entity.Blog;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {

    /**
     * Lấy tất cả bài viết của một khoá học (dùng cho giảng viên xem toàn bộ).
     */
    @Query("SELECT b FROM Blog b WHERE b.course.courseId = :courseId AND b.deleteFlag = false ORDER BY b.createdAt DESC")
    List<Blog> findAllByCourseId(@Param("courseId") Long courseId);

    /**
     * Lấy bài viết theo trạng thái (dùng để lấy bài đã duyệt cho khách vãng lai).
     */
    @Query("SELECT b FROM Blog b WHERE b.course.courseId = :courseId AND b.status = :status AND b.deleteFlag = false ORDER BY b.createdAt DESC")
    List<Blog> findByCourseIdAndStatus(@Param("courseId") Long courseId, @Param("status") EBlogStatus status);

    /**
     * Lấy bài đã duyệt + bài của chính học viên đó (dùng cho học viên đã đăng ký).
     */
    @Query("SELECT b FROM Blog b WHERE b.course.courseId = :courseId AND b.deleteFlag = false " +
           "AND (b.status = 'APPROVED' OR b.author.userId = :userId) ORDER BY b.createdAt DESC")
    List<Blog> findVisibleToEnrolledUser(@Param("courseId") Long courseId, @Param("userId") Long userId);

    @Query("SELECT b FROM Blog b WHERE b.course.courseId = :courseId AND b.lesson.lessonId = :lessonId AND b.deleteFlag = false ORDER BY b.createdAt DESC")
    List<Blog> findAllByCourseIdAndLessonId(@Param("courseId") Long courseId, @Param("lessonId") Long lessonId);

    @Query("SELECT b FROM Blog b WHERE b.course.courseId = :courseId AND b.lesson.lessonId = :lessonId AND b.status = :status AND b.deleteFlag = false ORDER BY b.createdAt DESC")
    List<Blog> findByCourseIdAndLessonIdAndStatus(@Param("courseId") Long courseId, @Param("lessonId") Long lessonId, @Param("status") EBlogStatus status);

    @Query("SELECT b FROM Blog b WHERE b.course.courseId = :courseId AND b.lesson.lessonId = :lessonId AND b.deleteFlag = false " +
           "AND (b.status = 'APPROVED' OR b.author.userId = :userId) ORDER BY b.createdAt DESC")
    List<Blog> findVisibleToEnrolledUserAndLessonId(@Param("courseId") Long courseId, @Param("lessonId") Long lessonId, @Param("userId") Long userId);
}
