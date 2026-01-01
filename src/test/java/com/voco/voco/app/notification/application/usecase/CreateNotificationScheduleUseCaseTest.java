package com.voco.voco.app.notification.application.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.voco.voco.app.notification.application.usecase.dto.in.CreateNotificationScheduleUseCaseDto;
import com.voco.voco.app.notification.domain.interfaces.NotificationScheduleCommandRepository;
import com.voco.voco.app.notification.domain.interfaces.NotificationScheduleQueryRepository;
import com.voco.voco.app.notification.domain.model.NotificationScheduleEntity;
import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;

@ExtendWith(MockitoExtension.class)
class CreateNotificationScheduleUseCaseTest {

	@InjectMocks
	private CreateNotificationScheduleUseCase createNotificationScheduleUseCase;

	@Mock
	private NotificationScheduleCommandRepository notificationScheduleCommandRepository;

	@Mock
	private NotificationScheduleQueryRepository notificationScheduleQueryRepository;

	private static final Long MEMBER_ID = 1L;
	private static final DayOfWeek DAY_OF_WEEK = DayOfWeek.MONDAY;
	private static final LocalTime NOTIFICATION_TIME = LocalTime.of(9, 0);

	@Nested
	@DisplayName("execute 메서드")
	class Execute {

		@Test
		@DisplayName("알림 스케줄 생성에 성공한다")
		void createNotificationSchedule_Success() {
			// given
			CreateNotificationScheduleUseCaseDto dto = new CreateNotificationScheduleUseCaseDto(
				MEMBER_ID, DAY_OF_WEEK, NOTIFICATION_TIME, null);

			given(notificationScheduleQueryRepository.exists(dto.toDomainDto())).willReturn(false);

			// when
			createNotificationScheduleUseCase.execute(dto);

			// then
			then(notificationScheduleCommandRepository).should().save(any(NotificationScheduleEntity.class));
		}

		@Test
		@DisplayName("동일한 멤버, 요일, 시간의 스케줄이 이미 존재하면 예외가 발생한다")
		void createNotificationSchedule_Duplicated_ThrowsException() {
			// given
			CreateNotificationScheduleUseCaseDto dto = new CreateNotificationScheduleUseCaseDto(
				MEMBER_ID, DAY_OF_WEEK, NOTIFICATION_TIME, null);

			given(notificationScheduleQueryRepository.exists(dto.toDomainDto())).willReturn(true);

			// when & then
			assertThatThrownBy(() -> createNotificationScheduleUseCase.execute(dto))
				.isInstanceOf(CoreException.class)
				.satisfies(exception -> {
					CoreException coreException = (CoreException)exception;
					assertThat(coreException.getErrorType()).isEqualTo(ApiErrorType.DUPLICATED_NOTIFICATION_SCHEDULE);
				});

			then(notificationScheduleCommandRepository).shouldHaveNoInteractions();
		}

		@Test
		@DisplayName("같은 멤버, 같은 요일이라도 다른 시간이면 생성에 성공한다")
		void createNotificationSchedule_SameDayDifferentTime_Success() {
			// given
			LocalTime differentTime = LocalTime.of(18, 0);
			CreateNotificationScheduleUseCaseDto dto = new CreateNotificationScheduleUseCaseDto(
				MEMBER_ID, DAY_OF_WEEK, differentTime, null);

			given(notificationScheduleQueryRepository.exists(dto.toDomainDto())).willReturn(false);

			// when
			createNotificationScheduleUseCase.execute(dto);

			// then
			then(notificationScheduleCommandRepository).should().save(any(NotificationScheduleEntity.class));
		}

		@Test
		@DisplayName("같은 멤버, 같은 시간이라도 다른 요일이면 생성에 성공한다")
		void createNotificationSchedule_DifferentDaySameTime_Success() {
			// given
			DayOfWeek differentDay = DayOfWeek.FRIDAY;
			CreateNotificationScheduleUseCaseDto dto = new CreateNotificationScheduleUseCaseDto(
				MEMBER_ID, differentDay, NOTIFICATION_TIME, null);

			given(notificationScheduleQueryRepository.exists(dto.toDomainDto())).willReturn(false);

			// when
			createNotificationScheduleUseCase.execute(dto);

			// then
			then(notificationScheduleCommandRepository).should().save(any(NotificationScheduleEntity.class));
		}
	}
}
