package org.swp.my_learning_path.service;

import org.swp.my_learning_path.constant.EApplicationStatus;
import org.swp.my_learning_path.constant.ERole;
import org.swp.my_learning_path.dto.request.ReviewApplicationRequest;
import org.swp.my_learning_path.dto.request.SubmitApplicationRequest;
import org.swp.my_learning_path.entity.InstructorApplication;
import org.swp.my_learning_path.entity.Tag;
import org.swp.my_learning_path.entity.User;
import org.swp.my_learning_path.repository.InstructorApplicationRepository;
import org.swp.my_learning_path.repository.TagRepository;
import org.swp.my_learning_path.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class InstructorApplicationServiceImpl implements InstructorApplicationService {

    private final InstructorApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final FileStorageService fileStorageService;
    private final NotificationService notificationService;
    private final EmailService emailService;

    @Override
    @Transactional
    public void submitApplication(Long userId, SubmitApplicationRequest request, MultipartFile cvFile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (user.getRole() == ERole.INSTRUCTOR) {
            throw new RuntimeException("Bạn đã là giảng viên, không cần nộp đơn.");
        }
        if (user.getRole() == ERole.ADMIN) {
            throw new RuntimeException("Tài khoản Admin không thể nộp đơn.");
        }

        Optional<InstructorApplication> latestApp = applicationRepository.findTopByUserOrderByCreatedAtDesc(user);
        boolean isUpdateInPlace = false;
        InstructorApplication applicationToUpdate = null;

        // Nếu có đơn PENDING → kiểm tra xem đã quá 10 ngày chưa
        if (latestApp.isPresent() && latestApp.get().getStatus() == EApplicationStatus.PENDING) {
            LocalDateTime tenDaysAgo = LocalDateTime.now().minusDays(10);
            if (latestApp.get().getCreatedAt().isAfter(tenDaysAgo)) {
                throw new RuntimeException("Bạn đang có một đơn đang chờ xét duyệt. Vui lòng chờ phản hồi.");
            } else {
                isUpdateInPlace = true;
                applicationToUpdate = latestApp.get();
            }
        }

        // Xử lý upload CV
        String cvFileName = null;
        String cvFilePath = null;
        boolean hasNewFile = cvFile != null && !cvFile.isEmpty();

        if (hasNewFile) {
            // Nếu tải lên file mới -> Tiến hành xóa file cũ trên đĩa
            latestApp.ifPresent(old -> {
                if ((old.getStatus() == EApplicationStatus.REJECTED || old.getStatus() == EApplicationStatus.PENDING) && old.getCvFilePath() != null) {
                    fileStorageService.deleteCvFile(old.getCvFilePath());
                }
            });

            String originalName = cvFile.getOriginalFilename();
            if (originalName != null && !originalName.toLowerCase().endsWith(".pdf")) {
                throw new RuntimeException("Chỉ chấp nhận file PDF cho CV.");
            }
            cvFilePath = fileStorageService.storeCvFile(cvFile);
            cvFileName = StringUtils.getFilename(originalName);
        } else {
            // Nếu không chọn file mới -> Giữ lại thông tin file cũ từ đơn bị từ chối hoặc đơn cũ đang PENDING
            if (latestApp.isPresent()) {
                InstructorApplication old = latestApp.get();
                if (old.getStatus() == EApplicationStatus.REJECTED || old.getStatus() == EApplicationStatus.PENDING) {
                    cvFileName = old.getCvFileName();
                    cvFilePath = old.getCvFilePath();
                }
            }
        }

        // Kiểm tra bắt buộc có CV
        if (cvFilePath == null) {
            throw new RuntimeException("Vui lòng tải lên CV của bạn.");
        }

        // Resolve tags từ tagIds
        if (request.getTagIds() == null || request.getTagIds().isEmpty()) {
            throw new RuntimeException("Vui lòng chọn ít nhất một lĩnh vực chuyên môn.");
        }
        Set<Tag> teachingTags = new HashSet<>();
        List<Tag> tags = tagRepository.findAllById(request.getTagIds());
        teachingTags.addAll(tags);

        if (isUpdateInPlace) {
            applicationToUpdate.setHeadline(request.getHeadline());
            applicationToUpdate.setBio(request.getBio());
            applicationToUpdate.setMotivation(request.getMotivation());
            applicationToUpdate.setLinkedinUrl(StringUtils.hasText(request.getLinkedinUrl()) ? request.getLinkedinUrl().trim() : null);
            applicationToUpdate.setCvFileName(cvFileName);
            applicationToUpdate.setCvFilePath(cvFilePath);
            applicationToUpdate.getTeachingTags().clear();
            applicationToUpdate.getTeachingTags().addAll(teachingTags);

            applicationRepository.save(applicationToUpdate);
            applicationRepository.updateCreatedAtAndResetReviewNote(applicationToUpdate.getApplicationId(), LocalDateTime.now());
        } else {
            InstructorApplication application = InstructorApplication.builder()
                    .user(user)
                    .headline(request.getHeadline())
                    .bio(request.getBio())
                    .motivation(request.getMotivation())
                    .linkedinUrl(StringUtils.hasText(request.getLinkedinUrl()) ? request.getLinkedinUrl().trim() : null)
                    .cvFileName(cvFileName)
                    .cvFilePath(cvFilePath)
                    .teachingTags(teachingTags)
                    .status(EApplicationStatus.PENDING)
                    .build();

            applicationRepository.save(application);
        }
    }

    @Override
    public Optional<InstructorApplication> getMyLatestApplication(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        return applicationRepository.findTopByUserOrderByCreatedAtDesc(user);
    }

    @Override
    public Page<InstructorApplication> getApplications(EApplicationStatus statusFilter, Pageable pageable) {
        if (statusFilter != null) {
            return applicationRepository.findByStatus(statusFilter, pageable);
        }
        return applicationRepository.findAll(pageable);
    }

    @Override
    public InstructorApplication getApplicationById(Long applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn với id: " + applicationId));
    }

    @Override
    @Transactional
    public void reviewApplication(Long applicationId, ReviewApplicationRequest request) {
        InstructorApplication application = getApplicationById(applicationId);

        if (application.getStatus() != EApplicationStatus.PENDING) {
            throw new RuntimeException("Đơn này đã được xử lý trước đó.");
        }
        if (request.getDecision() == EApplicationStatus.PENDING) {
            throw new RuntimeException("Quyết định không hợp lệ.");
        }
        if (request.getDecision() == EApplicationStatus.REJECTED &&
                (request.getReviewNote() == null || request.getReviewNote().trim().isEmpty())) {
            throw new RuntimeException("Vui lòng nhập lý do từ chối.");
        }

        application.setStatus(request.getDecision());
        application.setReviewNote(request.getReviewNote());

        User user = application.getUser();
        boolean isApproved = request.getDecision() == EApplicationStatus.APPROVED;

        if (isApproved) {
            user.setRole(ERole.INSTRUCTOR);
            userRepository.save(user);
        }

        applicationRepository.save(application);

        // 1. Gửi thông báo hệ thống (Notification)
        String title = "Kết quả duyệt đơn đăng ký Giảng viên";
        String content = isApproved 
                ? "Chúc mừng! Đơn đăng ký giảng viên của bạn đã được duyệt thành công. Bạn hiện đã là Giảng viên."
                : "Đơn đăng ký giảng viên của bạn đã bị từ chối. Lý do: " + request.getReviewNote();
        notificationService.sendNotification(user, title, content);

        // 2. Gửi email thông báo
        emailService.sendApplicationResultEmail(user.getEmail(), user.getFullName(), isApproved, request.getReviewNote());
    }

    @Override
    public long countPending() {
        return applicationRepository.countByStatus(EApplicationStatus.PENDING);
    }
}
