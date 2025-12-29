package com.voco.voco.app.call.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScoringEmbeddable {

	@Column(name = "base_score")
	private Integer baseScore;

	@Column(name = "total_deduction")
	private Integer totalDeduction;

	@Column(name = "critical_deduction")
	private Integer criticalDeduction;

	@Column(name = "major_deduction")
	private Integer majorDeduction;

	@Column(name = "minor_deduction")
	private Integer minorDeduction;

	@Column(name = "level_adjustment")
	private Integer levelAdjustment;

	@Column(name = "final_score")
	private Integer finalScore;

	@Column(name = "rating", length = 20)
	private String rating;

	private ScoringEmbeddable(
		Integer baseScore,
		Integer totalDeduction,
		Integer criticalDeduction,
		Integer majorDeduction,
		Integer minorDeduction,
		Integer levelAdjustment,
		Integer finalScore,
		String rating
	) {
		this.baseScore = baseScore;
		this.totalDeduction = totalDeduction;
		this.criticalDeduction = criticalDeduction;
		this.majorDeduction = majorDeduction;
		this.minorDeduction = minorDeduction;
		this.levelAdjustment = levelAdjustment;
		this.finalScore = finalScore;
		this.rating = rating;
	}

	public static ScoringEmbeddable create(
		Integer baseScore,
		Integer totalDeduction,
		Integer criticalDeduction,
		Integer majorDeduction,
		Integer minorDeduction,
		Integer levelAdjustment,
		Integer finalScore,
		String rating
	) {
		return new ScoringEmbeddable(
			baseScore,
			totalDeduction,
			criticalDeduction,
			majorDeduction,
			minorDeduction,
			levelAdjustment,
			finalScore,
			rating
		);
	}
}
