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

import com.voco.voco.app.notification.application.usecase.dto.in.UpdateNotificationScheduleUseCaseDto;
import com.voco.voco.app.notification.domain.interfaces.NotificationScheduleQueryRepository;
import com.voco.voco.app.notification.domain.model.NotificationScheduleEntity;
import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;

@ExtendWith(MockitoExtension.class)
class UpdateNotificationScheduleUseCaseTest {

	@InjectMocks
	private UpdateNotificationScheduleUseCase updateNotificationScheduleUseCase;

	@Mock
	private NotificationScheduleQueryRepository notificationScheduleQueryRepository;

	private static final Long SCHEDULE_ID = 1L;
	private static final Long MEMBER_ID = 1L;
	private static final DayOfWeek ORIGINAL_DAY = DayOfWeek.MONDAY;
	private static final LocalTime ORIGINAL_TIME = LocalTime.of(9, 0);
	private static final DayOfWeek NEW_DAY = DayOfWeek.FRIDAY;
	private static final LocalTime NEW_TIME = LocalTime.of(18, 0);

	@Nested
	@DisplayName("execute 메서드")
	class Execute {

		@Test
		@DisplayName("알림 스케줄 수정에 성공한다")
		void updateNotificationSchedule_Success() {
			// given
			UpdateNotificationScheduleUseCaseDto dto = new UpdateNotificationScheduleUseCaseDto(
				SCHEDULE_ID, MEMBER_ID, NEW_DAY, NEW_TIME);

			NotificationScheduleEntity schedule = NotificationScheduleEntity.create(
				MEMBER_ID, ORIGINAL_DAY, ORIGINAL_TIME);

			given(notificationScheduleQueryRepository.findByIdAndMemberId(dto.id(), dto.memberId()))
				.willReturn(Optional.of(schedule));

			// when
			updateNotificationScheduleUseCase.execute(dto);

			// then
			assertThat(schedule.getDayOfWeek()).isEqualTo(NEW_DAY);
			assertThat(schedule.getNotificationTime()).isEqualTo(NEW_TIME);
		}

		@Test
		@DisplayName("존재하지 않는 스케줄 수정 시 예외가 발생한다")
		void updateNotificationSchedule_NotFound_ThrowsException() {
			// given
			UpdateNotificationScheduleUseCaseDto dto = new UpdateNotificationScheduleUseCaseDto(
				SCHEDULE_ID, MEMBER_ID, NEW_DAY, NEW_TIME);

			given(notificationScheduleQueryRepository.findByIdAndMemberId(dto.id(), dto.memberId()))
				.willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> updateNotificationScheduleUseCase.execute(dto))
				.isInstanceOf(CoreException.class)
				.satisfies(exception -> {
					CoreException coreException = (CoreException)exception;
					assertThat(coreException.getErrorType()).isEqualTo(ApiErrorType.NOTIFICATION_SCHEDULE_NOT_FOUND);
				});
		}

		@Test
		@DisplayName("다른 사용자의 스케줄 수정 시 예외가 발생한다")
		void updateNotificationSchedule_OtherMember_ThrowsException() {
			// given
			Long otherMemberId = 2L;
			UpdateNotificationScheduleUseCaseDto dto = new UpdateNotificationScheduleUseCaseDto(
				SCHEDULE_ID, otherMemberId, NEW_DAY, NEW_TIME);

			given(notificationScheduleQueryRepository.findByIdAndMemberId(dto.id(), dto.memberId()))
				.willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> updateNotificationScheduleUseCase.execute(dto))
				.isInstanceOf(CoreException.class)
				.satisfies(exception -> {
					CoreException coreException = (CoreException)exception;
					assertThat(coreException.getErrorType()).isEqualTo(ApiErrorType.NOTIFICATION_SCHEDULE_NOT_FOUND);
				});
		}

		@Test
		@DisplayName("요일만 변경해도 수정에 성공한다")
		void updateNotificationSchedule_OnlyDayChange_Success() {
			// given
			UpdateNotificationScheduleUseCaseDto dto = new UpdateNotificationScheduleUseCaseDto(
				SCHEDULE_ID, MEMBER_ID, NEW_DAY, ORIGINAL_TIME);

			NotificationScheduleEntity schedule = NotificationScheduleEntity.create(
				MEMBER_ID, ORIGINAL_DAY, ORIGINAL_TIME);

			given(notificationScheduleQueryRepository.findByIdAndMemberId(dto.id(), dto.memberId()))
				.willReturn(Optional.of(schedule));

			// when
			updateNotificationScheduleUseCase.execute(dto);

			// then
			assertThat(schedule.getDayOfWeek()).isEqualTo(NEW_DAY);
			assertThat(schedule.getNotificationTime()).isEqualTo(ORIGINAL_TIME);
		}

		@Test
		@DisplayName("시간만 변경해도 수정에 성공한다")
		void updateNotificationSchedule_OnlyTimeChange_Success() {
			// given
			UpdateNotificationScheduleUseCaseDto dto = new UpdateNotificationScheduleUseCaseDto(
				SCHEDULE_ID, MEMBER_ID, ORIGINAL_DAY, NEW_TIME);

			NotificationScheduleEntity schedule = NotificationScheduleEntity.create(
				MEMBER_ID, ORIGINAL_DAY, ORIGINAL_TIME);

			given(notificationScheduleQueryRepository.findByIdAndMemberId(dto.id(), dto.memberId()))
				.willReturn(Optional.of(schedule));

			// when
			updateNotificationScheduleUseCase.execute(dto);

			// then
			assertThat(schedule.getDayOfWeek()).isEqualTo(ORIGINAL_DAY);
			assertThat(schedule.getNotificationTime()).isEqualTo(NEW_TIME);
		}
	}
}
