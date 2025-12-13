package com.voco.voco.app.notification.presentation.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voco.voco.app.notification.application.usecase.CreateNotificationScheduleUseCase;
import com.voco.voco.app.notification.application.usecase.DeleteNotificationScheduleUseCase;
import com.voco.voco.app.notification.application.usecase.UpdateNotificationScheduleUseCase;
import com.voco.voco.app.notification.application.usecase.dto.in.DeleteNotificationScheduleUseCaseDto;
import com.voco.voco.app.notification.presentation.controller.dto.in.CreateNotificationScheduleRequest;
import com.voco.voco.app.notification.presentation.controller.dto.in.UpdateNotificationScheduleRequest;
import com.voco.voco.common.annotation.MemberId;
import com.voco.voco.common.dto.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Notification Schedule", description = "알림 스케줄 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification-schedules")
public class NotificationScheduleController {

	private final CreateNotificationScheduleUseCase createNotificationScheduleUseCase;
	private final UpdateNotificationScheduleUseCase updateNotificationScheduleUseCase;
	private final DeleteNotificationScheduleUseCase deleteNotificationScheduleUseCase;

	@Operation(summary = "알림 스케줄 생성", description = "요일별 알림 스케줄을 생성합니다.")
	@PostMapping
	public ApiResponse<Void> createNotificationSchedule(
		@MemberId Long memberId,
		@Valid @RequestBody CreateNotificationScheduleRequest request
	) {
		createNotificationScheduleUseCase.execute(request.toUseCaseDto(memberId));
		return ApiResponse.success();
	}

	@Operation(summary = "알림 스케줄 수정", description = "알림 스케줄을 수정합니다.")
	@PutMapping("/{id}")
	public ApiResponse<Void> updateNotificationSchedule(
		@PathVariable Long id,
		@MemberId Long memberId,
		@Valid @RequestBody UpdateNotificationScheduleRequest request
	) {
		updateNotificationScheduleUseCase.execute(request.toUseCaseDto(id, memberId));
		return ApiResponse.success();
	}

	@Operation(summary = "알림 스케줄 삭제", description = "알림 스케줄을 삭제합니다.")
	@DeleteMapping("/{id}")
	public ApiResponse<Void> deleteNotificationSchedule(
		@PathVariable Long id,
		@MemberId Long memberId
	) {
		deleteNotificationScheduleUseCase.execute(new DeleteNotificationScheduleUseCaseDto(id, memberId));
		return ApiResponse.success();
	}
}