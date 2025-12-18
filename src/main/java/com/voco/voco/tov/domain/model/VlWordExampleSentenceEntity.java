package com.voco.voco.tov.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
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
@Table(name = "vl_word_example_sentences")
public class VlWordExampleSentenceEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "master_word_id", nullable = false)
	private UUID masterWordId;

	@Column(name = "sentence", nullable = false, columnDefinition = "text")
	private String sentence;

	@Column(name = "sentence_with_blank", nullable = false, columnDefinition = "text")
	private String sentenceWithBlank;

	@Column(name = "blank_answer", nullable = false, length = 100)
	private String blankAnswer;

	@CreatedDate
	@Column(name = "created_at", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	private VlWordExampleSentenceEntity(UUID masterWordId, String sentence, String sentenceWithBlank, String blankAnswer) {
		this.masterWordId = masterWordId;
		this.sentence = sentence;
		this.sentenceWithBlank = sentenceWithBlank;
		this.blankAnswer = blankAnswer;
	}

	public static VlWordExampleSentenceEntity create(UUID masterWordId, String sentence, String sentenceWithBlank, String blankAnswer) {
		return new VlWordExampleSentenceEntity(masterWordId, sentence, sentenceWithBlank, blankAnswer);
	}

	public void delete() {
		this.deletedAt = LocalDateTime.now();
	}
}