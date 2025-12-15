package com.voco.voco.app.auth.presentation.controller.dto.in;

import com.voco.voco.app.auth.application.usecase.dto.in.SocialSignInUseCaseDto;
import com.voco.voco.app.member.domain.model.Provider;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "소셜 로그인 요청")
public record SocialSignInRequest(
	@Schema(description = "소셜 로그인 제공자", example = "KAKAO")
	@NotNull(message = "소셜 로그인 제공자는 필수입니다.")
	Provider provider,

	@Schema(description = "소셜 토큰 (Apple/Google: idToken, Kakao: accessToken)")
	@NotBlank(message = "소셜 토큰은 필수입니다.")
	String idToken
) {
	public SocialSignInUseCaseDto toUseCaseDto() {
		return new SocialSignInUseCaseDto(provider, idToken);
	}
}
