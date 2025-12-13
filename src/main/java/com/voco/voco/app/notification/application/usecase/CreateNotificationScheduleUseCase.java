package com.voco.voco.app.notification.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voco.voco.app.notification.application.usecase.dto.in.CreateNotificationScheduleUseCaseDto;
import com.voco.voco.app.notification.domain.interfaces.NotificationScheduleCommandRepository;
import com.voco.voco.app.notification.domain.interfaces.NotificationScheduleQueryRepository;
import com.voco.voco.app.notification.domain.model.NotificationScheduleEntity;
import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateNotificationScheduleUseCase {

	private final NotificationScheduleCommandRepository notificationScheduleCommandRepository;
	private final NotificationScheduleQueryRepository notificationScheduleQueryRepository;

	public void execute(CreateNotificationScheduleUseCaseDto dto) {
		if (notificationScheduleQueryRepository.exists(dto.toDomainDto())) {
			throw new CoreException(ApiErrorType.DUPLICATED_NOTIFICATION_SCHEDULE);
		}

		notificationScheduleCommandRepository.save(
			NotificationScheduleEntity.create(dto.memberId(), dto.dayOfWeek(), dto.notificationTime())
		);
	}
}