package com.voco.voco.app.call.presentation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("POST /api/v1/livekit/token")
class CreateLiveKitTokenApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private static final String LIVEKIT_TOKEN_URL = "/api/v1/livekit/token";
	private static final String SIGN_UP_URL = "/api/v1/members/sign-up";
	private static final String SIGN_IN_URL = "/api/v1/auth/sign-in";
	private static final String VALID_PASSWORD = "Password1!";

	private String accessToken;
	private String testEmail;

	private String uniqueEmail() {
		return "test-" + UUID.randomUUID() + "@example.com";
	}

	@BeforeEach
	void setUp() throws Exception {
		testEmail = uniqueEmail();

		SignUpRequest signUpRequest = new SignUpRequest(
			"홍길동",
			"Hong Gildong",
			testEmail,
			VALID_PASSWORD,
			Level.BEGINNER,
			Set.of(Category.DAILY)
		);
		mockMvc.perform(post(SIGN_UP_URL)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(signUpRequest)));

		SignInRequest signInRequest = new SignInRequest(testEmail, VALID_PASSWORD);
		String signInResponse = mockMvc.perform(post(SIGN_IN_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signInRequest)))
			.andReturn()
			.getResponse()
			.getContentAsString();

		accessToken = JsonPath.read(signInResponse, "$.item.accessToken");
	}

	@Test
	@DisplayName("토큰 발급에 성공한다")
	void createToken_Success() throws Exception {
		// when
		ResultActions result = mockMvc.perform(post(LIVEKIT_TOKEN_URL)
			.header("Authorization", "Bearer " + accessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("SUCCESS"))
			.andExpect(jsonPath("$.item.token").exists())
			.andExpect(jsonPath("$.item.token").isString())
			.andExpect(jsonPath("$.item.roomName").exists())
			.andExpect(jsonPath("$.item.roomName").isString());
	}

	@Test
	@DisplayName("룸 이름에 멤버 레벨이 포함된다")
	void createToken_RoomNameContainsLevel() throws Exception {
		// when
		ResultActions result = mockMvc.perform(post(LIVEKIT_TOKEN_URL)
			.header("Authorization", "Bearer " + accessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.item.roomName").value(org.hamcrest.Matchers.containsString("beginner")));
	}

	@Test
	@DisplayName("INTERMEDIATE 레벨 회원의 룸 이름에 intermediate가 포함된다")
	void createToken_IntermediateLevel_RoomNameCorrect() throws Exception {
		// given
		String intermediateEmail = uniqueEmail();
		SignUpRequest signUpRequest = new SignUpRequest(
			"김중급",
			"Kim Intermediate",
			intermediateEmail,
			VALID_PASSWORD,
			Level.INTERMEDIATE,
			Set.of(Category.BUSINESS)
		);
		mockMvc.perform(post(SIGN_UP_URL)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(signUpRequest)));

		SignInRequest signInRequest = new SignInRequest(intermediateEmail, VALID_PASSWORD);
		String signInResponse = mockMvc.perform(post(SIGN_IN_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signInRequest)))
			.andReturn()
			.getResponse()
			.getContentAsString();

		String intermediateAccessToken = JsonPath.read(signInResponse, "$.item.accessToken");

		// when
		ResultActions result = mockMvc.perform(post(LIVEKIT_TOKEN_URL)
			.header("Authorization", "Bearer " + intermediateAccessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.item.roomName").value(org.hamcrest.Matchers.containsString("intermediate")));
	}

	@Test
	@DisplayName("토큰이 없으면 발급에 실패한다")
	void createToken_NoToken_Fail() throws Exception {
		// when
		ResultActions result = mockMvc.perform(post(LIVEKIT_TOKEN_URL));

		// then
		result
			.andDo(print())
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.type").value("FAIL"))
			.andExpect(jsonPath("$.exception.errorNo").value("AUTH-1"));
	}

}
