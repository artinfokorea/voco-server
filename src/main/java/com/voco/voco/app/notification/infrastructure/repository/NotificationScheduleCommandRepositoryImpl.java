package com.voco.voco.app.notification.infrastructure.repository;

import org.springframework.stereotype.Repository;

import com.voco.voco.app.notification.domain.interfaces.NotificationScheduleCommandRepository;
import com.voco.voco.app.notification.domain.model.NotificationScheduleEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NotificationScheduleCommandRepositoryImpl implements NotificationScheduleCommandRepository {

	private final NotificationScheduleJpaRepository notificationScheduleJpaRepository;

	@Override
	public NotificationScheduleEntity save(NotificationScheduleEntity notificationSchedule) {
		return notificationScheduleJpaRepository.save(notificationSchedule);
	}

	@Override
	public void clearScenarioIdByScenarioId(Long scenarioId) {
		notificationScheduleJpaRepository.clearScenarioIdByScenarioId(scenarioId);
	}
}