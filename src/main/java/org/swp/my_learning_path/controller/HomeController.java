package org.swp.my_learning_path.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping({"/home", "/"})
    public String homePage(Model model) {
        model.addAttribute(
                "pageTitle",
                "Trang chủ"
        );
        return "pages/home";
    }
}
