package com.voco.voco.app.auth.presentation.controller.dto.in;

import com.voco.voco.app.auth.application.usecase.dto.in.SignInUseCaseDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로그인 요청")
public record SignInRequest(
	@Schema(description = "이메일", example = "test@example.com")
	@NotBlank(message = "이메일은 필수입니다.")
	@Email(message = "올바른 이메일 형식이 아닙니다.")
	String email,

	@Schema(description = "비밀번호", example = "Password1!")
	@NotBlank(message = "비밀번호는 필수입니다.")
	String password
) {
	public SignInUseCaseDto toUseCaseDto() {
		return new SignInUseCaseDto(email, password);
	}
}