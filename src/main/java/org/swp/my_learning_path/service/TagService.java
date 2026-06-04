package org.swp.my_learning_path.service;

import org.swp.my_learning_path.dto.request.TagRequest;
import org.swp.my_learning_path.entity.Tag;

import java.util.List;

public interface TagService {
    List<Tag> getAllTags();
    Tag getTagById(Long id);
    Tag createTag(TagRequest request);
    Tag updateTag(Long id, TagRequest request);
    void deleteTag(Long id);
}
