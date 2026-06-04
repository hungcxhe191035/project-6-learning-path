package org.swp.my_learning_path.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.swp.my_learning_path.constant.ECourseStatus;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "course_versions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Slf4j
public class CourseVersion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_version_id")
    Long courseVersionId;

    @Column(name = "version_number", nullable = false)
    Integer versionNumber;

    @Column(name = "title", columnDefinition = "NVARCHAR(255)")
    String title;

    @Column(name = "subtitle", columnDefinition = "NVARCHAR(MAX)")
    String subtitle;

    @Column(name = "description",
            columnDefinition = "NVARCHAR(MAX)")
    String description;

    @Column(name = "price", precision = 18, scale = 2)
    BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    ECourseStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thumbnail_file_id")
    AppFile thumbnail;

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "course_version_tag_mappings",
            joinColumns = @JoinColumn(name = "course_version_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    Course course;
}