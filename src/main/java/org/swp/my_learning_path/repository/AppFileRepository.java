package org.swp.my_learning_path.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swp.my_learning_path.entity.AppFile;

@Repository
public interface AppFileRepository extends JpaRepository<AppFile, Long> {
}