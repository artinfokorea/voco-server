package com.voco.voco.app.call.presentation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
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
import com.voco.voco.app.call.presentation.controller.dto.in.CreateLiveKitTokenRequest;
import com.voco.voco.app.member.domain.model.Level;
import com.voco.voco.app.member.presentation.controller.dto.in.SignUpRequest;
import com.voco.voco.app.scenario.domain.model.BehaviorRulesEntity;
import com.voco.voco.app.scenario.domain.model.ConversationScenarioEntity;
import com.voco.voco.app.scenario.domain.model.LanguageRulesEntity;
import com.voco.voco.app.scenario.domain.model.ScenarioContextEntity;

import jakarta.persistence.EntityManager;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("POST /api/v1/livekit/token")
class CreateLiveKitTokenApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private EntityManager entityManager;

	private static final String LIVEKIT_TOKEN_URL = "/api/v1/livekit/token";
	private static final String SIGN_UP_URL = "/api/v1/members/sign-up";
	private static final String SIGN_IN_URL = "/api/v1/auth/sign-in";
	private static final String VALID_PASSWORD = "Password1!";

	private String accessToken;
	private Long scenarioId;

	private String uniqueEmail() {
		return "test-" + UUID.randomUUID() + "@example.com";
	}

	@BeforeEach
	void setUp() throws Exception {
		String testEmail = uniqueEmail();

		SignUpRequest signUpRequest = new SignUpRequest(
			"홍길동",
			"Hong Gildong",
			testEmail,
			VALID_PASSWORD,
			Level.BEGINNER
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

		// 시나리오 생성
		scenarioId = createScenario();
	}

	private Long createScenario() {
		ConversationScenarioEntity scenario = ConversationScenarioEntity.create(
			"Cafe Order",
			com.voco.voco.app.scenario.domain.model.Level.BEGINNER,
			"a cafe staff member",
			"a customer",
			"When all required information is collected",
			List.of("Confirm the order", "Politely end the conversation")
		);

		ScenarioContextEntity context = ScenarioContextEntity.create(
			"The user is visiting a cafe for the first time.",
			List.of("Friendly and polite", "Calm and patient")
		);
		scenario.addScenarioContext(context);

		LanguageRulesEntity languageRules = LanguageRulesEntity.create(
			List.of("Use simple vocabulary only"),
			List.of("Use short sentences"),
			List.of("Use simple English only")
		);
		scenario.addLanguageRules(languageRules);

		BehaviorRulesEntity behaviorRules = BehaviorRulesEntity.create(
			List.of("NEVER break character.")
		);
		scenario.addBehaviorRules(behaviorRules);

		entityManager.persist(scenario);
		entityManager.flush();

		return scenario.getId();
	}

	@Test
	@DisplayName("토큰 발급에 성공한다")
	void createToken_Success() throws Exception {
		// given
		CreateLiveKitTokenRequest request = new CreateLiveKitTokenRequest(scenarioId);

		// when
		ResultActions result = mockMvc.perform(post(LIVEKIT_TOKEN_URL)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

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
	@DisplayName("룸 이름에 시나리오 레벨이 포함된다")
	void createToken_RoomNameContainsLevel() throws Exception {
		// given
		CreateLiveKitTokenRequest request = new CreateLiveKitTokenRequest(scenarioId);

		// when
		ResultActions result = mockMvc.perform(post(LIVEKIT_TOKEN_URL)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.item.roomName").value(org.hamcrest.Matchers.containsString("beginner")));
	}

	@Test
	@DisplayName("토큰이 없으면 발급에 실패한다")
	void createToken_NoToken_Fail() throws Exception {
		// given
		CreateLiveKitTokenRequest request = new CreateLiveKitTokenRequest(scenarioId);

		// when
		ResultActions result = mockMvc.perform(post(LIVEKIT_TOKEN_URL)
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
	@DisplayName("존재하지 않는 시나리오 ID로 요청하면 실패한다")
	void createToken_ScenarioNotFound_Fail() throws Exception {
		// given
		CreateLiveKitTokenRequest request = new CreateLiveKitTokenRequest(999999L);

		// when
		ResultActions result = mockMvc.perform(post(LIVEKIT_TOKEN_URL)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		result
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.type").value("FAIL"))
			.andExpect(jsonPath("$.exception.errorNo").value("SCENARIO-1"));
	}

	@Test
	@DisplayName("시나리오 ID가 없으면 발급에 실패한다")
	void createToken_NoScenarioId_Fail() throws Exception {
		// given
		String requestJson = "{}";

		// when
		ResultActions result = mockMvc.perform(post(LIVEKIT_TOKEN_URL)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestJson));

		// then
		result
			.andDo(print())
			.andExpect(status().isBadRequest());
	}
}
