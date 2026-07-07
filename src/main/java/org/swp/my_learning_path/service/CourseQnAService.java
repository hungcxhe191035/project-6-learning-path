package org.swp.my_learning_path.service;

import org.swp.my_learning_path.constant.EQuestionStatus;
import org.swp.my_learning_path.dto.request.AnswerQuestionRequest;
import org.swp.my_learning_path.dto.request.AskQuestionRequest;
import org.swp.my_learning_path.dto.response.CourseQuestionDTO;
import org.swp.my_learning_path.dto.response.QuestionAnswerDTO;

import java.util.List;

public interface CourseQnAService {
    CourseQuestionDTO askQuestion(Long studentId, AskQuestionRequest request);

    QuestionAnswerDTO answerQuestion(Long senderId, AnswerQuestionRequest request);

    void closeQuestion(Long questionId, Long studentId);

    List<CourseQuestionDTO> getQuestionsByLesson(Long lessonId);

    List<CourseQuestionDTO> getInstructorQuestions(Long instructorId, EQuestionStatus status);
}
