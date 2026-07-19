package org.swp.my_learning_path.service;

import org.swp.my_learning_path.entity.Certificate;
import java.util.List;

public interface CertificateService {
    Certificate findById(Long id);
    Certificate findCertificate(Long userId, Long courseId);
    List<Certificate> getCertificatesByUserId(Long userId);

    /**
     * Kiểm tra xem học viên đã hoàn thành hết tất cả bài học chưa.
     * Nếu đã hoàn thành và chưa có chứng chỉ thì tự động tạo mới.
     * @return Certificate nếu vừa được cấp hoặc đã tồn tại, null nếu chưa xong.
     */
    Certificate issueCertificateIfCompleted(Long enrollmentId);
}
