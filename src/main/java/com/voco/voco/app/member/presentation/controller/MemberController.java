package com.voco.voco.app.member.presentation.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voco.voco.app.member.application.usecase.SignUpUseCase;
import com.voco.voco.app.member.presentation.controller.dto.in.SignUpRequest;
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

	@Operation(summary = "회원가입", description = "새로운 회원을 등록합니다.")
	@PostMapping("/sign-up")
	public ApiResponse<LongIdResponse> signUp(@Valid @RequestBody SignUpRequest request) {
		Long memberId = signUpUseCase.execute(request.toUseCaseDto());
		return ApiResponse.success(new LongIdResponse(memberId));
	}
}