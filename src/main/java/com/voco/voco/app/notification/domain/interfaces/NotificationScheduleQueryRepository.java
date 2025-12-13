package com.voco.voco.app.notification.domain.interfaces;

import java.util.Optional;

import com.voco.voco.app.notification.domain.interfaces.dto.in.NotificationScheduleDomainDto;
import com.voco.voco.app.notification.domain.model.NotificationScheduleEntity;

public interface NotificationScheduleQueryRepository {

	boolean exists(NotificationScheduleDomainDto dto);

	Optional<NotificationScheduleEntity> findByIdAndMemberId(Long id, Long memberId);
}