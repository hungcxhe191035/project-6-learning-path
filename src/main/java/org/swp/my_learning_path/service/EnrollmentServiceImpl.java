package org.swp.my_learning_path.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.swp.my_learning_path.entity.Enrollment;
import org.swp.my_learning_path.repository.EnrollmentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    @Override
    public List<Enrollment> getStudentsByInstructor(Long instructorId, Long courseId) {
        return enrollmentRepository.findEnrollmentsByInstructorAndCourse(instructorId, courseId);
    }

    @Override
    public boolean isEnrolled(Long studentId, Long courseId) {
        return enrollmentRepository.existsByStudent_UserIdAndCourse_CourseId(studentId, courseId);
    }
}
