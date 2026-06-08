package org.swp.my_learning_path.entity;

import org.swp.my_learning_path.constant.EApplicationStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "instructor_applications")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class InstructorApplication extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    Long applicationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(name = "headline", columnDefinition = "NVARCHAR(255)", nullable = false)
    String headline;

    @Column(name = "bio", columnDefinition = "NVARCHAR(MAX)", nullable = false)
    String bio;

    @Column(name = "motivation", columnDefinition = "NVARCHAR(MAX)", nullable = false)
    String motivation;

    @Column(name = "linkedin_url", length = 500)
    String linkedinUrl;

    @Column(name = "cv_file_name", columnDefinition = "NVARCHAR(255)")
    String cvFileName;

    @Column(name = "cv_file_path", length = 500)
    String cvFilePath;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "application_tag_mappings",
            joinColumns = @JoinColumn(name = "application_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    java.util.Set<Tag> teachingTags = new java.util.HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    @Builder.Default
    EApplicationStatus status = EApplicationStatus.PENDING;

    @Column(name = "review_note", columnDefinition = "NVARCHAR(MAX)")
    String reviewNote;
}
