package com.voco.voco.app.auth.presentation.controller.dto.in;

import com.voco.voco.app.auth.application.usecase.dto.in.RefreshTokenUseCaseDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "토큰 갱신 요청")
public record RefreshTokenRequest(
	@Schema(description = "액세스 토큰")
	@NotBlank(message = "액세스 토큰은 필수입니다.")
	String accessToken,

	@Schema(description = "리프레시 토큰")
	@NotBlank(message = "리프레시 토큰은 필수입니다.")
	String refreshToken
) {
	public RefreshTokenUseCaseDto toUseCaseDto() {
		return new RefreshTokenUseCaseDto(accessToken, refreshToken);
	}
}