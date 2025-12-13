package com.voco.voco.app.auth.application.usecase.dto.in;

public record RefreshTokenUseCaseDto(
	String accessToken,
	String refreshToken
) {
}