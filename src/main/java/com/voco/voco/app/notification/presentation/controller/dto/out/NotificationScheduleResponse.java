package com.voco.voco.app.notification.presentation.controller.dto.out;

import java.time.DayOfWeek;
import java.time.LocalTime;

import com.voco.voco.app.notification.application.usecase.dto.out.NotificationScheduleInfo;
import com.voco.voco.app.scenario.domain.model.Level;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "알림 스케줄 응답")
public record NotificationScheduleResponse(
	@Schema(description = "스케줄 ID", example = "1")
	Long id,

	@Schema(description = "요일", example = "MONDAY")
	DayOfWeek dayOfWeek,

	@Schema(description = "알림 시간", example = "09:00")
	LocalTime notificationTime,

	@Schema(description = "시나리오 ID")
	Long scenarioId,

	@Schema(description = "시나리오 제목")
	String scenarioName,

	@Schema(description = "시나리오 레벨")
	Level scenarioLevel
) {
	public static NotificationScheduleResponse from(NotificationScheduleInfo info) {
		return new NotificationScheduleResponse(
			info.id(),
			info.dayOfWeek(),
			info.notificationTime(),
			info.scenarioId(),
			info.scenarioName(),
			info.scenarioLevel()
		);
	}
}
