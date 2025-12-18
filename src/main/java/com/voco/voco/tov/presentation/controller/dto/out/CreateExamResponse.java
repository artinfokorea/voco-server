package com.voco.voco.tov.presentation.controller.dto.out;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "시험 생성 응답")
public record CreateExamResponse(
	@Schema(description = "생성된 시험 ID", example = "550e8400-e29b-41d4-a716-446655440000")
	UUID examId
) {
	public static CreateExamResponse from(UUID examId) {
		return new CreateExamResponse(examId);
	}
}
