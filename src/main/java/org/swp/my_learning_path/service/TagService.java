package org.swp.my_learning_path.service;

import org.swp.my_learning_path.dto.request.TagRequest;
import org.swp.my_learning_path.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TagService {
    List<Tag> getAllTags();
    Page<Tag> getTagsPaged(Pageable pageable);
    Tag getTagById(Long id);
    Tag createTag(TagRequest request);
    Tag updateTag(Long id, TagRequest request);
    void deleteTag(Long id);
}
