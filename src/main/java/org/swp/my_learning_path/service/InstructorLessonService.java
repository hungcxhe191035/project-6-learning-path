package org.swp.my_learning_path.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.swp.my_learning_path.constant.EFilePurpose;
import org.swp.my_learning_path.constant.EFileType;
import org.swp.my_learning_path.constant.ELessonType;
import org.swp.my_learning_path.dto.request.LessonRequest;
import org.swp.my_learning_path.entity.AppFile;
import org.swp.my_learning_path.entity.CourseSection;
import org.swp.my_learning_path.entity.Lesson;
import org.swp.my_learning_path.repository.AppFileRepository;
import org.swp.my_learning_path.repository.CourseSectionRepository;
import org.swp.my_learning_path.repository.LessonRepository;

import java.io.IOException;

// phase 3 cụm 3 liên quan đến các lession
@Service
@RequiredArgsConstructor
public class InstructorLessonService {

    private final LessonRepository lessonRepository;
    private final CourseSectionRepository sectionRepository;
    private final AppFileRepository appFileRepository;
    private final S3Service s3Service; // Thêm S3Service vào đây để bơm video lên AWS
    private final org.swp.my_learning_path.repository.QuizQuestionRepository quizQuestionRepository;
    private final org.swp.my_learning_path.repository.QuizAnswerRepository quizAnswerRepository;

    @Transactional
    public Long createLesson(Long sectionId, LessonRequest request) {
        CourseSection section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chương này!"));

        Lesson lesson = Lesson.builder()
                .section(section)
                .title(request.getTitle())
                .lessonType(request.getLessonType())
                .displayOrder(request.getDisplayOrder())
                .build();

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

    // ================== CÁC HÀM MỚI (PHASE 6) ================== //

    @Transactional
    public String uploadLessonVideo(Long lessonId, MultipartFile file) throws IOException {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài học này!"));

        if (lesson.getLessonType() != ELessonType.VIDEO) {
            throw new RuntimeException("Bài học này không phải loại VIDEO!");
        }

        String fileUrl = s3Service.uploadFile(file);

        AppFile appFile = AppFile.builder()
                .fileName(file.getOriginalFilename())
                .fileUrl(fileUrl)
                .fileType(EFileType.VIDEO)
                .purpose(EFilePurpose.LESSON_VIDEO)
                .extension("mp4")
                .build();
        appFile = appFileRepository.save(appFile);

        lesson.setVideo(appFile);
        lessonRepository.save(lesson);

        return fileUrl;
    }

    @Transactional
    public void updateLessonArticle(Long lessonId, String htmlContent) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài học này!"));

        if (lesson.getLessonType() != ELessonType.ARTICLE) {
            throw new RuntimeException("Bài học này không phải loại ARTICLE (Bài viết)!");
        }

        lesson.setArticleContent(htmlContent);
        lessonRepository.save(lesson);
    }

    // Hàm Lấy toàn bộ thông tin bài giảng để hiển thị lại
    @Transactional(readOnly = true)
    public Lesson getLessonById(Long lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài học này!"));
    }

    @Transactional(readOnly = true)
    public java.util.List<org.swp.my_learning_path.entity.QuizQuestion> getQuizQuestionsByLesson(Lesson lesson) {
        return quizQuestionRepository.findByLessonOrderByDisplayOrderAsc(lesson);
    }

    @Transactional(readOnly = true)
    public java.util.List<org.swp.my_learning_path.entity.QuizAnswer> getQuizAnswersByQuestion(org.swp.my_learning_path.entity.QuizQuestion question) {
        return quizAnswerRepository.findByQuestionOrderByDisplayOrderAsc(question);
    }

    @Transactional
    public void saveQuizContent(Long lessonId, java.util.List<org.swp.my_learning_path.dto.request.QuizQuestionRequest> questionsRequest) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài học này!"));
// nêếu bài giảng không phải định dạng là quiz hoăặc vid thì không được tạo quiz
        if (lesson.getLessonType() != ELessonType.QUIZ && lesson.getLessonType() != ELessonType.VIDEO) {
            throw new RuntimeException("Bài học này không hỗ trợ lưu bài tập trắc nghiệm!");
        }

        // Xóa sạch câu hỏi cũ để lưu mới toàn bộ
        java.util.List<org.swp.my_learning_path.entity.QuizQuestion> oldQuestions = quizQuestionRepository.findByLessonOrderByDisplayOrderAsc(lesson);
        for (org.swp.my_learning_path.entity.QuizQuestion q : oldQuestions) {
            quizAnswerRepository.deleteByQuestion(q);
        }
        quizQuestionRepository.deleteByLesson(lesson);

        // Lưu câu hỏi mới
        if (questionsRequest != null) {
            for (org.swp.my_learning_path.dto.request.QuizQuestionRequest qReq : questionsRequest) {
                org.swp.my_learning_path.entity.QuizQuestion newQ = org.swp.my_learning_path.entity.QuizQuestion.builder()
                        .lesson(lesson)
                        .questionText(qReq.getQuestionText())
                        .displayOrder(qReq.getDisplayOrder())
                        .videoTimestampSeconds(qReq.getVideoTimestampSeconds())
                        .build();
                newQ = quizQuestionRepository.save(newQ);

                if (qReq.getAnswers() != null) {
                    for (org.swp.my_learning_path.dto.request.QuizAnswerRequest aReq : qReq.getAnswers()) {
                        org.swp.my_learning_path.entity.QuizAnswer newA = org.swp.my_learning_path.entity.QuizAnswer.builder()
                                .question(newQ)
                                .answerText(aReq.getAnswerText())
                                .isCorrect(aReq.getIsCorrect())
                                .displayOrder(aReq.getDisplayOrder())
                                .build();
                        quizAnswerRepository.save(newA);
                    }
                }
            }
        }
    }

    // Hàm Upload Ảnh độc lập (từ CKEditor) lên S3
    @Transactional
    public String uploadIndependentImage(MultipartFile file) throws IOException {
        String fileUrl = s3Service.uploadFile(file);

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        }

        AppFile appFile = AppFile.builder()
                .fileName(originalFilename)
                .fileUrl(fileUrl)
                .fileType(EFileType.IMAGE)
                .purpose(EFilePurpose.COURSE_MATERIAL)
                .extension(extension)
                .build();
        appFileRepository.save(appFile);

        return fileUrl;
    }
}