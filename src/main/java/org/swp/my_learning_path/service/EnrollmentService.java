package org.swp.my_learning_path.service;
public interface EnrollmentService {
    boolean isEnrolled(Long studentId, Long courseId);
}