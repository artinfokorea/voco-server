package com.voco.voco.app.scenario.domain.model;

import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.voco.voco.common.model.BaseModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "voco_scenario")
public class ScenarioEntity extends BaseModel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(name = "level", nullable = false)
	private Level level;

	@Enumerated(EnumType.STRING)
	@Column(name = "category", nullable = false)
	private Category category;

	@Column(name = "content", nullable = false, columnDefinition = "TEXT")
	private String content;

	private ScenarioEntity(String title, String description, Level level, Category category, String content) {
		this.title = title;
		this.description = description;
		this.level = level;
		this.category = category;
		this.content = content;
	}

	public static ScenarioEntity create(String title, String description, Level level, Category category, String content) {
		return new ScenarioEntity(title, description, level, category, content);
	}

	public void update(String title, String description, Level level, Category category, String content) {
		this.title = title;
		this.description = description;
		this.level = level;
		this.category = category;
		this.content = content;
	}
}
