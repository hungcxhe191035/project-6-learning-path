package org.swp.my_learning_path.service;





import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swp.my_learning_path.constant.ECourseStatus;
import org.swp.my_learning_path.dto.response.CourseCardDTO;
import org.swp.my_learning_path.entity.Course;
import org.swp.my_learning_path.entity.CourseVersion;
import org.swp.my_learning_path.repository.CourseRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CourseCardDTO> getTop5Courses() {

        // Lấy danh sách khoá học đã duyệt, không bị xoá
        List<Course> courses = courseRepository
                .findByDeleteFlagFalseAndCurrentPublishedVersion_StatusOrderByCreatedAtDesc(
                        ECourseStatus.APPROVED
                );

        // Giới hạn 5 khoá học đầu tiên
        // rồi chuyển từng Course sang CourseCardDTO
        return courses.stream()
                .limit(5)
                .map(this::chuyenDoiSangDTO)
                .toList();
    }

    // Hàm chuyển đổi từ Course (entity) → CourseCardDTO
    private CourseCardDTO chuyenDoiSangDTO(Course course) {

        CourseVersion phienBan = course.getCurrentPublishedVersion();

        // Lấy link ảnh nếu có, không thì để null
        String anhThumbnail = null;
        if (phienBan.getThumbnail() != null) {
            anhThumbnail = phienBan.getThumbnail().getFileUrl();
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
                .build();
    }

    @Override
    public List<CourseCardDTO> getCourses() {
        // Lấy danh sách khoá học đã duyệt, không bị xoá
        List<Course> courses = courseRepository
                .findByDeleteFlagFalseAndCurrentPublishedVersion_StatusOrderByCreatedAtDesc(
                        ECourseStatus.APPROVED
                );

        return courses.stream()
                .map(this::chuyenDoiSangDTO)
                .toList();
    }
}