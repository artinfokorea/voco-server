package com.voco.voco.tov.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.voco.voco.tov.domain.model.enums.VlExamStatus;
import com.voco.voco.tov.domain.model.enums.VlPassStatus;
import com.voco.voco.tov.infrastructure.persistence.converter.VlExamStatusConverter;
import com.voco.voco.tov.infrastructure.persistence.converter.VlPassStatusConverter;

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
@Table(name = "vl_exams")
public class VlExamEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "user_id", nullable = false)
	private UUID userId;

	@Column(name = "word_group_id", nullable = false)
	private UUID wordGroupId;

	@Column(name = "start_chapter_number", nullable = false)
	private Integer startChapterNumber;

	@Column(name = "start_step_number")
	private Integer startStepNumber;

	@Column(name = "end_chapter_number", nullable = false)
	private Integer endChapterNumber;

	@Column(name = "end_step_number")
	private Integer endStepNumber;

	@Column(name = "total_questions", nullable = false)
	private Integer totalQuestions;

	@Convert(converter = VlExamStatusConverter.class)
	@Column(name = "status", nullable = false, length = 20)
	private VlExamStatus status;

	@Convert(converter = VlPassStatusConverter.class)
	@Column(name = "pass_status", length = 20)
	private VlPassStatus passStatus;

	@Column(name = "correct_answers", nullable = false)
	private Integer correctAnswers;

	@Column(name = "started_at")
	private String startedAt;

	@Column(name = "completed_at")
	private LocalDateTime completedAt;

	@CreatedDate
	@Column(name = "created_at", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	private VlExamEntity(UUID userId, UUID wordGroupId, Integer startChapterNumber, Integer startStepNumber,
		Integer endChapterNumber, Integer endStepNumber, Integer totalQuestions) {
		this.userId = userId;
		this.wordGroupId = wordGroupId;
		this.startChapterNumber = startChapterNumber;
		this.startStepNumber = startStepNumber;
		this.endChapterNumber = endChapterNumber;
		this.endStepNumber = endStepNumber;
		this.totalQuestions = totalQuestions;
		this.status = VlExamStatus.IN_PROGRESS;
		this.correctAnswers = 0;
	}

	public static VlExamEntity create(UUID userId, UUID wordGroupId, Integer startChapterNumber, Integer startStepNumber,
		Integer endChapterNumber, Integer endStepNumber, Integer totalQuestions) {
		return new VlExamEntity(userId, wordGroupId, startChapterNumber, startStepNumber,
			endChapterNumber, endStepNumber, totalQuestions);
	}

	public void complete(Integer correctAnswers, VlPassStatus passStatus) {
		this.correctAnswers = correctAnswers;
		this.passStatus = passStatus;
		this.status = VlExamStatus.COMPLETED;
		this.completedAt = LocalDateTime.now();
	}

	public void delete() {
		this.deletedAt = LocalDateTime.now();
	}
}