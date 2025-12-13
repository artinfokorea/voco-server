package com.voco.voco.app.member.application.usecase.dto.in;

import java.util.Set;

import com.voco.voco.app.member.domain.model.Category;
import com.voco.voco.app.member.domain.model.Level;

public record UpdateMemberUseCaseDto(
	Long memberId,
	String englishName,
	Level level,
	Set<Category> categories
) {
}
