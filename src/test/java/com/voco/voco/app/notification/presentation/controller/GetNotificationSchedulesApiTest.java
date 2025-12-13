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
@DisplayName("GET /api/v1/notification-schedules")
class GetNotificationSchedulesApiTest {

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
		String signInResponse = mockMvc.perform(post(SIGN_IN_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signInRequest)))
			.andReturn()
			.getResponse()
			.getContentAsString();

		accessToken = JsonPath.read(signInResponse, "$.item.accessToken");
	}

	private void createSchedule(DayOfWeek dayOfWeek, LocalTime time) throws Exception {
		CreateNotificationScheduleRequest request = new CreateNotificationScheduleRequest(dayOfWeek, time);
		mockMvc.perform(post(NOTIFICATION_SCHEDULE_URL)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));
	}

	@Test
	@DisplayName("알림 스케줄 전체 조회에 성공한다")
	void getNotificationSchedules_Success() throws Exception {
		// given
		createSchedule(DayOfWeek.MONDAY, LocalTime.of(9, 0));
		createSchedule(DayOfWeek.FRIDAY, LocalTime.of(18, 0));

		// when
		ResultActions result = mockMvc.perform(get(NOTIFICATION_SCHEDULE_URL)
			.header("Authorization", "Bearer " + accessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("SUCCESS"))
			.andExpect(jsonPath("$.item").isArray())
			.andExpect(jsonPath("$.item.length()").value(2));
	}

	@Test
	@DisplayName("스케줄이 없으면 빈 리스트를 반환한다")
	void getNotificationSchedules_Empty_ReturnsEmptyList() throws Exception {
		// when
		ResultActions result = mockMvc.perform(get(NOTIFICATION_SCHEDULE_URL)
			.header("Authorization", "Bearer " + accessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("SUCCESS"))
			.andExpect(jsonPath("$.item").isArray())
			.andExpect(jsonPath("$.item.length()").value(0));
	}

	@Test
	@DisplayName("다른 사용자의 스케줄은 조회되지 않는다")
	void getNotificationSchedules_OtherUserSchedule_NotIncluded() throws Exception {
		// given
		createSchedule(DayOfWeek.MONDAY, LocalTime.of(9, 0));

		String otherEmail = uniqueEmail();
		SignUpRequest otherSignUpRequest = new SignUpRequest(
			"김철수",
			"Kim Cheolsu",
			otherEmail,
			VALID_PASSWORD,
			Level.INTERMEDIATE,
			Set.of(Category.BUSINESS)
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

		// when
		ResultActions result = mockMvc.perform(get(NOTIFICATION_SCHEDULE_URL)
			.header("Authorization", "Bearer " + otherAccessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("SUCCESS"))
			.andExpect(jsonPath("$.item").isArray())
			.andExpect(jsonPath("$.item.length()").value(0));
	}

	@Test
	@DisplayName("토큰이 없으면 조회에 실패한다")
	void getNotificationSchedules_NoToken_Fail() throws Exception {
		// when
		ResultActions result = mockMvc.perform(get(NOTIFICATION_SCHEDULE_URL));

		// then
		result
			.andDo(print())
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.type").value("FAIL"))
			.andExpect(jsonPath("$.exception.errorNo").value("AUTH-1"));
	}

	@Test
	@DisplayName("삭제된 스케줄은 조회되지 않는다")
	void getNotificationSchedules_DeletedSchedule_NotIncluded() throws Exception {
		// given
		createSchedule(DayOfWeek.MONDAY, LocalTime.of(9, 0));
		createSchedule(DayOfWeek.FRIDAY, LocalTime.of(18, 0));

		String getResponse = mockMvc.perform(get(NOTIFICATION_SCHEDULE_URL)
				.header("Authorization", "Bearer " + accessToken))
			.andReturn()
			.getResponse()
			.getContentAsString();

		Integer scheduleId = JsonPath.read(getResponse, "$.item[0].id");

		mockMvc.perform(delete(NOTIFICATION_SCHEDULE_URL + "/" + scheduleId)
			.header("Authorization", "Bearer " + accessToken));

		// when
		ResultActions result = mockMvc.perform(get(NOTIFICATION_SCHEDULE_URL)
			.header("Authorization", "Bearer " + accessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("SUCCESS"))
			.andExpect(jsonPath("$.item").isArray())
			.andExpect(jsonPath("$.item.length()").value(1));
	}
}
