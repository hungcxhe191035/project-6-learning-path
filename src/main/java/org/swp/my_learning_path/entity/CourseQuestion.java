package org.swp.my_learning_path.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.swp.my_learning_path.constant.EQuestionStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseQuestion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    Long questionId;

    @Column(name = "title", nullable = false)
    String title;

    @Column(name = "content", columnDefinition = "NVARCHAR(MAX)", nullable = false)
    String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    EQuestionStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    Lesson lesson;
}
