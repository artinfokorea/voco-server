package com.voco.voco.app.notification.presentation.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voco.voco.app.notification.application.usecase.CreateNotificationScheduleUseCase;
import com.voco.voco.app.notification.presentation.controller.dto.in.CreateNotificationScheduleRequest;
import com.voco.voco.common.annotation.MemberId;
import com.voco.voco.common.dto.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Notification Schedule", description = "알림 스케줄 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification-schedules")
public class NotificationScheduleController {

	private final CreateNotificationScheduleUseCase createNotificationScheduleUseCase;

	@Operation(summary = "알림 스케줄 생성", description = "요일별 알림 스케줄을 생성합니다.")
	@PostMapping
	public ApiResponse<Void> createNotificationSchedule(
		@MemberId Long memberId,
		@Valid @RequestBody CreateNotificationScheduleRequest request
	) {
		createNotificationScheduleUseCase.execute(request.toUseCaseDto(memberId));
		return ApiResponse.success();
	}
}