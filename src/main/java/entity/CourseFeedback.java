package entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(
        name = "course_feedbacks",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_COURSE_FEEDBACK_STUDENT_COURSE",
                        columnNames = {"course_id", "student_id"}
                )
        }
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Slf4j
public class CourseFeedback extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    Long feedbackId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    User student;

    @Min(1)
    @Max(5)
    @Column(name = "rating", nullable = false)
    Integer rating;

    @Column(
            name = "comment",
            columnDefinition = "NVARCHAR(MAX)"
    )
    String comment;
}
