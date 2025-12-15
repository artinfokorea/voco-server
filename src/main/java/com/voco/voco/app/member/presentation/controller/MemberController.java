package com.voco.voco.app.member.presentation.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voco.voco.app.member.application.usecase.SignUpUseCase;
import com.voco.voco.app.member.application.usecase.SocialSignUpUseCase;
import com.voco.voco.app.member.application.usecase.UpdateMemberUseCase;
import com.voco.voco.app.member.presentation.controller.dto.in.SignUpRequest;
import com.voco.voco.app.member.presentation.controller.dto.in.SocialSignUpRequest;
import com.voco.voco.app.member.presentation.controller.dto.in.UpdateMemberRequest;
import com.voco.voco.common.annotation.MemberId;
import com.voco.voco.common.dto.response.ApiResponse;
import com.voco.voco.common.dto.response.LongIdResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Member", description = "회원 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

	private final SignUpUseCase signUpUseCase;
	private final SocialSignUpUseCase socialSignUpUseCase;
	private final UpdateMemberUseCase updateMemberUseCase;

	@Operation(summary = "이메일 회원가입", description = "이메일로 새로운 회원을 등록합니다.")
	@PostMapping("/sign-up")
	public ApiResponse<LongIdResponse> signUp(@Valid @RequestBody SignUpRequest request) {
		Long memberId = signUpUseCase.execute(request.toUseCaseDto());
		return ApiResponse.success(new LongIdResponse(memberId));
	}

	@Operation(summary = "소셜 회원가입", description = "소셜 계정으로 새로운 회원을 등록합니다.")
	@PostMapping("/sign-up/social")
	public ApiResponse<LongIdResponse> socialSignUp(@Valid @RequestBody SocialSignUpRequest request) {
		Long memberId = socialSignUpUseCase.execute(request.toUseCaseDto());
		return ApiResponse.success(new LongIdResponse(memberId));
	}

	@Operation(summary = "회원 정보 수정", description = "회원 정보를 수정합니다.")
	@PutMapping
	public ApiResponse<Void> updateMember(
		@MemberId Long memberId,
		@Valid @RequestBody UpdateMemberRequest request
	) {
		updateMemberUseCase.execute(request.toUseCaseDto(memberId));
		return ApiResponse.success(null);
	}
}
