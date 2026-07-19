package org.swp.my_learning_path;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import org.swp.my_learning_path.constant.EAccountStatus;
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
import org.swp.my_learning_path.service.EmailService;
import org.swp.my_learning_path.service.InstructorApplicationService;
import org.swp.my_learning_path.service.NotificationService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@SpringBootTest
class InstructorApplicationServiceTest {

    @Autowired
    private InstructorApplicationService applicationService;

    @Autowired
    private InstructorApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @MockBean
    private EmailService emailService;

    @MockBean
    private NotificationService notificationService;

    @Test
    @Transactional
    void testReviewApplication_Approved() {
        // Given: Create and save a student user
        User user = User.builder()
                .email("candidate.test.approve@gmail.com")
                .password("password123")
                .fullName("Nguyễn Văn A")
                .role(ERole.STUDENT)
                .status(EAccountStatus.ACTIVE)
                .build();
        user = userRepository.save(user);

        // Given: Create and save a PENDING instructor application for the student
        InstructorApplication application = InstructorApplication.builder()
                .user(user)
                .headline("Java Expert")
                .bio("5 years experience")
                .motivation("Want to share knowledge")
                .cvFileName("my_cv.pdf")
                .cvFilePath("uploads/cv/my_cv.pdf")
                .status(EApplicationStatus.PENDING)
                .build();
        application = applicationRepository.save(application);

        // When: Admin approves the application
        ReviewApplicationRequest request = new ReviewApplicationRequest();
        request.setDecision(EApplicationStatus.APPROVED);
        request.setReviewNote("Hồ sơ rất tốt");

        applicationService.reviewApplication(application.getApplicationId(), request);

        // Then: User role is updated to INSTRUCTOR
        User updatedUser = userRepository.findById(user.getUserId()).orElseThrow();
        assertEquals(ERole.INSTRUCTOR, updatedUser.getRole());

        // Then: Application status is updated to APPROVED
        InstructorApplication updatedApp = applicationRepository.findById(application.getApplicationId()).orElseThrow();
        assertEquals(EApplicationStatus.APPROVED, updatedApp.getStatus());
        assertEquals("Hồ sơ rất tốt", updatedApp.getReviewNote());

        // Then: System Web Notification is triggered with congratulations message
        verify(notificationService).sendNotification(
                Mockito.argThat(u -> u.getUserId().equals(updatedUser.getUserId())),
                Mockito.eq("Kết quả duyệt đơn đăng ký Giảng viên"),
                Mockito.eq("Chúc mừng! Đơn đăng ký giảng viên của bạn đã được duyệt thành công. Bạn hiện đã là Giảng viên.")
        );

        // Then: Email notification is triggered with congratulations message
        verify(emailService).sendApplicationResultEmail(
                Mockito.eq("candidate.test.approve@gmail.com"),
                Mockito.eq("Nguyễn Văn A"),
                Mockito.eq(true),
                Mockito.eq("Hồ sơ rất tốt")
        );
    }

    @Test
    @Transactional
    void testReviewApplication_Rejected() {
        // Given: Create and save a student user
        User user = User.builder()
                .email("candidate.test.reject@gmail.com")
                .password("password123")
                .fullName("Nguyễn Văn B")
                .role(ERole.STUDENT)
                .status(EAccountStatus.ACTIVE)
                .build();
        user = userRepository.save(user);

        // Given: Create and save a PENDING instructor application for the student
        InstructorApplication application = InstructorApplication.builder()
                .user(user)
                .headline("Java Beginner")
                .bio("No experience")
                .motivation("Want to learn")
                .cvFileName("my_cv.pdf")
                .cvFilePath("uploads/cv/my_cv.pdf")
                .status(EApplicationStatus.PENDING)
                .build();
        application = applicationRepository.save(application);

        // When: Admin rejects the application
        ReviewApplicationRequest request = new ReviewApplicationRequest();
        request.setDecision(EApplicationStatus.REJECTED);
        request.setReviewNote("Thiếu kinh nghiệm giảng dạy");

        applicationService.reviewApplication(application.getApplicationId(), request);

        // Then: User role remains STUDENT
        User updatedUser = userRepository.findById(user.getUserId()).orElseThrow();
        assertEquals(ERole.STUDENT, updatedUser.getRole());

        // Then: Application status is updated to REJECTED
        InstructorApplication updatedApp = applicationRepository.findById(application.getApplicationId()).orElseThrow();
        assertEquals(EApplicationStatus.REJECTED, updatedApp.getStatus());
        assertEquals("Thiếu kinh nghiệm giảng dạy", updatedApp.getReviewNote());

        // Then: System Web Notification is triggered with rejection message and reason
        verify(notificationService).sendNotification(
                Mockito.argThat(u -> u.getUserId().equals(updatedUser.getUserId())),
                Mockito.eq("Kết quả duyệt đơn đăng ký Giảng viên"),
                Mockito.eq("Đơn đăng ký giảng viên của bạn đã bị từ chối. Lý do: Thiếu kinh nghiệm giảng dạy")
        );

        // Then: Email notification is triggered with rejection status and reason
        verify(emailService).sendApplicationResultEmail(
                Mockito.eq("candidate.test.reject@gmail.com"),
                Mockito.eq("Nguyễn Văn B"),
                Mockito.eq(false),
                Mockito.eq("Thiếu kinh nghiệm giảng dạy")
        );
    }

    private Tag getOrCreateTag(String tagName) {
        return tagRepository.findByTagName(tagName).orElseGet(() -> {
            Tag tag = Tag.builder().tagName(tagName).build();
            return tagRepository.save(tag);
        });
    }

    @Test
    @Transactional
    void testSubmitApplication_PendingUnder10Days_ThrowsException() {
        // Given: Create a user and a tag
        User user = User.builder()
                .email("candidate.test.pending1@gmail.com")
                .password("password123")
                .fullName("Nguyễn Văn C")
                .role(ERole.STUDENT)
                .status(EAccountStatus.ACTIVE)
                .build();
        user = userRepository.save(user);

        Tag tag = getOrCreateTag("Java Core");

        // Save a pending application created just now
        InstructorApplication application = InstructorApplication.builder()
                .user(user)
                .headline("Java Beginner")
                .bio("No experience")
                .motivation("Want to learn")
                .cvFileName("my_cv.pdf")
                .cvFilePath("uploads/cv/my_cv.pdf")
                .status(EApplicationStatus.PENDING)
                .build();
        application = applicationRepository.save(application);

        // When/Then: Submitting again should throw an exception because < 10 days
        SubmitApplicationRequest request = new SubmitApplicationRequest();
        request.setHeadline("Java Updated");
        request.setBio("Updated bio");
        request.setMotivation("Updated motivation");
        request.setTagIds(List.of(tag.getTagId()));

        final Long userId = user.getUserId();
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            applicationService.submitApplication(userId, request, null);
        });

        assertEquals("Bạn đang có một đơn đang chờ xét duyệt. Vui lòng chờ phản hồi.", exception.getMessage());
    }

    @Test
    @Transactional
    void testSubmitApplication_PendingOver10Days_SuccessAndUpdatesInPlace() {
        // Given: Create a user and a tag
        User user = User.builder()
                .email("candidate.test.pending2@gmail.com")
                .password("password123")
                .fullName("Nguyễn Văn D")
                .role(ERole.STUDENT)
                .status(EAccountStatus.ACTIVE)
                .build();
        user = userRepository.save(user);

        Tag tag = getOrCreateTag("Spring Boot");

        // Save a pending application
        InstructorApplication application = InstructorApplication.builder()
                .user(user)
                .headline("Java Beginner")
                .bio("No experience")
                .motivation("Want to learn")
                .cvFileName("my_cv.pdf")
                .cvFilePath("uploads/cv/my_cv.pdf")
                .status(EApplicationStatus.PENDING)
                .build();
        application = applicationRepository.save(application);

        // Update its createdAt to 11 days ago
        LocalDateTime elevenDaysAgo = LocalDateTime.now().minusDays(11);
        applicationRepository.updateCreatedAtAndResetReviewNote(application.getApplicationId(), elevenDaysAgo);

        // When: Submitting again (over 10 days) should succeed and update in-place
        SubmitApplicationRequest request = new SubmitApplicationRequest();
        request.setHeadline("Java Senior");
        request.setBio("10 years experience");
        request.setMotivation("Want to teach advanced topics");
        request.setTagIds(List.of(tag.getTagId()));

        applicationService.submitApplication(user.getUserId(), request, null);

        // Then: The application was updated in-place (ID remains the same)
        InstructorApplication updatedApp = applicationRepository.findById(application.getApplicationId()).orElseThrow();
        assertEquals("Java Senior", updatedApp.getHeadline());
        assertEquals("10 years experience", updatedApp.getBio());
        assertEquals("Want to teach advanced topics", updatedApp.getMotivation());
        assertEquals(EApplicationStatus.PENDING, updatedApp.getStatus());
        assertNull(updatedApp.getReviewNote());

        // Then: The createdAt should be updated to now (after the 11 days ago time)
        assertTrue(updatedApp.getCreatedAt().isAfter(elevenDaysAgo));
    }
}
