package com.voco.voco.app.notification.application.usecase;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voco.voco.app.notification.application.usecase.dto.out.NotificationScheduleInfo;
import com.voco.voco.app.notification.domain.interfaces.NotificationScheduleQueryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetNotificationSchedulesUseCase {

	private final NotificationScheduleQueryRepository notificationScheduleQueryRepository;

	public List<NotificationScheduleInfo> execute(Long memberId) {
		return notificationScheduleQueryRepository.findAllWithScenarioByMemberId(memberId)
			.stream()
			.map(NotificationScheduleInfo::from)
			.toList();
	}
}
