package com.voco.voco.app.notification.infrastructure.repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voco.voco.app.notification.domain.model.NotificationScheduleEntity;

public interface NotificationScheduleJpaRepository extends JpaRepository<NotificationScheduleEntity, Long> {

	boolean existsByMemberIdAndDayOfWeekAndNotificationTime(Long memberId, DayOfWeek dayOfWeek, LocalTime notificationTime);

	Optional<NotificationScheduleEntity> findByIdAndMemberId(Long id, Long memberId);
}
