package org.swp.my_learning_path.service;

import org.swp.my_learning_path.dto.request.TagRequest;
import org.swp.my_learning_path.entity.Tag;
import org.swp.my_learning_path.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public List<Tag> getAllTags() {
        return tagRepository.findAllByOrderByTagNameAsc();
    }

    @Override
    public Tag getTagById(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tag với id: " + id));
    }

    @Override
    @Transactional
    public Tag createTag(TagRequest request) {
        if (tagRepository.existsByTagName(request.getTagName().trim())) {
            throw new RuntimeException("Tag \"" + request.getTagName() + "\" đã tồn tại.");
        }
        Tag tag = Tag.builder()
                .tagName(request.getTagName().trim())
                .description(request.getDescription())
                .build();
        tag.setDeleteFlag(false);
        return tagRepository.save(tag);
    }

    @Override
    @Transactional
    public Tag updateTag(Long id, TagRequest request) {
        Tag tag = getTagById(id);
        if (tagRepository.existsByTagNameAndTagIdNot(request.getTagName().trim(), id)) {
            throw new RuntimeException("Tag \"" + request.getTagName() + "\" đã tồn tại.");
        }
        tag.setTagName(request.getTagName().trim());
        tag.setDescription(request.getDescription());
        return tagRepository.save(tag);
    }

    @Override
    @Transactional
    public void deleteTag(Long id) {
        Tag tag = getTagById(id);
        tagRepository.delete(tag);
    }
}
