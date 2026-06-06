package org.swp.my_learning_path.repository;

import org.swp.my_learning_path.entity.LearningPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningPathRepository
        extends JpaRepository<LearningPath, Long> {

    List<LearningPath> findByUser_UserId(Long userId);

}
