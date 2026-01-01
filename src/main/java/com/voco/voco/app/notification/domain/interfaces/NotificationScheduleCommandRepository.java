package com.voco.voco.app.notification.domain.interfaces;

import com.voco.voco.app.notification.domain.model.NotificationScheduleEntity;

public interface NotificationScheduleCommandRepository {

	NotificationScheduleEntity save(NotificationScheduleEntity notificationSchedule);

	void clearScenarioIdByScenarioId(Long scenarioId);
}