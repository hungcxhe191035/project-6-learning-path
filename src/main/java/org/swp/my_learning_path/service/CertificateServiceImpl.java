package org.swp.my_learning_path.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.swp.my_learning_path.entity.Certificate;
import org.swp.my_learning_path.repository.CertificateRepository;

@Service
@RequiredArgsConstructor
public class CertificateServiceImpl implements CertificateService{
    private final CertificateRepository certificateRepository;

    @Override
    public Certificate findById(Long id) {
        return certificateRepository.findById(id).orElse(null);
    }

    @Override
    public Certificate findCertificate(
            Long userId,
            Long courseId) {

        return certificateRepository
                .findCertificate(
                        userId,
                        courseId)
                .orElse(null);
    }
}
