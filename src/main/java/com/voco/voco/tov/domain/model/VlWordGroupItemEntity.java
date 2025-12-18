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
@Table(name = "vl_word_group_items")
public class VlWordGroupItemEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "word_group_id", nullable = false)
	private UUID wordGroupId;

	@Column(name = "master_word_id", nullable = false)
	private UUID masterWordId;

	@Column(name = "word_chapter_id", nullable = false)
	private UUID wordChapterId;

	@Column(name = "word_step_id")
	private UUID wordStepId;

	@Column(name = "item_order", nullable = false)
	private Integer itemOrder;

	@Column(name = "word_seq", nullable = false)
	private Integer wordSeq;

	@CreatedDate
	@Column(name = "created_at", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	private VlWordGroupItemEntity(UUID wordGroupId, UUID masterWordId, UUID wordChapterId, UUID wordStepId, Integer itemOrder, Integer wordSeq) {
		this.wordGroupId = wordGroupId;
		this.masterWordId = masterWordId;
		this.wordChapterId = wordChapterId;
		this.wordStepId = wordStepId;
		this.itemOrder = itemOrder;
		this.wordSeq = wordSeq;
	}

	public static VlWordGroupItemEntity create(UUID wordGroupId, UUID masterWordId, UUID wordChapterId, UUID wordStepId, Integer itemOrder, Integer wordSeq) {
		return new VlWordGroupItemEntity(wordGroupId, masterWordId, wordChapterId, wordStepId, itemOrder, wordSeq);
	}

	public void delete() {
		this.deletedAt = LocalDateTime.now();
	}
}