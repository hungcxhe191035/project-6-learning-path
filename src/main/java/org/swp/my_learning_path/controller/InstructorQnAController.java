package org.swp.my_learning_path.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.swp.my_learning_path.constant.EQuestionStatus;
import org.swp.my_learning_path.dto.response.CourseQuestionDTO;
import org.swp.my_learning_path.security.CustomUserDetails;
import org.swp.my_learning_path.service.CourseQnAService;

import java.util.List;

@Controller
@RequestMapping("/instructor/qna")
@PreAuthorize("hasRole('INSTRUCTOR')")
@RequiredArgsConstructor
public class InstructorQnAController {

    private final CourseQnAService qnaService;

    @GetMapping
    public String viewQnADashboard(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String statusFilter,
            Model model) {

        EQuestionStatus status = null;
        if (statusFilter != null && !statusFilter.isBlank()) {
            try {
                status = EQuestionStatus.valueOf(statusFilter.toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }

        List<CourseQuestionDTO> questions = qnaService.getInstructorQuestions(userDetails.getUserId(), status);

        model.addAttribute("questions", questions);
        model.addAttribute("currentFilter", statusFilter);
        return "pages/instructor/qna-management";
    }
}
