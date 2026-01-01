package com.voco.voco.app.member.presentation.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voco.voco.app.member.application.usecase.DeleteMemberUseCase;
import com.voco.voco.app.member.application.usecase.GetMyInfoUseCase;
import com.voco.voco.app.member.application.usecase.SignUpUseCase;
import com.voco.voco.app.member.application.usecase.SocialSignUpUseCase;
import com.voco.voco.app.member.application.usecase.UpdateMemberUseCase;
import com.voco.voco.app.member.presentation.controller.dto.in.SignUpRequest;
import com.voco.voco.app.member.presentation.controller.dto.in.SocialSignUpRequest;
import com.voco.voco.app.member.presentation.controller.dto.in.UpdateMemberRequest;
import com.voco.voco.app.member.presentation.controller.dto.out.MyInfoResponse;
import com.voco.voco.common.annotation.MemberId;
import com.voco.voco.common.dto.response.ApiResponse;
import com.voco.voco.common.dto.response.LongIdResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
	private final GetMyInfoUseCase getMyInfoUseCase;
	private final DeleteMemberUseCase deleteMemberUseCase;

	@Operation(summary = "내 정보 조회", description = "로그인한 회원의 정보를 조회합니다.")
	@SecurityRequirement(name = "bearerAuth")
	@GetMapping("/me")
	public ApiResponse<MyInfoResponse> getMyInfo(@MemberId Long memberId) {
		return ApiResponse.success(MyInfoResponse.from(getMyInfoUseCase.execute(memberId)));
	}

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
	@SecurityRequirement(name = "bearerAuth")
	@PutMapping
	public ApiResponse<Void> updateMember(
		@MemberId Long memberId,
		@Valid @RequestBody UpdateMemberRequest request
	) {
		updateMemberUseCase.execute(request.toUseCaseDto(memberId));
		return ApiResponse.success(null);
	}

	@Operation(summary = "회원 탈퇴", description = "회원을 탈퇴합니다.")
	@SecurityRequirement(name = "bearerAuth")
	@DeleteMapping
	public ApiResponse<Void> deleteMember(@MemberId Long memberId) {
		deleteMemberUseCase.execute(memberId);
		return ApiResponse.success(null);
	}
}
