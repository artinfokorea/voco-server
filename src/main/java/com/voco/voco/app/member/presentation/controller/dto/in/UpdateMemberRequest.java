package com.voco.voco.app.member.presentation.controller.dto.in;

import com.voco.voco.app.member.application.usecase.dto.in.UpdateMemberUseCaseDto;
import com.voco.voco.app.member.domain.model.Level;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "회원 수정 요청")
public record UpdateMemberRequest(
	@Schema(description = "영문 이름", example = "Hong Gildong")
	@NotBlank(message = "영문 이름은 필수입니다.")
	String englishName,

	@Schema(description = "레벨", example = "BEGINNER")
	@NotNull(message = "레벨은 필수입니다.")
	Level level
) {
	public UpdateMemberUseCaseDto toUseCaseDto(Long memberId) {
		return new UpdateMemberUseCaseDto(memberId, englishName, level);
	}
}
