package com.voco.voco.app.member.application.usecase.dto.in;

import com.voco.voco.app.member.domain.model.Level;

public record SignUpUseCaseDto(
	String koreanName,
	String englishName,
	String email,
	String password,
	Level level
) {
}