package com.voco.voco.tov.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.voco.voco.tov.domain.model.enums.VlRangeSelectionType;
import com.voco.voco.tov.infrastructure.persistence.converter.VlRangeSelectionTypeConverter;

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
@Table(name = "vl_word_groups")
public class VlWordGroupEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "service_name", nullable = false, length = 50)
	private String serviceName;

	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@Column(name = "description", columnDefinition = "text")
	private String description;

	@Convert(converter = VlRangeSelectionTypeConverter.class)
	@Column(name = "range_selection_type", nullable = false, length = 20)
	private VlRangeSelectionType vlRangeSelectionType;

	@Column(name = "enable_meaning", nullable = false)
	private Boolean enableMeaning;

	@Column(name = "enable_spelling", nullable = false)
	private Boolean enableSpelling;

	@Column(name = "enable_synonym_antonym", nullable = false)
	private Boolean enableSynonymAntonym;

	@Column(name = "enable_context", nullable = false)
	private Boolean enableContext;

	@Column(name = "enable_study", nullable = false)
	private Boolean enableStudy;

	@Column(name = "pass_percentage_total", nullable = false)
	private Integer passPercentageTotal;

	@Column(name = "pass_percentage_meaning", nullable = false)
	private Integer passPercentageMeaning;

	@Column(name = "pass_percentage_spelling", nullable = false)
	private Integer passPercentageSpelling;

	@Column(name = "pass_percentage_synonym_antonym", nullable = false)
	private Integer passPercentageSynonymAntonym;

	@Column(name = "pass_percentage_context", nullable = false)
	private Integer passPercentageContext;

	@Column(name = "chapter_name")
	private String chapterName;

	@Column(name = "step_name")
	private String stepName;

	@CreatedDate
	@Column(name = "created_at", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	private VlWordGroupEntity(String serviceName, String name, String description,
		VlRangeSelectionType vlRangeSelectionType,
		Boolean enableMeaning, Boolean enableSpelling, Boolean enableSynonymAntonym, Boolean enableContext,
		Boolean enableStudy,
		Integer passPercentageTotal, Integer passPercentageMeaning, Integer passPercentageSpelling,
		Integer passPercentageSynonymAntonym, Integer passPercentageContext, String chapterName, String stepName) {
		this.serviceName = serviceName;
		this.name = name;
		this.description = description;
		this.vlRangeSelectionType = vlRangeSelectionType;
		this.enableMeaning = enableMeaning;
		this.enableSpelling = enableSpelling;
		this.enableSynonymAntonym = enableSynonymAntonym;
		this.enableContext = enableContext;
		this.enableStudy = enableStudy;
		this.passPercentageTotal = passPercentageTotal;
		this.passPercentageMeaning = passPercentageMeaning;
		this.passPercentageSpelling = passPercentageSpelling;
		this.passPercentageSynonymAntonym = passPercentageSynonymAntonym;
		this.passPercentageContext = passPercentageContext;
		this.chapterName = chapterName;
		this.stepName = stepName;
	}

	public static VlWordGroupEntity create(String name, String description) {
		return new VlWordGroupEntity("VOCA_LAB", name, description, VlRangeSelectionType.CHAPTER,
			true, true, true, true, true, 70, 70, 70, 70, 70, null, null);
	}

	public void delete() {
		this.deletedAt = LocalDateTime.now();
	}
}
