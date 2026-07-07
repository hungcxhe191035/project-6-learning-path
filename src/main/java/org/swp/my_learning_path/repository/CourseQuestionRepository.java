package org.swp.my_learning_path.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.swp.my_learning_path.constant.EQuestionStatus;
import org.swp.my_learning_path.entity.CourseQuestion;

import java.util.List;

@Repository
public interface CourseQuestionRepository extends JpaRepository<CourseQuestion, Long> {

    // Lấy danh sách câu hỏi theo bài học (xếp mới nhất lên đầu)
    List<CourseQuestion> findByLesson_LessonIdOrderByCreatedAtDesc(Long lessonId);

    // Lấy danh sách câu hỏi của một học viên
    List<CourseQuestion> findByStudent_UserIdOrderByCreatedAtDesc(Long studentId);

    // Lấy tất cả câu hỏi liên quan đến các khóa học của một Giảng viên
    @Query("SELECT q FROM CourseQuestion q " +
           "JOIN q.course c " +
           "WHERE c.instructor.userId = :instructorId " +
           "ORDER BY q.createdAt DESC")
    List<CourseQuestion> findAllByInstructorId(@Param("instructorId") Long instructorId);

    // Lấy câu hỏi theo trạng thái cho Giảng viên
    @Query("SELECT q FROM CourseQuestion q " +
           "JOIN q.course c " +
           "WHERE c.instructor.userId = :instructorId " +
           "AND q.status = :status " +
           "ORDER BY q.createdAt DESC")
    List<CourseQuestion> findByInstructorIdAndStatus(@Param("instructorId") Long instructorId,
                                                      @Param("status") EQuestionStatus status);
}
