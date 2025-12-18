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
@Table(name = "vl_word_meanings")
public class VlWordMeaningEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "master_word_id", nullable = false)
	private UUID masterWordId;

	@Column(name = "korean_meaning", nullable = false, length = 200)
	private String koreanMeaning;

	@Column(name = "display_order", nullable = false)
	private Integer displayOrder;

	@CreatedDate
	@Column(name = "created_at", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	private VlWordMeaningEntity(UUID masterWordId, String koreanMeaning, Integer displayOrder) {
		this.masterWordId = masterWordId;
		this.koreanMeaning = koreanMeaning;
		this.displayOrder = displayOrder;
	}

	public static VlWordMeaningEntity create(UUID masterWordId, String koreanMeaning, Integer displayOrder) {
		return new VlWordMeaningEntity(masterWordId, koreanMeaning, displayOrder);
	}

	public void delete() {
		this.deletedAt = LocalDateTime.now();
	}
}