package com.voco.voco.app.notification.presentation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.voco.voco.app.auth.presentation.controller.dto.in.SignInRequest;
import com.voco.voco.app.member.domain.model.Level;
import com.voco.voco.app.member.presentation.controller.dto.in.SignUpRequest;
import com.voco.voco.app.notification.domain.model.NotificationScheduleEntity;
import com.voco.voco.app.notification.infrastructure.repository.NotificationScheduleJpaRepository;
import com.voco.voco.app.notification.presentation.controller.dto.in.CreateNotificationScheduleRequest;
import com.voco.voco.app.notification.presentation.controller.dto.in.UpdateNotificationScheduleRequest;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("PUT /api/v1/notification-schedules/{id}")
class UpdateNotificationScheduleApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private NotificationScheduleJpaRepository notificationScheduleJpaRepository;

	private static final String NOTIFICATION_SCHEDULE_URL = "/api/v1/notification-schedules";
	private static final String SIGN_UP_URL = "/api/v1/members/sign-up";
	private static final String SIGN_IN_URL = "/api/v1/auth/sign-in";
	private static final String VALID_PASSWORD = "Password1!";

	private String accessToken;
	private Long memberId;

	private String uniqueEmail() {
		return "test-" + UUID.randomUUID() + "@example.com";
	}

	@BeforeEach
	void setUp() throws Exception {
		String email = uniqueEmail();

		SignUpRequest signUpRequest = new SignUpRequest(
			"홍길동",
			"Hong Gildong",
			email,
			VALID_PASSWORD,
			Level.BEGINNER
		);
		String signUpResponse = mockMvc.perform(post(SIGN_UP_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signUpRequest)))
			.andReturn()
			.getResponse()
			.getContentAsString();

		memberId = ((Number)JsonPath.read(signUpResponse, "$.item.id")).longValue();

		SignInRequest signInRequest = new SignInRequest(email, VALID_PASSWORD);
		String signInResponse = mockMvc.perform(post(SIGN_IN_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signInRequest)))
			.andReturn()
			.getResponse()
			.getContentAsString();

		accessToken = JsonPath.read(signInResponse, "$.item.accessToken");
	}

	private Long createSchedule(DayOfWeek dayOfWeek, LocalTime time) throws Exception {
		CreateNotificationScheduleRequest request = new CreateNotificationScheduleRequest(dayOfWeek, time);
		mockMvc.perform(post(NOTIFICATION_SCHEDULE_URL)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		return notificationScheduleJpaRepository
			.findByIdAndMemberId(
				notificationScheduleJpaRepository.findAll().get(0).getId(),
				memberId
			)
			.map(NotificationScheduleEntity::getId)
			.orElseThrow();
	}

	@Test
	@DisplayName("유효한 요청으로 알림 스케줄 수정에 성공한다")
	void updateNotificationSchedule_Success() throws Exception {
		// given
		Long scheduleId = createSchedule(DayOfWeek.MONDAY, LocalTime.of(9, 0));

		UpdateNotificationScheduleRequest request = new UpdateNotificationScheduleRequest(
			DayOfWeek.FRIDAY,
			LocalTime.of(18, 0)
		);

		// when
		ResultActions result = mockMvc.perform(put(NOTIFICATION_SCHEDULE_URL + "/" + scheduleId)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("SUCCESS"));
	}

	@Test
	@DisplayName("존재하지 않는 스케줄 수정 시 실패한다")
	void updateNotificationSchedule_NotFound_Fail() throws Exception {
		// given
		Long nonExistentId = 99999L;

		UpdateNotificationScheduleRequest request = new UpdateNotificationScheduleRequest(
			DayOfWeek.FRIDAY,
			LocalTime.of(18, 0)
		);

		// when
		ResultActions result = mockMvc.perform(put(NOTIFICATION_SCHEDULE_URL + "/" + nonExistentId)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		result
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.type").value("FAIL"))
			.andExpect(jsonPath("$.exception.errorNo").value("NOTIFICATION-2"));
	}

	@Test
	@DisplayName("다른 사용자의 스케줄 수정 시 실패한다")
	void updateNotificationSchedule_OtherUserSchedule_Fail() throws Exception {
		// given
		Long scheduleId = createSchedule(DayOfWeek.MONDAY, LocalTime.of(9, 0));

		String otherEmail = uniqueEmail();
		SignUpRequest otherSignUpRequest = new SignUpRequest(
			"김철수",
			"Kim Cheolsu",
			otherEmail,
			VALID_PASSWORD,
			Level.INTERMEDIATE
		);
		mockMvc.perform(post(SIGN_UP_URL)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(otherSignUpRequest)));

		SignInRequest otherSignInRequest = new SignInRequest(otherEmail, VALID_PASSWORD);
		String otherSignInResponse = mockMvc.perform(post(SIGN_IN_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(otherSignInRequest)))
			.andReturn()
			.getResponse()
			.getContentAsString();

		String otherAccessToken = JsonPath.read(otherSignInResponse, "$.item.accessToken");

		UpdateNotificationScheduleRequest request = new UpdateNotificationScheduleRequest(
			DayOfWeek.FRIDAY,
			LocalTime.of(18, 0)
		);

		// when
		ResultActions result = mockMvc.perform(put(NOTIFICATION_SCHEDULE_URL + "/" + scheduleId)
			.header("Authorization", "Bearer " + otherAccessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		result
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.type").value("FAIL"))
			.andExpect(jsonPath("$.exception.errorNo").value("NOTIFICATION-2"));
	}

	@Test
	@DisplayName("토큰이 없으면 알림 스케줄 수정에 실패한다")
	void updateNotificationSchedule_NoToken_Fail() throws Exception {
		// given
		Long scheduleId = createSchedule(DayOfWeek.MONDAY, LocalTime.of(9, 0));

		UpdateNotificationScheduleRequest request = new UpdateNotificationScheduleRequest(
			DayOfWeek.FRIDAY,
			LocalTime.of(18, 0)
		);

		// when
		ResultActions result = mockMvc.perform(put(NOTIFICATION_SCHEDULE_URL + "/" + scheduleId)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		result
			.andDo(print())
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.type").value("FAIL"))
			.andExpect(jsonPath("$.exception.errorNo").value("AUTH-1"));
	}

	@Test
	@DisplayName("필수 값이 없으면 알림 스케줄 수정에 실패한다")
	void updateNotificationSchedule_MissingRequiredField_Fail() throws Exception {
		// given
		Long scheduleId = createSchedule(DayOfWeek.MONDAY, LocalTime.of(9, 0));

		String requestJson = """
			{
				"dayOfWeek": "FRIDAY"
			}
			""";

		// when
		ResultActions result = mockMvc.perform(put(NOTIFICATION_SCHEDULE_URL + "/" + scheduleId)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestJson));

		// then
		result
			.andDo(print())
			.andExpect(status().isBadRequest());
	}
}
