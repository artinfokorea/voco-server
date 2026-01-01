package com.voco.voco.app.notification.domain.interfaces.dto.out;

import java.time.DayOfWeek;
import java.time.LocalTime;

import com.voco.voco.app.scenario.domain.model.Level;

public record NotificationScheduleWithScenarioDomainDto(
	Long id,
	DayOfWeek dayOfWeek,
	LocalTime notificationTime,
	Long scenarioId,
	String scenarioName,
	Level scenarioLevel
) {
}
