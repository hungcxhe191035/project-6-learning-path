package com.swp391.final_project.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "quiz_answers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Slf4j
public class QuizAnswer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long answerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    QuizQuestion question;

    @Column(name = "answer_text", columnDefinition = "NVARCHAR(MAX)")
    String answerText;

    @Column(name = "is_correct")
    Boolean isCorrect;

    @Column(name = "display_order")
    Integer displayOrder;
}
