package org.swp.my_learning_path.service;

import org.swp.my_learning_path.entity.Certificate;

public interface CertificateService {
    Certificate findById(Long id);
    Certificate findCertificate(Long userId, Long courseId);
}
