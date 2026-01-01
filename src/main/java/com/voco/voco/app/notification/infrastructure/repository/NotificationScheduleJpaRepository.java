package com.voco.voco.app.notification.infrastructure.repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.voco.voco.app.notification.domain.model.NotificationScheduleEntity;

public interface NotificationScheduleJpaRepository extends JpaRepository<NotificationScheduleEntity, Long> {

	boolean existsByMemberIdAndDayOfWeekAndNotificationTime(Long memberId, DayOfWeek dayOfWeek, LocalTime notificationTime);

	Optional<NotificationScheduleEntity> findByIdAndMemberId(Long id, Long memberId);

	List<NotificationScheduleEntity> findAllByMemberId(Long memberId);

	@Modifying
	@Query("UPDATE NotificationScheduleEntity n SET n.scenarioId = null WHERE n.scenarioId = :scenarioId")
	void clearScenarioIdByScenarioId(@Param("scenarioId") Long scenarioId);
}
