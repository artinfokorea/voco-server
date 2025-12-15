package com.voco.voco.app.member.application.usecase.dto.in;

import java.util.Set;

import com.voco.voco.app.member.domain.model.Category;
import com.voco.voco.app.member.domain.model.Level;
import com.voco.voco.app.member.domain.model.Provider;

public record SocialSignUpUseCaseDto(
	Provider provider,
	String idToken,
	String koreanName,
	String englishName,
	Level level,
	Set<Category> categories
) {
}
