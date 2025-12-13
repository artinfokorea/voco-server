package com.voco.voco.app.notification.presentation.controller.dto.in;

import java.time.DayOfWeek;
import java.time.LocalTime;

import com.voco.voco.app.notification.application.usecase.dto.in.CreateNotificationScheduleUseCaseDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "알림 스케줄 생성 요청")
public record CreateNotificationScheduleRequest(
	@Schema(description = "요일", example = "MONDAY")
	@NotNull(message = "요일은 필수입니다.")
	DayOfWeek dayOfWeek,

	@Schema(description = "알림 시간", example = "09:00")
	@NotNull(message = "알림 시간은 필수입니다.")
	LocalTime notificationTime
) {
	public CreateNotificationScheduleUseCaseDto toUseCaseDto(Long memberId) {
		return new CreateNotificationScheduleUseCaseDto(memberId, dayOfWeek, notificationTime);
	}
}