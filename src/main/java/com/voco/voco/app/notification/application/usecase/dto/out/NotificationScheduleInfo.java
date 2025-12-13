package com.voco.voco.app.notification.application.usecase.dto.out;

import java.time.DayOfWeek;
import java.time.LocalTime;

import com.voco.voco.app.notification.domain.model.NotificationScheduleEntity;

public record NotificationScheduleInfo(
	Long id,
	DayOfWeek dayOfWeek,
	LocalTime notificationTime
) {
	public static NotificationScheduleInfo from(NotificationScheduleEntity entity) {
		return new NotificationScheduleInfo(
			entity.getId(),
			entity.getDayOfWeek(),
			entity.getNotificationTime()
		);
	}
}
