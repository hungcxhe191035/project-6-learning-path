package org.swp.my_learning_path.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Entity
@Table(name = "courses")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Slf4j
public class Course extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    Long courseId;

    @Column(name = "average_rating", precision = 3, scale = 2)
    BigDecimal averageRating = BigDecimal.ZERO;

    @Column(name = "total_reviews")
    Integer totalReviews = 0;

    @Column(name = "total_students")
    Integer totalStudents = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id")
    User instructor;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_published_version_id")
    CourseVersion currentPublishedVersion;
}
