package com.voco.voco.app.member.presentation.controller.dto.in;

import com.voco.voco.app.member.application.usecase.dto.in.SocialSignUpUseCaseDto;
import com.voco.voco.app.member.domain.model.Level;
import com.voco.voco.app.member.domain.model.Provider;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "소셜 회원가입 요청")
public record SocialSignUpRequest(
	@Schema(description = "소셜 로그인 제공자", example = "KAKAO")
	@NotNull(message = "소셜 로그인 제공자는 필수입니다.")
	Provider provider,

	@Schema(description = "소셜 토큰 (Apple/Google: idToken, Kakao: accessToken)")
	@NotBlank(message = "소셜 토큰은 필수입니다.")
	String idToken,

	@Schema(description = "한글 이름", example = "홍길동")
	@NotBlank(message = "한글 이름은 필수입니다.")
	String koreanName,

	@Schema(description = "영문 이름", example = "Hong Gildong")
	@NotBlank(message = "영문 이름은 필수입니다.")
	String englishName,

	@Schema(description = "레벨", example = "BEGINNER")
	@NotNull(message = "레벨은 필수입니다.")
	Level level
) {
	public SocialSignUpUseCaseDto toUseCaseDto() {
		return new SocialSignUpUseCaseDto(provider, idToken, koreanName, englishName, level);
	}
}
