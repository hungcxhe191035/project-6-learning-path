package org.swp.my_learning_path.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.swp.my_learning_path.constant.EAccountStatus;
import org.swp.my_learning_path.constant.ERole;
import org.swp.my_learning_path.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByRoleAndDeleteFlagFalse(ERole role);

    java.util.List<User> findByRoleAndDeleteFlagFalse(ERole role);

    @Query("SELECT u FROM User u WHERE u.userId = :id AND u.deleteFlag = false")
    Optional<User> findByIdAndNotDeleted(@Param("id") Long id);

    @Query("SELECT u FROM User u WHERE u.deleteFlag = false AND " +
            "(:role IS NULL OR u.role = :role) AND " +
            "(:status IS NULL OR u.status = :status) AND " +
            "(:search IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> searchUsers(@Param("role") ERole role, @Param("status") EAccountStatus status, @Param("search") String search, Pageable pageable);
}
