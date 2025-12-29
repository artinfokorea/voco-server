package com.voco.voco.app.call.presentation.controller.dto.out;

import java.util.List;
import java.util.Map;

import com.voco.voco.app.call.application.usecase.dto.out.CallAnalysisInfo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "통화 분석 결과 응답")
public record CallAnalysisResponse(
	@Schema(description = "분석 결과 ID")
	Long id,

	@Schema(description = "과제 완료 분석 (JSON)")
	Map<String, Object> taskCompletion,

	@Schema(description = "총 사용자 발화 수")
	Integer totalUserUtterances,

	@Schema(description = "정확한 발화 수")
	Integer correctUtterances,

	@Schema(description = "발화별 분석 목록")
	List<Map<String, Object>> utteranceAnalyses,

	@Schema(description = "오류 목록")
	List<Map<String, Object>> errors,

	@Schema(description = "오류 요약")
	Map<String, Object> errorSummary,

	@Schema(description = "채점 정보")
	ScoringResponse scoring,

	@Schema(description = "피드백 정보")
	FeedbackResponse feedback,

	@Schema(description = "간략 설명")
	String briefDescription
) {
	@Schema(description = "채점 정보")
	public record ScoringResponse(
		@Schema(description = "기본 점수") Integer baseScore,
		@Schema(description = "총 감점") Integer totalDeduction,
		@Schema(description = "치명적 오류 감점") Integer criticalDeduction,
		@Schema(description = "주요 오류 감점") Integer majorDeduction,
		@Schema(description = "경미한 오류 감점") Integer minorDeduction,
		@Schema(description = "레벨 조정") Integer levelAdjustment,
		@Schema(description = "최종 점수") Integer finalScore,
		@Schema(description = "등급") String rating
	) {
	}

	@Schema(description = "피드백 정보")
	public record FeedbackResponse(
		@Schema(description = "강점 목록") List<String> strengths,
		@Schema(description = "개선점 목록") List<String> improvements,
		@Schema(description = "집중 영역 목록") List<String> focusAreas,
		@Schema(description = "팁 목록") List<String> tips
	) {
	}

	public static CallAnalysisResponse from(CallAnalysisInfo info) {
		return new CallAnalysisResponse(
			info.id(),
			info.taskCompletion(),
			info.totalUserUtterances(),
			info.correctUtterances(),
			info.utteranceAnalyses(),
			info.errors(),
			info.errorSummary(),
			info.scoring() != null
				? new ScoringResponse(
					info.scoring().baseScore(),
					info.scoring().totalDeduction(),
					info.scoring().criticalDeduction(),
					info.scoring().majorDeduction(),
					info.scoring().minorDeduction(),
					info.scoring().levelAdjustment(),
					info.scoring().finalScore(),
					info.scoring().rating()
				)
				: null,
			info.feedback() != null
				? new FeedbackResponse(
					info.feedback().strengths(),
					info.feedback().improvements(),
					info.feedback().focusAreas(),
					info.feedback().tips()
				)
				: null,
			info.briefDescription()
		);
	}
}
