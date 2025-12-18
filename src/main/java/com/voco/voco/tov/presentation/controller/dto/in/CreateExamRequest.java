package com.voco.voco.tov.presentation.controller.dto.in;

import java.util.UUID;

import com.voco.voco.tov.application.dto.in.CreateExamUseCaseDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "시험 생성 요청")
public record CreateExamRequest(
	@Schema(description = "유저 ID", example = "550e8400-e29b-41d4-a716-446655440000")
	@NotNull(message = "유저 ID는 필수입니다.")
	UUID userId,

	@Schema(description = "단어 그룹 ID", example = "550e8400-e29b-41d4-a716-446655440000")
	@NotNull(message = "단어 그룹 ID는 필수입니다.")
	UUID groupId,

	@Schema(description = "시작 챕터 번호", example = "1")
	@NotNull(message = "시작 챕터 번호는 필수입니다.")
	@Min(value = 1, message = "챕터 번호는 1 이상이어야 합니다.")
	Integer chapterFrom,

	@Schema(description = "종료 챕터 번호", example = "3")
	@NotNull(message = "종료 챕터 번호는 필수입니다.")
	@Min(value = 1, message = "챕터 번호는 1 이상이어야 합니다.")
	Integer chapterTo,

	@Schema(description = "시작 스텝 번호 (null이면 챕터 처음부터)", example = "1")
	Integer stepFrom,

	@Schema(description = "종료 스텝 번호 (null이면 챕터 끝까지)", example = "5")
	Integer stepTo,

	@Schema(description = "문제 수", example = "20")
	@NotNull(message = "문제 수는 필수입니다.")
	@Max(value = 300, message = "문제 수는 300 이하여야 합니다.")
	@Min(value = 30, message = "문제 수는 30 이상이어야 합니다.")
	Integer size
) {
	public CreateExamUseCaseDto toUseCaseDto() {
		return new CreateExamUseCaseDto(userId, groupId, chapterFrom, chapterTo, stepFrom, stepTo, size);
	}
}
