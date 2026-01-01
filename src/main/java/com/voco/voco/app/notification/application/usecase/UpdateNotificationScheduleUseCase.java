package com.voco.voco.app.notification.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voco.voco.app.notification.application.usecase.dto.in.UpdateNotificationScheduleUseCaseDto;
import com.voco.voco.app.notification.domain.interfaces.NotificationScheduleQueryRepository;
import com.voco.voco.app.notification.domain.model.NotificationScheduleEntity;
import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateNotificationScheduleUseCase {

	private final NotificationScheduleQueryRepository notificationScheduleQueryRepository;

	public void execute(UpdateNotificationScheduleUseCaseDto dto) {
		NotificationScheduleEntity schedule = notificationScheduleQueryRepository
			.findByIdAndMemberId(dto.id(), dto.memberId())
			.orElseThrow(() -> new CoreException(ApiErrorType.NOTIFICATION_SCHEDULE_NOT_FOUND));

		schedule.update(dto.dayOfWeek(), dto.notificationTime(), dto.scenarioId());
	}
}
