package org.swp.my_learning_path.repository;

import org.swp.my_learning_path.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findAllByOrderByTagNameAsc();

    Optional<Tag> findByTagName(String tagName);

    boolean existsByTagName(String tagName);

    boolean existsByTagNameAndTagIdNot(String tagName, Long tagId);
}
