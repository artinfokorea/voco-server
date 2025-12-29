package com.voco.voco.app.member.application.usecase.dto.in;

import com.voco.voco.app.member.domain.model.Level;

public record UpdateMemberUseCaseDto(
	Long memberId,
	String englishName,
	Level level
) {
}
