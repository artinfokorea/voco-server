package com.voco.voco.app.auth.application.usecase.dto.in;

public record SignInUseCaseDto(
	String email,
	String password
) {
}
