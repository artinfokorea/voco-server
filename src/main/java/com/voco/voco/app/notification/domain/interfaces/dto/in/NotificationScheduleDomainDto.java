package com.voco.voco.app.notification.domain.interfaces.dto.in;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record NotificationScheduleDomainDto(
	Long memberId,
	DayOfWeek dayOfWeek,
	LocalTime notificationTime
) {
}