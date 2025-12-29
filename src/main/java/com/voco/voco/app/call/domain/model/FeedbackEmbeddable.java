package com.voco.voco.app.call.domain.model;

import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedbackEmbeddable {

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "strengths")
	private List<String> strengths;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "improvements")
	private List<String> improvements;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "focus_areas")
	private List<String> focusAreas;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "tips")
	private List<String> tips;

	private FeedbackEmbeddable(
		List<String> strengths,
		List<String> improvements,
		List<String> focusAreas,
		List<String> tips
	) {
		this.strengths = strengths;
		this.improvements = improvements;
		this.focusAreas = focusAreas;
		this.tips = tips;
	}

	public static FeedbackEmbeddable create(
		List<String> strengths,
		List<String> improvements,
		List<String> focusAreas,
		List<String> tips
	) {
		return new FeedbackEmbeddable(strengths, improvements, focusAreas, tips);
	}
}
