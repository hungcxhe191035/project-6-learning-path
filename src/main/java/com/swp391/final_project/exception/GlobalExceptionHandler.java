package com.swp391.final_project.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.security.access.AccessDeniedException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(AccessDeniedException ex, Model model) {
        log.warn("Access Denied: {}", ex.getMessage());
        model.addAttribute("pageTitle", "403 - Từ chối truy cập");
        return "pages/403";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNotFoundException(NoResourceFoundException ex, Model model) {
        log.warn("Resource Not Found: {}", ex.getMessage());
        model.addAttribute("pageTitle", "404 - Không tìm thấy trang");
        model.addAttribute("errorMessage", "Không tìm thấy trang hoặc tài nguyên yêu cầu: " + ex.getResourcePath());
        return "pages/error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        log.error("Unhandled Exception occurred: ", ex);
        model.addAttribute("pageTitle", "Lỗi Hệ Thống");
        model.addAttribute("errorMessage", ex.getMessage() != null ? ex.getMessage() : ex.toString());
        return "pages/error";
    }
}
