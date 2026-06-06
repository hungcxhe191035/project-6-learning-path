package org.swp.my_learning_path.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.swp.my_learning_path.dto.response.LearningPathDetailDto;
import org.swp.my_learning_path.security.CustomUserDetails;
import org.swp.my_learning_path.service.LearningPathService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class LearningPathController {

    private final LearningPathService learningPathService;

    @GetMapping("/my-learning-paths")
    public String myLearningPaths(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            Model model) {
        model.addAttribute(
                "learningPaths",
                learningPathService.getMyLearningPaths(userDetails.getUserId())
        );

        return "pages/my-learning-path";
    }

    @GetMapping("/my-learning-paths/{pathId}")
    public String detail(
            @PathVariable Long pathId,
            @AuthenticationPrincipal CustomUserDetails user,
            Model model
    ) {

        LearningPathDetailDto path =
                learningPathService.getLearningPathDetail(
                        user.getUserId(),
                        pathId
                );

        model.addAttribute("path", path);

        if (!path.getCourses().isEmpty()) {

            model.addAttribute(
                    "selectedCourse",
                    path.getCourses().get(0)
            );
        }

        return "pages/learning-path-detail";
    }
}
