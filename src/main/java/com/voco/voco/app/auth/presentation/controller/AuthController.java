package com.voco.voco.app.auth.presentation.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voco.voco.app.auth.application.usecase.SignInUseCase;
import com.voco.voco.app.auth.application.usecase.dto.out.TokenInfo;
import com.voco.voco.app.auth.presentation.controller.dto.in.SignInRequest;
import com.voco.voco.app.auth.presentation.controller.dto.out.TokenResponse;
import com.voco.voco.common.dto.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final SignInUseCase signInUseCase;

	@Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
	@PostMapping("/sign-in")
	public ApiResponse<TokenResponse> signIn(@Valid @RequestBody SignInRequest request) {
		TokenInfo tokenInfo = signInUseCase.execute(request.toUseCaseDto());
		return ApiResponse.success(TokenResponse.from(tokenInfo));
	}
}