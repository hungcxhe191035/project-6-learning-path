package org.swp.my_learning_path.service;

import org.swp.my_learning_path.constant.EApplicationStatus;
import org.swp.my_learning_path.dto.request.ReviewApplicationRequest;
import org.swp.my_learning_path.dto.request.SubmitApplicationRequest;
import org.swp.my_learning_path.entity.InstructorApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface InstructorApplicationService {

    /**
     * STUDENT nộp đơn xin trở thành INSTRUCTOR.
     * @param cvFile File CV PDF (có thể null nếu không upload)
     */
    void submitApplication(Long userId, SubmitApplicationRequest request, MultipartFile cvFile);

    Optional<InstructorApplication> getMyLatestApplication(Long userId);

    Page<InstructorApplication> getApplications(EApplicationStatus statusFilter, Pageable pageable);

    InstructorApplication getApplicationById(Long applicationId);

    void reviewApplication(Long applicationId, ReviewApplicationRequest request);

    long countPending();
}

