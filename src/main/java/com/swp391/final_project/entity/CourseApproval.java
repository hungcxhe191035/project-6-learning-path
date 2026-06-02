package com.swp391.final_project.entity;

import com.swp391.final_project.constant.ECourseStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "course_approvals")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class CourseApproval extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "approval_id")
    Long approvalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_version_id", nullable = false)
    CourseVersion courseVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    User admin;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    ECourseStatus status;

    @Column(
            name = "comment",
            columnDefinition = "NVARCHAR(MAX)"
    )
    String comment;
}
