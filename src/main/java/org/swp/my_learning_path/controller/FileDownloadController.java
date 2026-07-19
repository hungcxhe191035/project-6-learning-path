package org.swp.my_learning_path.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.swp.my_learning_path.entity.InstructorApplication;
import org.swp.my_learning_path.entity.User;
import org.swp.my_learning_path.repository.UserRepository;
import org.swp.my_learning_path.security.CustomUserDetails;
import org.swp.my_learning_path.service.FileStorageService;
import org.swp.my_learning_path.service.InstructorApplicationService;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class FileDownloadController {

    private final FileStorageService fileStorageService;
    private final InstructorApplicationService applicationService;
    private final UserRepository userRepository;

    /**
     * Download CV file – ADMIN và chính chủ sở hữu CV (STUDENT/INSTRUCTOR) được truy cập.
     */
    @GetMapping("/files/cv/{filename:.+}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'INSTRUCTOR')")
    public ResponseEntity<Resource> downloadCv(
            @PathVariable String filename,
            @AuthenticationPrincipal UserDetails userDetails) {

        // Fallback if @AuthenticationPrincipal resolves to null
        if (userDetails == null) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetails) {
                userDetails = (UserDetails) principal;
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }

        // Kiểm tra bảo mật: Admin có toàn quyền. Student/Instructor chỉ được phép tải file của chính mình.
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            Long userId = null;
            if (userDetails instanceof CustomUserDetails) {
                userId = ((CustomUserDetails) userDetails).getUser().getUserId();
            } else {
                // Fallback to fetch from database if standard UserDetails is injected (e.g. in testing)
                Optional<User> userOpt = userRepository.findByEmail(userDetails.getUsername());
                if (userOpt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
                userId = userOpt.get().getUserId();
            }

            Optional<InstructorApplication> myApp = applicationService.getMyLatestApplication(userId);
            if (myApp.isEmpty() || !filename.equals(myApp.get().getCvFilePath())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        Resource resource = fileStorageService.loadCvAsResource(filename);

        // Lấy tên file gốc (sau phần UUID_)
        String displayName = filename;
        int underscoreIdx = filename.indexOf('_');
        if (underscoreIdx > 0 && underscoreIdx < filename.length() - 1) {
            displayName = filename.substring(underscoreIdx + 1);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + displayName + "\"")
                .body(resource);
    }
}
