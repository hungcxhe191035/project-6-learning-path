package org.swp.my_learning_path.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.swp.my_learning_path.dto.response.LearnCourseDTO;
import org.swp.my_learning_path.security.CustomUserDetails;
import org.swp.my_learning_path.service.LearnService;

@Controller
@RequiredArgsConstructor
public class LearnController {

    private final LearnService learnService;

    // Trang học chính
    @GetMapping("/learn/{courseId}")
    public String learnPage(@PathVariable Long courseId,
                            @AuthenticationPrincipal CustomUserDetails userDetails,
                            Model model) {
        if (userDetails == null) return "redirect:/login";

        LearnCourseDTO data = learnService.getLearnData(courseId, userDetails.getUserId());
        model.addAttribute("course", data);
        return "pages/learn-course";
    }

    // Trang làm bài Quiz riêng
    @GetMapping("/learn/{courseId}/quiz/{lessonId}")
    public String quizPage(@PathVariable Long courseId,
                           @PathVariable Long lessonId,
                           @AuthenticationPrincipal CustomUserDetails userDetails,
                           Model model) {
        if (userDetails == null) return "redirect:/login";

        model.addAttribute("courseId", courseId);
        model.addAttribute("lessonId", lessonId);
        return "pages/quiz";
    }
    // API lấy chi tiết 1 bài học (title, videoUrl, articleContent)
    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<?> getLessonDetail(@PathVariable Long lessonId) {
        return ResponseEntity.ok(learnService.getLessonDetail(lessonId));
    }
}