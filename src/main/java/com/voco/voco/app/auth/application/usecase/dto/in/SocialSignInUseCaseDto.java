package com.voco.voco.app.auth.application.usecase.dto.in;

import com.voco.voco.app.member.domain.model.Provider;

public record SocialSignInUseCaseDto(
	Provider provider,
	String idToken
) {
}
