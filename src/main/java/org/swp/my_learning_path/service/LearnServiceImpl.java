package org.swp.my_learning_path.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swp.my_learning_path.constant.ECourseStatus;
import org.swp.my_learning_path.dto.response.*;
import org.swp.my_learning_path.entity.*;
import org.swp.my_learning_path.repository.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LearnServiceImpl implements LearnService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseSectionRepository courseSectionRepository;
    private final LessonRepository lessonRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final LessonCommentRepository lessonCommentRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizAnswerRepository quizAnswerRepository;
    private final CourseFeedbackRepository courseFeedbackRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public LearnCourseDTO getLearnData(Long courseId, Long studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khoá học!"));

        Enrollment enrollment = enrollmentRepository
                .findByStudent_UserIdAndCourse_CourseId(studentId, courseId)
                .orElseThrow(() -> new RuntimeException("Bạn chưa đăng ký khoá học này!"));

        CourseVersion version = course.getCurrentPublishedVersion();
        String thumbnailUrl = version.getThumbnail() != null ? version.getThumbnail().getFileUrl() : null;

        // Lấy tất cả tiến độ của học sinh trong enrollment này
        List<LessonProgress> allProgress = lessonProgressRepository.findByEnrollment(enrollment);
        Set<Long> completedLessonIds = allProgress.stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsCompleted()))
                .map(p -> p.getLesson().getLessonId())
                .collect(Collectors.toSet());

        // Lấy danh sách chương
        List<CourseSection> sections = courseSectionRepository
                .findByCourseVersion_CourseVersionIdOrderByDisplayOrderAsc(version.getCourseVersionId());

        // Đếm tổng số bài học để tính %
        int totalLessons = 0;
        int completedLessons = completedLessonIds.size();
        Long firstUnlockedId = null;

        List<SectionLearnDTO> sectionDTOs = new ArrayList<>();
        // Dùng biến để track bài trước có completed không (cho logic khóa)
        boolean previousCompleted = true; // bài đầu tiên luôn mở khóa

        for (CourseSection section : sections) {
            List<Lesson> lessons = lessonRepository
                    .findBySection_SectionIdOrderByDisplayOrderAsc(section.getSectionId());

            List<LessonLearnDTO> lessonDTOs = new ArrayList<>();
            long secCompleted = 0;

            for (Lesson lesson : lessons) {
                totalLessons++;
                boolean isCompleted = completedLessonIds.contains(lesson.getLessonId());
                boolean isLocked = !previousCompleted;

                String videoUrl = null;
                if (lesson.getVideo() != null) {
                    videoUrl = lesson.getVideo().getFileUrl();
                }

                LessonLearnDTO dto = LessonLearnDTO.builder()
                        .lessonId(lesson.getLessonId())
                        .title(lesson.getTitle())
                        .lessonType(lesson.getLessonType() != null ? lesson.getLessonType().name() : "VIDEO")
                        .displayOrder(lesson.getDisplayOrder())
                        .durationSeconds(lesson.getDurationSeconds())
                        .videoUrl(videoUrl)
                        .articleContent(lesson.getArticleContent())
                        .isCompleted(isCompleted)
                        .isLocked(isLocked)
                        .build();

                lessonDTOs.add(dto);

                if (isCompleted) secCompleted++;
                if (!isLocked && !isCompleted && firstUnlockedId == null) {
                    firstUnlockedId = lesson.getLessonId();
                }

                // Bài tiếp theo bị lock nếu bài này chưa hoàn thành
                previousCompleted = isCompleted;
            }

            sectionDTOs.add(SectionLearnDTO.builder()
                    .sectionId(section.getSectionId())
                    .title(section.getTitle())
                    .displayOrder(section.getDisplayOrder())
                    .lessons(lessonDTOs)
                    .completedCount(secCompleted)
                    .totalCount(lessons.size())
                    .build());
        }

        int progressPercent = totalLessons == 0 ? 0 : (int) ((completedLessons * 100.0) / totalLessons);

        return LearnCourseDTO.builder()
                .courseId(courseId)
                .title(version.getTitle())
                .thumbnailUrl(thumbnailUrl)
                .instructorName(course.getInstructor().getFullName())
                .sections(sectionDTOs)
                .progressPercent(progressPercent)
                .firstUnlockedLessonId(firstUnlockedId)
                .build();
    }

    @Override
    @Transactional
    public void completeLesson(Long lessonId, Long studentId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài học!"));

        Long courseId = lesson.getSection().getCourseVersion().getCourse().getCourseId();
        Enrollment enrollment = enrollmentRepository
                .findByStudent_UserIdAndCourse_CourseId(studentId, courseId)
                .orElseThrow(() -> new RuntimeException("Chưa đăng ký khoá học!"));

        Optional<LessonProgress> existing = lessonProgressRepository.findByEnrollmentAndLesson(enrollment, lesson);
        if (existing.isPresent()) {
            LessonProgress progress = existing.get();
            if (!Boolean.TRUE.equals(progress.getIsCompleted())) {
                progress.setIsCompleted(true);
                progress.setCompletedAt(LocalDateTime.now());
                lessonProgressRepository.save(progress);
            }
        } else {
            LessonProgress progress = LessonProgress.builder()
                    .enrollment(enrollment)
                    .lesson(lesson)
                    .isCompleted(true)
                    .completedAt(LocalDateTime.now())
                    .build();
            lessonProgressRepository.save(progress);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getQuizData(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài học!"));

        List<QuizQuestion> questions = quizQuestionRepository.findByLessonOrderByDisplayOrderAsc(lesson);
        List<Map<String, Object>> result = new ArrayList<>();

        for (QuizQuestion q : questions) {
            List<QuizAnswer> answers = quizAnswerRepository.findByQuestionOrderByDisplayOrderAsc(q);
            List<Map<String, Object>> answerList = answers.stream().map(a -> {
                Map<String, Object> aMap = new LinkedHashMap<>();
                aMap.put("answerId", a.getAnswerId());
                aMap.put("answerText", a.getAnswerText());
                aMap.put("displayOrder", a.getDisplayOrder());
                // KHÔNG trả về isCorrect cho học sinh!
                return aMap;
            }).collect(Collectors.toList());

            Map<String, Object> qMap = new LinkedHashMap<>();
            qMap.put("questionId", q.getQuestionId());
            qMap.put("questionText", q.getQuestionText());
            qMap.put("displayOrder", q.getDisplayOrder());
            qMap.put("answers", answerList);
            result.add(qMap);
        }
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> submitQuiz(Long lessonId, Long studentId, Map<Long, Long> answers) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài học!"));

        List<QuizQuestion> questions = quizQuestionRepository.findByLessonOrderByDisplayOrderAsc(lesson);

        int totalQuestions = questions.size();
        int correctCount = 0;

        // Kết quả chi tiết từng câu để hiển thị lại cho học sinh
        List<Map<String, Object>> questionResults = new ArrayList<>();

        for (QuizQuestion q : questions) {
            List<QuizAnswer> qAnswers = quizAnswerRepository.findByQuestionOrderByDisplayOrderAsc(q);
            Long correctAnswerId = qAnswers.stream()
                    .filter(a -> Boolean.TRUE.equals(a.getIsCorrect()))
                    .map(QuizAnswer::getAnswerId)
                    .findFirst().orElse(null);

            Long studentAnswer = answers.get(q.getQuestionId());
            boolean isCorrect = correctAnswerId != null && correctAnswerId.equals(studentAnswer);
            if (isCorrect) correctCount++;

            Map<String, Object> qResult = new LinkedHashMap<>();
            qResult.put("questionId", q.getQuestionId());
            qResult.put("questionText", q.getQuestionText());
            qResult.put("correctAnswerId", correctAnswerId);
            qResult.put("studentAnswerId", studentAnswer);
            qResult.put("isCorrect", isCorrect);
            // Gửi lại danh sách đáp án (có text) để hiển thị
            List<Map<String, Object>> aList = qAnswers.stream().map(a -> {
                Map<String, Object> aMap = new LinkedHashMap<>();
                aMap.put("answerId", a.getAnswerId());
                aMap.put("answerText", a.getAnswerText());
                return aMap;
            }).collect(Collectors.toList());
            qResult.put("answers", aList);
            questionResults.add(qResult);
        }

        int scorePercent = totalQuestions == 0 ? 0 : (int) ((correctCount * 100.0) / totalQuestions);
        boolean passed = scorePercent >= 80;

        // Nếu đạt >= 80% thì đánh dấu bài học này là hoàn thành
        if (passed) {
            completeLesson(lessonId, studentId);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalQuestions", totalQuestions);
        result.put("correctCount", correctCount);
        result.put("scorePercent", scorePercent);
        result.put("passed", passed);
        result.put("questionResults", questionResults);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonCommentDTO> getComments(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài học!"));

        List<LessonComment> rootComments = lessonCommentRepository
                .findByLessonAndParentCommentIsNullOrderByCreatedAtDesc(lesson);

        return rootComments.stream().map(c -> {
            List<LessonComment> replies = lessonCommentRepository.findByParentCommentOrderByCreatedAtAsc(c);
            List<LessonCommentDTO> replyDTOs = replies.stream().map(r -> LessonCommentDTO.builder()
                    .commentId(r.getCommentId())
                    .userName(r.getUser().getFullName())
                    .content(r.getContent())
                    .createdAt(r.getCreatedAt())
                    .replies(List.of())
                    .build()).collect(Collectors.toList());

            return LessonCommentDTO.builder()
                    .commentId(c.getCommentId())
                    .userName(c.getUser().getFullName())
                    .content(c.getContent())
                    .createdAt(c.getCreatedAt())
                    .replies(replyDTOs)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LessonCommentDTO addComment(Long lessonId, Long studentId, String content, Long parentCommentId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài học!"));
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));

        LessonComment parent = null;
        if (parentCommentId != null) {
            parent = lessonCommentRepository.findById(parentCommentId)
                    .orElse(null);
        }

        LessonComment comment = LessonComment.builder()
                .lesson(lesson)
                .user(user)
                .content(content)
                .parentComment(parent)
                .build();
        comment = lessonCommentRepository.save(comment);

        return LessonCommentDTO.builder()
                .commentId(comment.getCommentId())
                .userName(user.getFullName())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .replies(List.of())
                .build();
    }

    @Override
    @Transactional
    public void submitFeedback(Long courseId, Long studentId, int rating, String comment) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khoá học!"));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));

        Optional<CourseFeedback> existing = courseFeedbackRepository
                .findByCourse_CourseIdAndStudent_UserId(courseId, studentId);

        if (existing.isPresent()) {
            CourseFeedback fb = existing.get();
            fb.setRating(rating);
            fb.setComment(comment);
            courseFeedbackRepository.save(fb);
        } else {
            CourseFeedback fb = CourseFeedback.builder()
                    .course(course)
                    .student(student)
                    .rating(rating)
                    .comment(comment)
                    .build();
            courseFeedbackRepository.save(fb);
        }

        // Cập nhật lại averageRating và totalReviews trên Course
        // Cập nhật lại averageRating và totalReviews trên Course
        List<CourseFeedback> allFeedbacks = courseFeedbackRepository.findByCourse_CourseIdOrderByCreatedAtDesc(courseId);
        double avg = allFeedbacks.stream().mapToInt(CourseFeedback::getRating).average().orElse(0);

        // Chuyển đổi double sang BigDecimal, làm tròn 1 chữ số thập phân
        java.math.BigDecimal bdAvg = java.math.BigDecimal.valueOf(avg)
                .setScale(1, java.math.RoundingMode.HALF_UP);

        course.setAverageRating(bdAvg);
        course.setTotalReviews(allFeedbacks.size());
        courseRepository.save(course);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getLessonDetail(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài học!"));
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("lessonId", lesson.getLessonId());
        result.put("title", lesson.getTitle());
        result.put("lessonType", lesson.getLessonType() != null ? lesson.getLessonType().name() : "VIDEO");
        result.put("videoUrl", lesson.getVideo() != null ? lesson.getVideo().getFileUrl() : null);
        result.put("articleContent", lesson.getArticleContent());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getVideoTimestampQuizzes(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài học!"));

        List<QuizQuestion> questions = quizQuestionRepository.findByLessonOrderByDisplayOrderAsc(lesson);

        // Chỉ lấy câu hỏi có cấu hình timestamp (bỏ qua câu hỏi null)
        return questions.stream()
                .filter(q -> q.getVideoTimestampSeconds() != null)
                .map(q -> {
                    List<QuizAnswer> answers = quizAnswerRepository
                            .findByQuestionOrderByDisplayOrderAsc(q);

                    List<Map<String, Object>> answerList = answers.stream().map(a -> {
                        Map<String, Object> aMap = new LinkedHashMap<>();
                        aMap.put("answerId", a.getAnswerId());
                        aMap.put("answerText", a.getAnswerText());
                        aMap.put("isCorrect", a.getIsCorrect()); // <-- QUAN TRỌNG NHẤT LÀ DÒNG NÀY!
                        return aMap;
                    }).collect(Collectors.toList());

                    Map<String, Object> qMap = new LinkedHashMap<>();
                    qMap.put("questionId", q.getQuestionId());
                    qMap.put("questionText", q.getQuestionText());
                    qMap.put("videoTimestampSeconds", q.getVideoTimestampSeconds());
                    qMap.put("answers", answerList);
                    return qMap;
                })
                .collect(Collectors.toList());
    }
}