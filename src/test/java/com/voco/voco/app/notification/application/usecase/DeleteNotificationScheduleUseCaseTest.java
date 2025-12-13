package com.voco.voco.app.notification.application.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.voco.voco.app.notification.application.usecase.dto.in.DeleteNotificationScheduleUseCaseDto;
import com.voco.voco.app.notification.domain.interfaces.NotificationScheduleQueryRepository;
import com.voco.voco.app.notification.domain.model.NotificationScheduleEntity;
import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;

@ExtendWith(MockitoExtension.class)
class DeleteNotificationScheduleUseCaseTest {

	@InjectMocks
	private DeleteNotificationScheduleUseCase deleteNotificationScheduleUseCase;

	@Mock
	private NotificationScheduleQueryRepository notificationScheduleQueryRepository;

	private static final Long SCHEDULE_ID = 1L;
	private static final Long MEMBER_ID = 1L;

	@Nested
	@DisplayName("execute 메서드")
	class Execute {

		@Test
		@DisplayName("알림 스케줄 삭제에 성공한다")
		void deleteNotificationSchedule_Success() {
			// given
			DeleteNotificationScheduleUseCaseDto dto = new DeleteNotificationScheduleUseCaseDto(
				SCHEDULE_ID, MEMBER_ID);

			NotificationScheduleEntity schedule = NotificationScheduleEntity.create(
				MEMBER_ID, DayOfWeek.MONDAY, LocalTime.of(9, 0));

			given(notificationScheduleQueryRepository.findByIdAndMemberId(dto.id(), dto.memberId()))
				.willReturn(Optional.of(schedule));

			// when
			deleteNotificationScheduleUseCase.execute(dto);

			// then
			then(notificationScheduleQueryRepository).should().findByIdAndMemberId(dto.id(), dto.memberId());
		}

		@Test
		@DisplayName("존재하지 않는 스케줄 삭제 시 예외가 발생한다")
		void deleteNotificationSchedule_NotFound_ThrowsException() {
			// given
			DeleteNotificationScheduleUseCaseDto dto = new DeleteNotificationScheduleUseCaseDto(
				SCHEDULE_ID, MEMBER_ID);

			given(notificationScheduleQueryRepository.findByIdAndMemberId(dto.id(), dto.memberId()))
				.willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> deleteNotificationScheduleUseCase.execute(dto))
				.isInstanceOf(CoreException.class)
				.satisfies(exception -> {
					CoreException coreException = (CoreException)exception;
					assertThat(coreException.getErrorType()).isEqualTo(ApiErrorType.NOTIFICATION_SCHEDULE_NOT_FOUND);
				});
		}

		@Test
		@DisplayName("다른 사용자의 스케줄 삭제 시 예외가 발생한다")
		void deleteNotificationSchedule_OtherMember_ThrowsException() {
			// given
			Long otherMemberId = 2L;
			DeleteNotificationScheduleUseCaseDto dto = new DeleteNotificationScheduleUseCaseDto(
				SCHEDULE_ID, otherMemberId);

			given(notificationScheduleQueryRepository.findByIdAndMemberId(dto.id(), dto.memberId()))
				.willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> deleteNotificationScheduleUseCase.execute(dto))
				.isInstanceOf(CoreException.class)
				.satisfies(exception -> {
					CoreException coreException = (CoreException)exception;
					assertThat(coreException.getErrorType()).isEqualTo(ApiErrorType.NOTIFICATION_SCHEDULE_NOT_FOUND);
				});
		}
	}
}
