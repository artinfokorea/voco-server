package com.voco.voco.app.call.presentation.controller.dto.in;

import java.util.List;
import java.util.Map;

import com.voco.voco.app.call.application.usecase.dto.in.CreateCallAnalysisUseCaseDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Schema(description = "통화 분석 결과 생성 요청")
public record CreateCallAnalysisRequest(
	@Schema(description = "과제 완료 분석 (JSON)")
	@NotNull
	Map<String, Object> taskCompletion,

	@Schema(description = "언어 정확도 분석")
	@NotNull
	@Valid
	LanguageAccuracyRequest languageAccuracy
) {
	@Schema(description = "언어 정확도 분석")
	public record LanguageAccuracyRequest(
		@Schema(description = "총 사용자 발화 수")
		@NotNull
		Integer totalUserUtterances,

		@Schema(description = "정확한 발화 수")
		@NotNull
		Integer correctUtterances,

		@Schema(description = "발화 분석 목록")
		@NotNull
		List<Map<String, Object>> utteranceAnalyses,

		@Schema(description = "오류 목록")
		List<Map<String, Object>> errors,

		@Schema(description = "오류 요약")
		@NotNull
		Map<String, Object> errorSummary,

		@Schema(description = "점수 정보")
		@NotNull
		@Valid
		ScoringRequest scoring,

		@Schema(description = "피드백 정보")
		@NotNull
		@Valid
		FeedbackRequest feedback,

		@Schema(description = "간략 설명")
		@NotNull
		String briefDescription
	) {
	}

	@Schema(description = "점수 정보")
	public record ScoringRequest(
		@Schema(description = "기본 점수")
		Integer baseScore,

		@Schema(description = "총 감점")
		Integer totalDeduction,

		@Schema(description = "치명적 오류 감점")
		Integer criticalDeduction,

		@Schema(description = "주요 오류 감점")
		Integer majorDeduction,

		@Schema(description = "경미한 오류 감점")
		Integer minorDeduction,

		@Schema(description = "레벨 조정")
		Integer levelAdjustment,

		@Schema(description = "최종 점수")
		Integer finalScore,

		@Schema(description = "등급")
		String rating
	) {
	}

	@Schema(description = "피드백 정보")
	public record FeedbackRequest(
		@Schema(description = "강점")
		List<String> strengths,

		@Schema(description = "개선점")
		List<String> improvements,

		@Schema(description = "집중 영역")
		List<String> focusAreas,

		@Schema(description = "팁")
		List<String> tips
	) {
	}

	public CreateCallAnalysisUseCaseDto toUseCaseDto() {
		return new CreateCallAnalysisUseCaseDto(
			taskCompletion,
			languageAccuracy.totalUserUtterances(),
			languageAccuracy.correctUtterances(),
			languageAccuracy.utteranceAnalyses(),
			languageAccuracy.errors(),
			languageAccuracy.errorSummary(),
			new CreateCallAnalysisUseCaseDto.ScoringDto(
				languageAccuracy.scoring().baseScore(),
				languageAccuracy.scoring().totalDeduction(),
				languageAccuracy.scoring().criticalDeduction(),
				languageAccuracy.scoring().majorDeduction(),
				languageAccuracy.scoring().minorDeduction(),
				languageAccuracy.scoring().levelAdjustment(),
				languageAccuracy.scoring().finalScore(),
				languageAccuracy.scoring().rating()
			),
			new CreateCallAnalysisUseCaseDto.FeedbackDto(
				languageAccuracy.feedback().strengths(),
				languageAccuracy.feedback().improvements(),
				languageAccuracy.feedback().focusAreas(),
				languageAccuracy.feedback().tips()
			),
			languageAccuracy.briefDescription()
		);
	}
}
