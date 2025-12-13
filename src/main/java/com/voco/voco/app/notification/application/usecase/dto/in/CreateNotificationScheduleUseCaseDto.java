package com.voco.voco.app.notification.application.usecase.dto.in;

import java.time.DayOfWeek;
import java.time.LocalTime;

import com.voco.voco.app.notification.domain.interfaces.dto.in.NotificationScheduleDomainDto;

public record CreateNotificationScheduleUseCaseDto(
	Long memberId,
	DayOfWeek dayOfWeek,
	LocalTime notificationTime
) {
	public NotificationScheduleDomainDto toDomainDto() {
		return new NotificationScheduleDomainDto(memberId, dayOfWeek, notificationTime);
	}
}