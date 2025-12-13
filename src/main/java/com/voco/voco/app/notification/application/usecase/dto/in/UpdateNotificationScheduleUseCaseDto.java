package com.voco.voco.app.notification.application.usecase.dto.in;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record UpdateNotificationScheduleUseCaseDto(
	Long id,
	Long memberId,
	DayOfWeek dayOfWeek,
	LocalTime notificationTime
) {
}
