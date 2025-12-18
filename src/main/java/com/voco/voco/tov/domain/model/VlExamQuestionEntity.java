package com.voco.voco.tov.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.voco.voco.tov.domain.model.enums.VlQuestionType;
import com.voco.voco.tov.infrastructure.persistence.converter.VlQuestionTypeConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@SQLRestriction("deleted_at is NULL")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "vl_exam_questions")
public class VlExamQuestionEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "exam_id", nullable = false)
	private UUID examId;

	@Column(name = "master_word_id", nullable = false)
	private UUID masterWordId;

	@Convert(converter = VlQuestionTypeConverter.class)
	@Column(name = "question_type", nullable = false, length = 50)
	private VlQuestionType vlQuestionType;

	@Column(name = "question_number", nullable = false)
	private Integer questionNumber;

	@Column(name = "question_text", nullable = false, columnDefinition = "text")
	private String questionText;

	@Column(name = "correct_answer", nullable = false, columnDefinition = "text")
	private String correctAnswer;

	@Column(name = "option_1", length = 200)
	private String option1;

	@Column(name = "option_2", length = 200)
	private String option2;

	@Column(name = "option_3", length = 200)
	private String option3;

	@Column(name = "option_4", length = 200)
	private String option4;

	@Column(name = "option_5", length = 200)
	private String option5;

	@Column(name = "user_answer", columnDefinition = "text")
	private String userAnswer;

	@Column(name = "is_correct")
	private Boolean isCorrect;

	@Column(name = "answered_at")
	private LocalDateTime answeredAt;

	@CreatedDate
	@Column(name = "created_at", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	private VlExamQuestionEntity(UUID examId, UUID masterWordId, VlQuestionType vlQuestionType,
		Integer questionNumber, String questionText, String correctAnswer,
		String option1, String option2, String option3, String option4, String option5) {
		this.examId = examId;
		this.masterWordId = masterWordId;
		this.vlQuestionType = vlQuestionType;
		this.questionNumber = questionNumber;
		this.questionText = questionText;
		this.correctAnswer = correctAnswer;
		this.option1 = option1;
		this.option2 = option2;
		this.option3 = option3;
		this.option4 = option4;
		this.option5 = option5;
	}

	public static VlExamQuestionEntity create(UUID examId, UUID masterWordId, VlQuestionType vlQuestionType,
		Integer questionNumber, String questionText, String correctAnswer,
		String option1, String option2, String option3, String option4, String option5) {
		return new VlExamQuestionEntity(examId, masterWordId, vlQuestionType, questionNumber,
			questionText, correctAnswer, option1, option2, option3, option4, option5);
	}

	public void answer(String userAnswer) {
		this.userAnswer = userAnswer;
		this.isCorrect = correctAnswer.equalsIgnoreCase(userAnswer);
		this.answeredAt = LocalDateTime.now();
	}

	public void delete() {
		this.deletedAt = LocalDateTime.now();
	}
}