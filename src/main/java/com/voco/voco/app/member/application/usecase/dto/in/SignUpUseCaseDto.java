package com.voco.voco.app.member.application.usecase.dto.in;

public record SignUpUseCaseDto(
	String koreanName,
	String englishName,
	String email,
	String password
) {
}