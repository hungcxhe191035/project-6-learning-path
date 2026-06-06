package org.swp.my_learning_path.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.swp.my_learning_path.constant.ECourseStatus;
import org.swp.my_learning_path.entity.Course;
import org.swp.my_learning_path.entity.CourseSection;
import org.swp.my_learning_path.entity.CourseVersion;
import org.swp.my_learning_path.entity.Lesson;
import org.swp.my_learning_path.repository.CourseRepository;
import org.swp.my_learning_path.repository.CourseSectionRepository;
import org.swp.my_learning_path.repository.CourseVersionRepository;
import org.swp.my_learning_path.repository.LessonRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/instructor/courses")
@RequiredArgsConstructor
public class InstructorUIController {

    private final CourseRepository courseRepository;
    private final CourseVersionRepository courseVersionRepository;
    private final CourseSectionRepository courseSectionRepository;
    private final LessonRepository lessonRepository;

    @GetMapping
    public String showCourseList(Model model) {
        // Bước 1: Gọi bảo vệ Spring Security ra để lấy thông tin phiên đăng nhập hiện tại
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();

        // Bước 2: Bóc tách thông tin (ép kiểu về CustomUserDetails) để lôi cái ID thật của Giảng viên ra
        Long mockInstructorId = ((org.swp.my_learning_path.security.CustomUserDetails) auth.getPrincipal()).getUser().getUserId();

        List<Course> courses = courseRepository.findByInstructor_UserIdOrderByCreatedAtDesc(mockInstructorId);
        model.addAttribute("courses", courses);
        return "pages/instructor/course-list";
    }

    @GetMapping("/create")
    public String createCoursePage(Model model) {
        model.addAttribute("pageTitle", "Tạo khóa học mới");
        return "pages/instructor/course-wizard";
    }

    @GetMapping("/{courseId}/edit")
    public String editCoursePage(@PathVariable Long courseId, Model model) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học!"));

        // Lấy ra bản Nháp (DRAFT) của Khóa học này để hiển thị ra màn hình
        CourseVersion draftVersion = courseVersionRepository.findByCourse_CourseIdAndStatus(courseId, ECourseStatus.DRAFT)
                .orElse(null);

        // Khởi tạo sẵn 2 cái giỏ rỗng để đựng Chương và Bài giảng
        List<CourseSection> sections = new ArrayList<>();
        Map<Long, List<Lesson>> sectionLessonsMap = new HashMap<>();

        // Nếu có bản nháp thì bắt đầu lùng sục lấy Chương và Bài giảng của nó
        if (draftVersion != null) {
            // Lấy danh sách Chương (đã tự động sắp xếp theo DisplayOrder)
            sections = courseSectionRepository.findByCourseVersion_CourseVersionIdOrderByDisplayOrderAsc(draftVersion.getCourseVersionId());

            // Lặp qua từng Chương để bới ra danh sách Bài giảng con bên trong
            for (CourseSection section : sections) {
                List<Lesson> lessons = lessonRepository.findBySection_SectionIdOrderByDisplayOrderAsc(section.getSectionId());
                sectionLessonsMap.put(section.getSectionId(), lessons);
            }
        }

        model.addAttribute("pageTitle", "Chỉnh sửa khóa học");
        model.addAttribute("courseId", courseId);

        // Đẩy toàn bộ Dữ liệu (Bản nháp, Các Chương, Các Bài Giảng) ra Model để thằng HTML (Thymeleaf) nó đọc được
        model.addAttribute("draft", draftVersion);
        model.addAttribute("sections", sections);
        model.addAttribute("sectionLessonsMap", sectionLessonsMap);

        return "pages/instructor/course-wizard";
    }
}