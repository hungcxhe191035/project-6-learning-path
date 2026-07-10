package org.swp.my_learning_path.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swp.my_learning_path.entity.Certificate;
import org.swp.my_learning_path.entity.Enrollment;
import org.swp.my_learning_path.repository.CertificateRepository;
import org.swp.my_learning_path.repository.EnrollmentRepository;
import org.swp.my_learning_path.repository.LessonProgressRepository;
import org.swp.my_learning_path.repository.LessonRepository;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CertificateServiceImpl implements CertificateService {

    private final CertificateRepository certificateRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonRepository lessonRepository;
    private final LessonProgressRepository lessonProgressRepository;

    @Override
    public Certificate findById(Long id) {
        return certificateRepository.findById(id).orElse(null);
    }

    @Override
    public Certificate findCertificate(Long userId, Long courseId) {
        return certificateRepository
                .findCertificate(userId, courseId)
                .orElse(null);
    }

    /**
     * Kiểm tra nếu học viên đã hoàn thành toàn bộ bài học của course,
     * tự động insert Certificate vào DB (nếu chưa có).
     * @return Certificate đã có / vừa tạo, hoặc null nếu chưa học xong.
     */
    @Override
    @Transactional
    public Certificate issueCertificateIfCompleted(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment không tồn tại!"));

        // Lấy courseVersionId hiện tại của course đang học
        Long courseVersionId = enrollment.getCourse()
                .getCurrentPublishedVersion()
                .getCourseVersionId();

        // Đếm tổng số bài học của course
        long totalLessons = lessonRepository.countByCourseVersionId(courseVersionId);

        if (totalLessons == 0) {
            log.warn("Course version {} không có bài học nào!", courseVersionId);
            return null;
        }

        // Đếm số bài học đã hoàn thành trong enrollment này
        long completedLessons = lessonProgressRepository
                .countByEnrollmentAndIsCompleted(enrollment, true);

        if (completedLessons < totalLessons) {
            // Chưa học xong
            return null;
        }

        // Đã học xong — kiểm tra certificate đã tồn tại chưa
        Optional<Certificate> existing = certificateRepository
                .findCertificate(
                        enrollment.getStudent().getUserId(),
                        enrollment.getCourse().getCourseId());

        if (existing.isPresent()) {
            log.info("Certificate đã tồn tại cho enrollment {}", enrollmentId);
            return existing.get();
        }

        // Tạo mới certificate
        String code = "CERT-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
        Certificate certificate = Certificate.builder()
                .enrollment(enrollment)
                .certificateCode(code)
                .build();
        certificate = certificateRepository.save(certificate);

        log.info("Đã cấp certificate [{}] cho enrollment {}", code, enrollmentId);
        return certificate;
    }
}

