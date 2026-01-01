package com.voco.voco.app.notification.application.usecase.dto.out;

import java.time.DayOfWeek;
import java.time.LocalTime;

import com.voco.voco.app.notification.domain.interfaces.dto.out.NotificationScheduleWithScenarioDomainDto;
import com.voco.voco.app.scenario.domain.model.Level;

public record NotificationScheduleInfo(
	Long id,
	DayOfWeek dayOfWeek,
	LocalTime notificationTime,
	Long scenarioId,
	String scenarioName,
	Level scenarioLevel
) {
	public static NotificationScheduleInfo from(NotificationScheduleWithScenarioDomainDto dto) {
		return new NotificationScheduleInfo(
			dto.id(),
			dto.dayOfWeek(),
			dto.notificationTime(),
			dto.scenarioId(),
			dto.scenarioName(),
			dto.scenarioLevel()
		);
	}
}
