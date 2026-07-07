package org.swp.my_learning_path.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swp.my_learning_path.constant.EQuestionStatus;
import org.swp.my_learning_path.constant.ERole;
import org.swp.my_learning_path.dto.request.AnswerQuestionRequest;
import org.swp.my_learning_path.dto.request.AskQuestionRequest;
import org.swp.my_learning_path.dto.response.CourseQuestionDTO;
import org.swp.my_learning_path.dto.response.QuestionAnswerDTO;
import org.swp.my_learning_path.entity.*;
import org.swp.my_learning_path.repository.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseQnAServiceImpl implements CourseQnAService {

    private final CourseQuestionRepository questionRepository;
    private final QuestionAnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;

    @Override
    @Transactional
    public CourseQuestionDTO askQuestion(Long studentId, AskQuestionRequest request) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin học viên!"));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("Khóa học không tồn tại!"));

        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new IllegalArgumentException("Bài học không tồn tại!"));

        CourseQuestion question = CourseQuestion.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .status(EQuestionStatus.PENDING)
                .student(student)
                .course(course)
                .lesson(lesson)
                .build();

        CourseQuestion savedQuestion = questionRepository.save(question);
        return mapToQuestionDTO(savedQuestion);
    }

    @Override
    @Transactional
    public QuestionAnswerDTO answerQuestion(Long senderId, AnswerQuestionRequest request) {
        CourseQuestion question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("Câu hỏi không tồn tại!"));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin người trả lời!"));

        // Tự động cập nhật trạng thái câu hỏi thành ANSWERED khi giảng viên trả lời
        if (question.getStatus() == EQuestionStatus.PENDING) {
            question.setStatus(EQuestionStatus.ANSWERED);
            questionRepository.save(question);
        }

        QuestionAnswer answer = QuestionAnswer.builder()
                .question(question)
                .sender(sender)
                .content(request.getContent())
                .build();

        QuestionAnswer savedAnswer = answerRepository.save(answer);

        String roleStr = sender.getRole() == ERole.INSTRUCTOR ? "INSTRUCTOR" : "STUDENT";
        String avatarUrl = sender.getAvatar() != null ? sender.getAvatar().getFileUrl() : "/assets/images/default-avatar.png";

        return QuestionAnswerDTO.builder()
                .answerId(savedAnswer.getAnswerId())
                .senderId(sender.getUserId())
                .senderName(sender.getFullName())
                .senderAvatar(avatarUrl)
                .senderRole(roleStr)
                .content(savedAnswer.getContent())
                .createdAt(savedAnswer.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public void closeQuestion(Long questionId, Long studentId) {
        CourseQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Câu hỏi không tồn tại!"));

        if (!question.getStudent().getUserId().equals(studentId)) {
            throw new IllegalStateException("Bạn không có quyền đóng câu hỏi của người khác!");
        }

        question.setStatus(EQuestionStatus.CLOSED);
        questionRepository.save(question);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseQuestionDTO> getQuestionsByLesson(Long lessonId) {
        List<CourseQuestion> questions = questionRepository.findByLesson_LessonIdOrderByCreatedAtDesc(lessonId);
        return questions.stream().map(this::mapToQuestionDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseQuestionDTO> getInstructorQuestions(Long instructorId, EQuestionStatus status) {
        List<CourseQuestion> questions;
        if (status != null) {
            questions = questionRepository.findByInstructorIdAndStatus(instructorId, status);
        } else {
            questions = questionRepository.findAllByInstructorId(instructorId);
        }
        return questions.stream().map(this::mapToQuestionDTO).collect(Collectors.toList());
    }

    private CourseQuestionDTO mapToQuestionDTO(CourseQuestion q) {
        List<QuestionAnswer> answers = answerRepository.findByQuestion_QuestionIdOrderByCreatedAtAsc(q.getQuestionId());
        
        List<QuestionAnswerDTO> answerDTOs = answers.stream().map(ans -> {
            User s = ans.getSender();
            String roleStr = s.getRole() == ERole.INSTRUCTOR ? "INSTRUCTOR" : "STUDENT";
            String avatar = s.getAvatar() != null ? s.getAvatar().getFileUrl() : "/assets/images/default-avatar.png";
            
            return QuestionAnswerDTO.builder()
                    .answerId(ans.getAnswerId())
                    .senderId(s.getUserId())
                    .senderName(s.getFullName())
                    .senderAvatar(avatar)
                    .senderRole(roleStr)
                    .content(ans.getContent())
                    .createdAt(ans.getCreatedAt())
                    .build();
        }).collect(Collectors.toList());

        String studentAvatar = q.getStudent().getAvatar() != null 
                ? q.getStudent().getAvatar().getFileUrl() 
                : "/assets/images/default-avatar.png";

        return CourseQuestionDTO.builder()
                .questionId(q.getQuestionId())
                .studentId(q.getStudent().getUserId())
                .studentName(q.getStudent().getFullName())
                .studentAvatar(studentAvatar)
                .courseId(q.getCourse().getCourseId())
                .courseTitle(q.getCourse().getCurrentPublishedVersion() != null ? q.getCourse().getCurrentPublishedVersion().getTitle() : "Khóa học")
                .lessonId(q.getLesson().getLessonId())
                .lessonTitle(q.getLesson().getTitle())
                .title(q.getTitle())
                .content(q.getContent())
                .status(q.getStatus())
                .createdAt(q.getCreatedAt())
                .answers(answerDTOs)
                .build();
    }
}
