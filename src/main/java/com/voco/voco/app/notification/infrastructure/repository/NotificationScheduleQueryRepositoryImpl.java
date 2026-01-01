package com.voco.voco.app.notification.infrastructure.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.voco.voco.app.notification.domain.interfaces.NotificationScheduleQueryRepository;
import com.voco.voco.app.notification.domain.interfaces.dto.in.NotificationScheduleDomainDto;
import com.voco.voco.app.notification.domain.interfaces.dto.out.NotificationScheduleWithScenarioDomainDto;
import com.voco.voco.app.notification.domain.model.NotificationScheduleEntity;
import com.voco.voco.app.notification.domain.model.QNotificationScheduleEntity;
import com.voco.voco.app.scenario.domain.model.QConversationScenarioEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NotificationScheduleQueryRepositoryImpl implements NotificationScheduleQueryRepository {

	private final NotificationScheduleJpaRepository notificationScheduleJpaRepository;
	private final JPAQueryFactory queryFactory;

	private static final QNotificationScheduleEntity schedule = QNotificationScheduleEntity.notificationScheduleEntity;
	private static final QConversationScenarioEntity scenario = QConversationScenarioEntity.conversationScenarioEntity;

	@Override
	public boolean exists(NotificationScheduleDomainDto dto) {
		return notificationScheduleJpaRepository.existsByMemberIdAndDayOfWeekAndNotificationTime(
			dto.memberId(), dto.dayOfWeek(), dto.notificationTime());
	}

	@Override
	public Optional<NotificationScheduleEntity> findByIdAndMemberId(Long id, Long memberId) {
		return notificationScheduleJpaRepository.findByIdAndMemberId(id, memberId);
	}

	@Override
	public List<NotificationScheduleEntity> findAllByMemberId(Long memberId) {
		return notificationScheduleJpaRepository.findAllByMemberId(memberId);
	}

	@Override
	public List<NotificationScheduleWithScenarioDomainDto> findAllWithScenarioByMemberId(Long memberId) {
		return queryFactory
			.select(Projections.constructor(
				NotificationScheduleWithScenarioDomainDto.class,
				schedule.id,
				schedule.dayOfWeek,
				schedule.notificationTime,
				scenario.id,
				scenario.name,
				scenario.level
			))
			.from(schedule)
			.leftJoin(scenario).on(schedule.scenarioId.eq(scenario.id))
			.where(schedule.memberId.eq(memberId))
			.fetch();
	}
}