package com.voco.voco.app.member.application.usecase.dto.in;

import com.voco.voco.app.member.domain.model.Level;
import com.voco.voco.app.member.domain.model.Provider;

public record SocialSignUpUseCaseDto(
	Provider provider,
	String idToken,
	String koreanName,
	String englishName,
	Level level
) {
}
