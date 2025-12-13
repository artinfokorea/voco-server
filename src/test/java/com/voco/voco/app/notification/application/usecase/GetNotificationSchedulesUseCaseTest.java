package com.voco.voco.app.notification.application.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.voco.voco.app.notification.application.usecase.dto.out.NotificationScheduleInfo;
import com.voco.voco.app.notification.domain.interfaces.NotificationScheduleQueryRepository;
import com.voco.voco.app.notification.domain.model.NotificationScheduleEntity;

@ExtendWith(MockitoExtension.class)
class GetNotificationSchedulesUseCaseTest {

	@InjectMocks
	private GetNotificationSchedulesUseCase getNotificationSchedulesUseCase;

	@Mock
	private NotificationScheduleQueryRepository notificationScheduleQueryRepository;

	private static final Long MEMBER_ID = 1L;

	@Nested
	@DisplayName("execute 메서드")
	class Execute {

		@Test
		@DisplayName("알림 스케줄 전체 조회에 성공한다")
		void getNotificationSchedules_Success() {
			// given
			NotificationScheduleEntity schedule1 = createScheduleWithId(1L, DayOfWeek.MONDAY, LocalTime.of(9, 0));
			NotificationScheduleEntity schedule2 = createScheduleWithId(2L, DayOfWeek.FRIDAY, LocalTime.of(18, 0));

			given(notificationScheduleQueryRepository.findAllByMemberId(MEMBER_ID))
				.willReturn(List.of(schedule1, schedule2));

			// when
			List<NotificationScheduleInfo> result = getNotificationSchedulesUseCase.execute(MEMBER_ID);

			// then
			assertThat(result).hasSize(2);
			assertThat(result.get(0).id()).isEqualTo(1L);
			assertThat(result.get(0).dayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
			assertThat(result.get(0).notificationTime()).isEqualTo(LocalTime.of(9, 0));
			assertThat(result.get(1).id()).isEqualTo(2L);
			assertThat(result.get(1).dayOfWeek()).isEqualTo(DayOfWeek.FRIDAY);
			assertThat(result.get(1).notificationTime()).isEqualTo(LocalTime.of(18, 0));
		}

		@Test
		@DisplayName("스케줄이 없으면 빈 리스트를 반환한다")
		void getNotificationSchedules_Empty_ReturnsEmptyList() {
			// given
			given(notificationScheduleQueryRepository.findAllByMemberId(MEMBER_ID))
				.willReturn(Collections.emptyList());

			// when
			List<NotificationScheduleInfo> result = getNotificationSchedulesUseCase.execute(MEMBER_ID);

			// then
			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("하나의 스케줄만 있어도 조회에 성공한다")
		void getNotificationSchedules_SingleSchedule_Success() {
			// given
			NotificationScheduleEntity schedule = createScheduleWithId(1L, DayOfWeek.MONDAY, LocalTime.of(9, 0));

			given(notificationScheduleQueryRepository.findAllByMemberId(MEMBER_ID))
				.willReturn(List.of(schedule));

			// when
			List<NotificationScheduleInfo> result = getNotificationSchedulesUseCase.execute(MEMBER_ID);

			// then
			assertThat(result).hasSize(1);
			assertThat(result.get(0).dayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
		}
	}

	private NotificationScheduleEntity createScheduleWithId(Long id, DayOfWeek dayOfWeek, LocalTime time) {
		NotificationScheduleEntity schedule = NotificationScheduleEntity.create(MEMBER_ID, dayOfWeek, time);
		try {
			java.lang.reflect.Field idField = NotificationScheduleEntity.class.getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(schedule, id);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return schedule;
	}
}
