package com.swp391.final_project.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "quiz_questions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Slf4j
public class QuizQuestion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    Lesson lesson;

    @Column(name = "question_text", columnDefinition = "NVARCHAR(MAX)")
    String questionText;

    @Column(name = "display_order")
    Integer displayOrder;
}
