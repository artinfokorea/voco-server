package com.voco.voco.app.notification.presentation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;
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
import com.voco.voco.app.member.domain.model.Category;
import com.voco.voco.app.member.domain.model.Level;
import com.voco.voco.app.member.presentation.controller.dto.in.SignUpRequest;
import com.voco.voco.app.notification.presentation.controller.dto.in.CreateNotificationScheduleRequest;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("POST /api/v1/notification-schedules")
class CreateNotificationScheduleApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private static final String NOTIFICATION_SCHEDULE_URL = "/api/v1/notification-schedules";
	private static final String SIGN_UP_URL = "/api/v1/members/sign-up";
	private static final String SIGN_IN_URL = "/api/v1/auth/sign-in";
	private static final String VALID_PASSWORD = "Password1!";

	private String accessToken;

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
			Level.BEGINNER,
			Set.of(Category.DAILY)
		);
		mockMvc.perform(post(SIGN_UP_URL)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(signUpRequest)));

		SignInRequest signInRequest = new SignInRequest(email, VALID_PASSWORD);
		String response = mockMvc.perform(post(SIGN_IN_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signInRequest)))
			.andReturn()
			.getResponse()
			.getContentAsString();

		accessToken = JsonPath.read(response, "$.item.accessToken");
	}

	@Test
	@DisplayName("유효한 요청으로 알림 스케줄 생성에 성공한다")
	void createNotificationSchedule_Success() throws Exception {
		// given
		CreateNotificationScheduleRequest request = new CreateNotificationScheduleRequest(
			DayOfWeek.MONDAY,
			LocalTime.of(9, 0)
		);

		// when
		ResultActions result = mockMvc.perform(post(NOTIFICATION_SCHEDULE_URL)
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
	@DisplayName("동일한 멤버, 요일, 시간의 스케줄이 이미 존재하면 생성에 실패한다")
	void createNotificationSchedule_Duplicated_Fail() throws Exception {
		// given
		CreateNotificationScheduleRequest request = new CreateNotificationScheduleRequest(
			DayOfWeek.MONDAY,
			LocalTime.of(9, 0)
		);

		mockMvc.perform(post(NOTIFICATION_SCHEDULE_URL)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// when
		ResultActions result = mockMvc.perform(post(NOTIFICATION_SCHEDULE_URL)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		result
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.type").value("FAIL"))
			.andExpect(jsonPath("$.exception.errorNo").value("NOTIFICATION-1"));
	}

	@Test
	@DisplayName("같은 요일이라도 다른 시간이면 생성에 성공한다")
	void createNotificationSchedule_SameDayDifferentTime_Success() throws Exception {
		// given
		CreateNotificationScheduleRequest firstRequest = new CreateNotificationScheduleRequest(
			DayOfWeek.MONDAY,
			LocalTime.of(9, 0)
		);

		mockMvc.perform(post(NOTIFICATION_SCHEDULE_URL)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(firstRequest)));

		CreateNotificationScheduleRequest secondRequest = new CreateNotificationScheduleRequest(
			DayOfWeek.MONDAY,
			LocalTime.of(18, 0)
		);

		// when
		ResultActions result = mockMvc.perform(post(NOTIFICATION_SCHEDULE_URL)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(secondRequest)));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("SUCCESS"));
	}

	@Test
	@DisplayName("같은 시간이라도 다른 요일이면 생성에 성공한다")
	void createNotificationSchedule_DifferentDaySameTime_Success() throws Exception {
		// given
		CreateNotificationScheduleRequest firstRequest = new CreateNotificationScheduleRequest(
			DayOfWeek.MONDAY,
			LocalTime.of(9, 0)
		);

		mockMvc.perform(post(NOTIFICATION_SCHEDULE_URL)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(firstRequest)));

		CreateNotificationScheduleRequest secondRequest = new CreateNotificationScheduleRequest(
			DayOfWeek.FRIDAY,
			LocalTime.of(9, 0)
		);

		// when
		ResultActions result = mockMvc.perform(post(NOTIFICATION_SCHEDULE_URL)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(secondRequest)));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("SUCCESS"));
	}

	@Test
	@DisplayName("토큰이 없으면 알림 스케줄 생성에 실패한다")
	void createNotificationSchedule_NoToken_Fail() throws Exception {
		// given
		CreateNotificationScheduleRequest request = new CreateNotificationScheduleRequest(
			DayOfWeek.MONDAY,
			LocalTime.of(9, 0)
		);

		// when
		ResultActions result = mockMvc.perform(post(NOTIFICATION_SCHEDULE_URL)
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
	@DisplayName("필수 값이 없으면 알림 스케줄 생성에 실패한다")
	void createNotificationSchedule_MissingRequiredField_Fail() throws Exception {
		// given
		String requestJson = """
			{
				"dayOfWeek": "MONDAY"
			}
			""";

		// when
		ResultActions result = mockMvc.perform(post(NOTIFICATION_SCHEDULE_URL)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestJson));

		// then
		result
			.andDo(print())
			.andExpect(status().isBadRequest());
	}
}
