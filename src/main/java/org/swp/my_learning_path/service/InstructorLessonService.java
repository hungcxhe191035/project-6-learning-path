package org.swp.my_learning_path.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swp.my_learning_path.constant.ELessonType;
import org.swp.my_learning_path.dto.request.LessonRequest;
import org.swp.my_learning_path.entity.AppFile;
import org.swp.my_learning_path.entity.CourseSection;
import org.swp.my_learning_path.entity.Lesson;
import org.swp.my_learning_path.repository.AppFileRepository;
import org.swp.my_learning_path.repository.CourseSectionRepository;
import org.swp.my_learning_path.repository.LessonRepository;
// phase 3 cụm 3 liên quan đến các lession
@Service
@RequiredArgsConstructor
public class InstructorLessonService {

    private final LessonRepository lessonRepository;
    private final CourseSectionRepository sectionRepository;
    private final AppFileRepository appFileRepository;

    @Transactional
    public Long createLesson(Long sectionId, LessonRequest request) {
        // 1. Tìm xem cái Chương đó có tồn tại không
        CourseSection section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chương này!"));

        // 2. Tạo sườn bài học
        Lesson lesson = Lesson.builder()
                .section(section)
                .title(request.getTitle())
                .lessonType(request.getLessonType())
                .displayOrder(request.getDisplayOrder())
                .build();

        // 3. Đắp thịt tùy theo loại bài học
        if (request.getLessonType() == ELessonType.VIDEO) {
            if (request.getVideoFileId() != null) {
                AppFile video = appFileRepository.findById(request.getVideoFileId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy file video!"));
                lesson.setVideo(video);
            }
            lesson.setDurationSeconds(request.getDurationSeconds());
        } else if (request.getLessonType() == ELessonType.ARTICLE) {
            lesson.setArticleContent(request.getArticleContent());
        }
        // Riêng loại QUIZ thì cứ tạo cái sườn rỗng, mình sẽ dán câu hỏi vào sau ở Cụm API 4.

        lesson = lessonRepository.save(lesson);
        return lesson.getLessonId();
    }

    @Transactional
    public void updateLesson(Long lessonId, LessonRequest request) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài học này!"));

        lesson.setTitle(request.getTitle());
        lesson.setDisplayOrder(request.getDisplayOrder());

        if (lesson.getLessonType() == ELessonType.VIDEO) {
            if (request.getVideoFileId() != null) {
                AppFile video = appFileRepository.findById(request.getVideoFileId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy file video!"));
                lesson.setVideo(video);
            }
            lesson.setDurationSeconds(request.getDurationSeconds());
        } else if (lesson.getLessonType() == ELessonType.ARTICLE) {
            lesson.setArticleContent(request.getArticleContent());
        }

        lessonRepository.save(lesson);
    }

    @Transactional
    public void deleteLesson(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài học này!"));
        lessonRepository.delete(lesson);
    }
}