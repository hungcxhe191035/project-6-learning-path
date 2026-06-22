package org.swp.my_learning_path.service;

import org.swp.my_learning_path.constant.EAccountStatus;
import org.swp.my_learning_path.constant.ERole;
import org.swp.my_learning_path.constant.ECourseStatus;
import org.swp.my_learning_path.dto.request.CreateUserRequest;
import org.swp.my_learning_path.entity.User;
import org.swp.my_learning_path.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminService {
    Page<User> searchUsers(ERole role, EAccountStatus status, String search, Pageable pageable);
    User getUserById(Long userId);
    void toggleUserStatus(Long userId);
    void createUser(CreateUserRequest request);
    void assignRole(Long userId, ERole role);
    void bulkLockUsers(List<Long> userIds, String adminEmail);
    void bulkUnlockUsers(List<Long> userIds, String adminEmail);

    // Quản lý Khóa học
    Page<Course> searchCourses(ECourseStatus status, Boolean blocked, String search, Pageable pageable);
    void blockCourse(Long courseId, String reason);
    void unblockCourse(Long courseId);
    void deleteCourse(Long courseId);
}
