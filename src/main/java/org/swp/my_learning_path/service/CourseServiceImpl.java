package org.swp.my_learning_path.service;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.swp.my_learning_path.constant.ECourseStatus;
import org.swp.my_learning_path.dto.response.CourseCardDTO;
import org.swp.my_learning_path.dto.response.CourseDetailDTO;
import org.swp.my_learning_path.dto.response.FeedbackDTO;
import org.swp.my_learning_path.dto.response.LessonDTO;
import org.swp.my_learning_path.dto.response.SectionDTO;
import org.swp.my_learning_path.entity.Course;
import org.swp.my_learning_path.entity.CourseFeedback;
import org.swp.my_learning_path.entity.CourseSection;
import org.swp.my_learning_path.entity.CourseVersion;
import org.swp.my_learning_path.entity.Lesson;
import org.swp.my_learning_path.repository.CourseFeedbackRepository;
import org.swp.my_learning_path.repository.CourseRepository;
import org.swp.my_learning_path.repository.CourseSectionRepository;
import org.swp.my_learning_path.repository.LessonRepository;
import org.swp.my_learning_path.repository.EnrollmentRepository;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseSectionRepository courseSectionRepository;
    private final LessonRepository lessonRepository;
    private final CourseFeedbackRepository courseFeedbackRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CourseCardDTO> getTop5Courses(Long studentId) {
        // Lấy danh sách khoá học đã duyệt, không bị xoá
        List<Course> courses = courseRepository
                .findByDeleteFlagFalseAndCurrentPublishedVersion_StatusOrderByCreatedAtDesc(
                        ECourseStatus.APPROVED
                );

        // Giới hạn 5 khoá học đầu tiên
        // rồi chuyển từng Course sang CourseCardDTO
        return courses.stream()
                .limit(5)
                .map(course -> chuyenDoiSangDTO(course, studentId))
                .toList();
    }

    @Override
    public List<CourseCardDTO> getCourses(Long studentId) {
        // Lấy danh sách khoá học đã duyệt, không bị xoá
        List<Course> courses = courseRepository
                .findByDeleteFlagFalseAndCurrentPublishedVersion_StatusOrderByCreatedAtDesc(
                        ECourseStatus.APPROVED
                );

        return courses.stream()
                .map(course -> chuyenDoiSangDTO(course, studentId))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CourseDetailDTO getCourseDetail(Long courseId) {

        // 1. Lấy khoá học từ DB
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khoá học!"));

        CourseVersion version = course.getCurrentPublishedVersion();

        // 2. Lấy URL ảnh thumbnail
        String thumbnailUrl = null;
        if (version.getThumbnail() != null) {
            thumbnailUrl = version.getThumbnail().getFileUrl();
        }

        // 3. Lấy danh sách phần học (sections) theo thứ tự
        List<CourseSection> danhSachPhan = courseSectionRepository
                .findByCourseVersionCourseVersionIdOrderByDisplayOrderAsc(
                        version.getCourseVersionId()
                );

        // 4. Với từng phần, lấy danh sách bài học (lessons)
        List<SectionDTO> sectionDTOs = new ArrayList<>();
        for (CourseSection phan : danhSachPhan) {

            List<Lesson> danhSachBaiHoc = lessonRepository
                    .findBySection_SectionIdOrderByDisplayOrderAsc(phan.getSectionId());

            List<LessonDTO> lessonDTOs = danhSachBaiHoc.stream()
                    .map(baiHoc -> LessonDTO.builder()
                            .lessonId(baiHoc.getLessonId())
                            .title(baiHoc.getTitle())
                            .lessonType(baiHoc.getLessonType() != null
                                    ? baiHoc.getLessonType().name() : "VIDEO")
                            .durationSeconds(baiHoc.getDurationSeconds())
                            .displayOrder(baiHoc.getDisplayOrder())
                            .build())
                    .toList();

            sectionDTOs.add(SectionDTO.builder()
                    .sectionId(phan.getSectionId())
                    .title(phan.getTitle())
                    .displayOrder(phan.getDisplayOrder())
                    .lessons(lessonDTOs)
                    .build());
        }

        // 5. Lấy 4 đánh giá mới nhất
        List<CourseFeedback> danhSachDanhGia = courseFeedbackRepository
                .findByCourse_CourseIdOrderByCreatedAtDesc(courseId);

        List<FeedbackDTO> feedbackDTOs = danhSachDanhGia.stream()
                .limit(4)
                .map(danhGia -> FeedbackDTO.builder()
                        .studentName(danhGia.getStudent().getFullName())
                        .rating(danhGia.getRating())
                        .comment(danhGia.getComment())
                        .createdAt(danhGia.getCreatedAt())
                        .build())
                .toList();

        // 6. Trả về CourseDetailDTO
        return CourseDetailDTO.builder()
                .courseId(course.getCourseId())
                .title(version.getTitle())
                .subtitle(version.getSubtitle())
                .description(version.getDescription())
                .price(version.getPrice())
                .thumbnailUrl(thumbnailUrl)
                .averageRating(course.getAverageRating())
                .totalReviews(course.getTotalReviews())
                .totalStudents(course.getTotalStudents())
                .instructorName(course.getInstructor().getFullName())
                .courseVersionId(version.getCourseVersionId())
                .sections(sectionDTOs)
                .feedbacks(feedbackDTOs)
                .build();
    }

    // Hàm chuyển đổi từ Course (entity) → CourseCardDTO
    private CourseCardDTO chuyenDoiSangDTO(Course course, Long studentId) {

        CourseVersion phienBan = course.getCurrentPublishedVersion();

        // Lấy link ảnh nếu có, không thì để null
        String anhThumbnail = null;
        if (phienBan.getThumbnail() != null) {
            anhThumbnail = phienBan.getThumbnail().getFileUrl();
        }

        boolean isEnrolled = false;
        if (studentId != null) {
            isEnrolled = enrollmentRepository.existsByStudent_UserIdAndCourse_CourseId(studentId, course.getCourseId());
        }

        // Trả về DTO với các thông tin cần hiển thị
        return CourseCardDTO.builder()
                .courseId(course.getCourseId())
                .title(phienBan.getTitle())
                .subtitle(phienBan.getSubtitle())
                .instructorName(course.getInstructor().getFullName())
                .price(phienBan.getPrice())
                .averageRating(course.getAverageRating())
                .totalReviews(course.getTotalReviews())
                .thumbnailUrl(anhThumbnail)
                .isEnrolled(isEnrolled)
                .build();
    }
}