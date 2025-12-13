package com.voco.voco.app.notification.infrastructure.repository;

import org.springframework.stereotype.Repository;

import com.voco.voco.app.notification.domain.interfaces.NotificationScheduleQueryRepository;
import com.voco.voco.app.notification.domain.interfaces.dto.in.NotificationScheduleDomainDto;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NotificationScheduleQueryRepositoryImpl implements NotificationScheduleQueryRepository {

	private final NotificationScheduleJpaRepository notificationScheduleJpaRepository;

	@Override
	public boolean exists(NotificationScheduleDomainDto dto) {
		return notificationScheduleJpaRepository.existsByMemberIdAndDayOfWeekAndNotificationTime(
			dto.memberId(), dto.dayOfWeek(), dto.notificationTime());
	}
}