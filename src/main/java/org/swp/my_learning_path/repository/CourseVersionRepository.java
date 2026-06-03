package org.swp.my_learning_path.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swp.my_learning_path.entity.CourseVersion;

@Repository
public interface CourseVersionRepository extends JpaRepository<CourseVersion, Long> {
}