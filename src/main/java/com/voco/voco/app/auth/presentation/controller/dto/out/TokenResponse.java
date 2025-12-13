package com.voco.voco.app.auth.presentation.controller.dto.out;

import com.voco.voco.app.auth.application.usecase.dto.out.TokenInfo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "토큰 응답")
public record TokenResponse(
	@Schema(description = "액세스 토큰")
	String accessToken,

	@Schema(description = "리프레시 토큰")
	String refreshToken
) {
	public static TokenResponse from(TokenInfo tokenInfo) {
		return new TokenResponse(tokenInfo.accessToken(), tokenInfo.refreshToken());
	}
}
