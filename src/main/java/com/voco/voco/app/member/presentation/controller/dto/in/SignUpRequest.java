package com.voco.voco.app.member.presentation.controller.dto.in;

import com.voco.voco.app.member.application.usecase.dto.in.SignUpUseCaseDto;
import com.voco.voco.app.member.domain.model.Level;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "회원가입 요청")
public record SignUpRequest(
	@Schema(description = "한글 이름", example = "홍길동")
	@NotBlank(message = "한글 이름은 필수입니다.")
	String koreanName,

	@Schema(description = "영문 이름", example = "Hong Gildong")
	@NotBlank(message = "영문 이름은 필수입니다.")
	String englishName,

	@Schema(description = "이메일", example = "test@example.com")
	@NotBlank(message = "이메일은 필수입니다.")
	@Email(message = "올바른 이메일 형식이 아닙니다.")
	String email,

	@Schema(description = "비밀번호", example = "Password1!")
	@NotBlank(message = "비밀번호는 필수입니다.")
	String password,

	@Schema(description = "레벨", example = "BEGINNER")
	@NotNull(message = "레벨은 필수입니다.")
	Level level
) {
	public SignUpUseCaseDto toUseCaseDto() {
		return new SignUpUseCaseDto(koreanName, englishName, email, password, level);
	}
}