package org.swp.my_learning_path.service;

import org.swp.my_learning_path.dto.request.TagRequest;
import org.swp.my_learning_path.entity.Tag;
import org.swp.my_learning_path.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public List<Tag> getAllTags() {
        return tagRepository.findByDeleteFlagFalseOrderByTagNameAsc();
    }

    @Override
    public Page<Tag> getTagsPaged(Pageable pageable) {
        return tagRepository.findByDeleteFlagFalse(pageable);
    }

    @Override
    public Tag getTagById(Long id) {
        return tagRepository.findById(id)
                .filter(t -> !t.isDeleteFlag())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tag với id: " + id));
    }

    @Override
    @Transactional
    public Tag createTag(TagRequest request) {
        String name = request.getTagName().trim();
        Optional<Tag> existingOpt = tagRepository.findByTagName(name);
        if (existingOpt.isPresent()) {
            Tag existing = existingOpt.get();
            if (!existing.isDeleteFlag()) {
                throw new RuntimeException("Tag \"" + name + "\" đã tồn tại.");
            }
            // Reactivate soft-deleted tag
            existing.setDeleteFlag(false);
            existing.setDescription(request.getDescription());
            return tagRepository.save(existing);
        }
        Tag tag = Tag.builder()
                .tagName(name)
                .description(request.getDescription())
                .build();
        tag.setDeleteFlag(false);
        return tagRepository.save(tag);
    }

    @Override
    @Transactional
    public Tag updateTag(Long id, TagRequest request) {
        Tag tag = getTagById(id);
        String name = request.getTagName().trim();
        Optional<Tag> duplicateOpt = tagRepository.findByTagName(name);
        if (duplicateOpt.isPresent()) {
            Tag duplicate = duplicateOpt.get();
            if (!duplicate.getTagId().equals(id) && !duplicate.isDeleteFlag()) {
                throw new RuntimeException("Tag \"" + name + "\" đã tồn tại.");
            }
        }
        tag.setTagName(name);
        tag.setDescription(request.getDescription());
        return tagRepository.save(tag);
    }

    @Override
    @Transactional
    public void deleteTag(Long id) {
        Tag tag = getTagById(id);
        tag.setDeleteFlag(true);
        tagRepository.deleteCourseVersionTagMappingsByTagId(id);
        tagRepository.deleteApplicationTagMappingsByTagId(id);
        tagRepository.save(tag);
    }
}
