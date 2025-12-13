package com.voco.voco.app.auth.application.usecase.dto.out;

public record TokenInfo(
	String accessToken,
	String refreshToken
) {
}