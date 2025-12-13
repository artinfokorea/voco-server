package com.voco.voco.app.notification.domain.interfaces;

import com.voco.voco.app.notification.domain.interfaces.dto.in.NotificationScheduleDomainDto;

public interface NotificationScheduleQueryRepository {

	boolean exists(NotificationScheduleDomainDto dto);
}