package com.voco.voco.app.call.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CallAnalysisGrade {
	EXCELLENT(90),
	GOOD(80),
	FAIR(70),
	POOR(60),
	BAD(0);

	private final int minScore;

	public static CallAnalysisGrade fromScore(int score) {
		if (score >= EXCELLENT.minScore) {
			return EXCELLENT;
		} else if (score >= GOOD.minScore) {
			return GOOD;
		} else if (score >= FAIR.minScore) {
			return FAIR;
		} else if (score >= POOR.minScore) {
			return POOR;
		}
		return BAD;
	}
}
