package org.swp.my_learning_path.repository;

import org.swp.my_learning_path.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findAllByOrderByTagNameAsc();

    Optional<Tag> findByTagName(String tagName);

    boolean existsByTagName(String tagName);

    boolean existsByTagNameAndTagIdNot(String tagName, Long tagId);

    @Modifying
    @Query(value = "DELETE FROM course_version_tag_mappings WHERE tag_id = :tagId", nativeQuery = true)
    void deleteCourseVersionTagMappingsByTagId(@Param("tagId") Long tagId);

    @Modifying
    @Query(value = "DELETE FROM application_tag_mappings WHERE tag_id = :tagId", nativeQuery = true)
    void deleteApplicationTagMappingsByTagId(@Param("tagId") Long tagId);
}
