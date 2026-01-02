package com.voco.voco.app.auth.presentation.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voco.voco.app.auth.application.usecase.AdminRefreshTokenUseCase;
import com.voco.voco.app.auth.application.usecase.AdminSignInUseCase;
import com.voco.voco.app.auth.application.usecase.RefreshTokenUseCase;
import com.voco.voco.app.auth.application.usecase.SignInUseCase;
import com.voco.voco.app.auth.application.usecase.SocialSignInUseCase;
import com.voco.voco.app.auth.application.usecase.dto.out.TokenInfo;
import com.voco.voco.app.auth.presentation.controller.dto.in.RefreshTokenRequest;
import com.voco.voco.app.auth.presentation.controller.dto.in.SignInRequest;
import com.voco.voco.app.auth.presentation.controller.dto.in.SocialSignInRequest;
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
	private final AdminSignInUseCase adminSignInUseCase;
	private final SocialSignInUseCase socialSignInUseCase;
	private final RefreshTokenUseCase refreshTokenUseCase;
	private final AdminRefreshTokenUseCase adminRefreshTokenUseCase;

	@Operation(summary = "이메일 로그인", description = "이메일과 비밀번호로 로그인합니다.")
	@PostMapping("/sign-in")
	public ApiResponse<TokenResponse> signIn(@Valid @RequestBody SignInRequest request) {
		TokenInfo tokenInfo = signInUseCase.execute(request.toUseCaseDto());
		return ApiResponse.success(TokenResponse.from(tokenInfo));
	}

	@Operation(summary = "관리자 로그인", description = "관리자 계정으로 로그인합니다.")
	@PostMapping("/admin/sign-in")
	public ApiResponse<TokenResponse> adminSignIn(@Valid @RequestBody SignInRequest request) {
		TokenInfo tokenInfo = adminSignInUseCase.execute(request.toUseCaseDto());
		return ApiResponse.success(TokenResponse.from(tokenInfo));
	}

	@Operation(summary = "소셜 로그인", description = "소셜 계정으로 로그인합니다.")
	@PostMapping("/sign-in/social")
	public ApiResponse<TokenResponse> socialSignIn(@Valid @RequestBody SocialSignInRequest request) {
		TokenInfo tokenInfo = socialSignInUseCase.execute(request.toUseCaseDto());
		return ApiResponse.success(TokenResponse.from(tokenInfo));
	}

	@Operation(summary = "토큰 갱신", description = "액세스 토큰과 리프레시 토큰으로 새로운 토큰을 발급합니다.")
	@PostMapping("/refresh")
	public ApiResponse<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
		TokenInfo tokenInfo = refreshTokenUseCase.execute(request.toUseCaseDto());
		return ApiResponse.success(TokenResponse.from(tokenInfo));
	}

	@Operation(summary = "관리자 토큰 갱신", description = "관리자 계정의 토큰을 갱신합니다.")
	@PostMapping("/admin/refresh")
	public ApiResponse<TokenResponse> adminRefresh(@Valid @RequestBody RefreshTokenRequest request) {
		TokenInfo tokenInfo = adminRefreshTokenUseCase.execute(request.toUseCaseDto());
		return ApiResponse.success(TokenResponse.from(tokenInfo));
	}
}
