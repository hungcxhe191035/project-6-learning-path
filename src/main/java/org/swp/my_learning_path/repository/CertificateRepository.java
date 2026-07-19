package org.swp.my_learning_path.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.swp.my_learning_path.entity.Certificate;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    @Query("""
        SELECT c
        FROM Certificate c
        WHERE c.enrollment.student.userId = :userId
          AND c.enrollment.course.courseId = :courseId
    """)
    Optional<Certificate> findCertificate(
            @Param("userId") Long userId,
            @Param("courseId") Long courseId);

    @Query("""
        SELECT c
        FROM Certificate c
        WHERE c.enrollment.student.userId = :userId
        ORDER BY c.createdAt DESC
    """)
    List<Certificate> findByUserId(@Param("userId") Long userId);
}
