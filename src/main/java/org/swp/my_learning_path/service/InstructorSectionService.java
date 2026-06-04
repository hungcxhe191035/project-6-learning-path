package org.swp.my_learning_path.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swp.my_learning_path.constant.ECourseStatus;
import org.swp.my_learning_path.dto.request.SectionRequest;
import org.swp.my_learning_path.entity.CourseSection;
import org.swp.my_learning_path.entity.CourseVersion;
import org.swp.my_learning_path.repository.CourseSectionRepository;
import org.swp.my_learning_path.repository.CourseVersionRepository;
// thuộc phase 3 cụm 2, crud chương
//Chứa logic Thêm, Sửa, Xóa Bài học
@Service
@RequiredArgsConstructor
public class InstructorSectionService {

    private final CourseSectionRepository sectionRepository;
    private final CourseVersionRepository courseVersionRepository;

    @Transactional
    public Long createSection(Long courseId, SectionRequest request) {
        // 1. Tìm bản nháp của khóa học
        CourseVersion version = courseVersionRepository.findByCourse_CourseIdAndStatus(courseId, ECourseStatus.DRAFT)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bản nháp của khóa học này!"));

        // 2. Tạo Chương mới
        CourseSection section = CourseSection.builder()
                .courseVersion(version)
                .title(request.getTitle())
                .displayOrder(request.getDisplayOrder())
                .build();

        section = sectionRepository.save(section);
        return section.getSectionId(); // Trả về ID để Postman báo cáo
    }

    @Transactional
    public void updateSection(Long sectionId, SectionRequest request) {
        CourseSection section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chương này!"));

        section.setTitle(request.getTitle());
        section.setDisplayOrder(request.getDisplayOrder());
        sectionRepository.save(section);
    }

    @Transactional
    public void deleteSection(Long sectionId) {
        CourseSection section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chương này!"));
        sectionRepository.delete(section);
    }
}