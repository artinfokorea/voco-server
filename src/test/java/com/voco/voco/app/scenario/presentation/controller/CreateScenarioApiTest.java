package com.voco.voco.app.scenario.presentation.controller;

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
import com.voco.voco.app.member.presentation.controller.dto.in.SignUpRequest;
import com.voco.voco.app.scenario.domain.model.Level;
import com.voco.voco.app.scenario.presentation.controller.dto.in.CreateScenarioRequest;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("POST /api/v1/scenarios")
class CreateScenarioApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private static final String SCENARIO_URL = "/api/v1/scenarios";
	private static final String SIGN_UP_URL = "/api/v1/members/sign-up";
	private static final String SIGN_IN_URL = "/api/v1/auth/sign-in";
	private static final String VALID_PASSWORD = "Password1!";

	private String accessToken;

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
			com.voco.voco.app.member.domain.model.Level.BEGINNER
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

	private CreateScenarioRequest createValidRequest() {
		return new CreateScenarioRequest(
			"Cafe Order",
			Level.BEGINNER,
			"a cafe staff member",
			"a customer",
			"When all required information is collected",
			List.of("Confirm the order", "Politely end the conversation"),
			new CreateScenarioRequest.ScenarioContextRequest(
				"The user is visiting a cafe for the first time and wants to order a drink.",
				List.of("Friendly and polite", "Calm and patient", "Not too talkative")
			),
			new CreateScenarioRequest.LanguageRulesRequest(
				List.of("Use simple vocabulary only", "Avoid slang"),
				List.of("Use short sentences", "Ask ONLY one question at a time"),
				List.of("Use simple English only", "Avoid long sentences")
			),
			new CreateScenarioRequest.BehaviorRulesRequest(
				List.of("NEVER explain grammar or vocabulary.", "NEVER break character.")
			),
			List.of(
				new CreateScenarioRequest.ConversationStateRequest(1, "Greeting"),
				new CreateScenarioRequest.ConversationStateRequest(2, "Ask for drink"),
				new CreateScenarioRequest.ConversationStateRequest(3, "Confirm order")
			),
			List.of(
				new CreateScenarioRequest.ConversationSlotRequest("drink_type", List.of("coffee", "latte", "americano")),
				new CreateScenarioRequest.ConversationSlotRequest("size", List.of("small", "medium", "large"))
			)
		);
	}

	@Test
	@DisplayName("시나리오 생성에 성공한다")
	void createScenario_Success() throws Exception {
		// given
		CreateScenarioRequest request = createValidRequest();

		// when
		ResultActions result = mockMvc.perform(post(SCENARIO_URL)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("SUCCESS"))
			.andExpect(jsonPath("$.item").isNumber());
	}

	@Test
	@DisplayName("conversationStates와 conversationSlots 없이도 생성에 성공한다")
	void createScenario_WithoutOptionalFields_Success() throws Exception {
		// given
		CreateScenarioRequest request = new CreateScenarioRequest(
			"Cafe Order",
			Level.BEGINNER,
			"a cafe staff member",
			"a customer",
			"When all required information is collected",
			null,
			new CreateScenarioRequest.ScenarioContextRequest(
				"The user is visiting a cafe.",
				List.of("Friendly")
			),
			new CreateScenarioRequest.LanguageRulesRequest(
				List.of("Use simple vocabulary"),
				List.of("Use short sentences"),
				List.of("Use simple English")
			),
			new CreateScenarioRequest.BehaviorRulesRequest(
				List.of("NEVER break character.")
			),
			null,
			null
		);

		// when
		ResultActions result = mockMvc.perform(post(SCENARIO_URL)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("SUCCESS"))
			.andExpect(jsonPath("$.item").isNumber());
	}

	@Test
	@DisplayName("name이 없으면 생성에 실패한다")
	void createScenario_NoName_Fail() throws Exception {
		// given
		CreateScenarioRequest request = new CreateScenarioRequest(
			null,
			Level.BEGINNER,
			"a cafe staff member",
			"a customer",
			"When all required information is collected",
			null,
			new CreateScenarioRequest.ScenarioContextRequest(
				"The user is visiting a cafe.",
				List.of("Friendly")
			),
			new CreateScenarioRequest.LanguageRulesRequest(
				List.of("Use simple vocabulary"),
				List.of("Use short sentences"),
				List.of("Use simple English")
			),
			new CreateScenarioRequest.BehaviorRulesRequest(
				List.of("NEVER break character.")
			),
			null,
			null
		);

		// when
		ResultActions result = mockMvc.perform(post(SCENARIO_URL)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		result
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.type").value("FAIL"));
	}

	@Test
	@DisplayName("level이 없으면 생성에 실패한다")
	void createScenario_NoLevel_Fail() throws Exception {
		// given
		CreateScenarioRequest request = new CreateScenarioRequest(
			"Cafe Order",
			null,
			"a cafe staff member",
			"a customer",
			"When all required information is collected",
			null,
			new CreateScenarioRequest.ScenarioContextRequest(
				"The user is visiting a cafe.",
				List.of("Friendly")
			),
			new CreateScenarioRequest.LanguageRulesRequest(
				List.of("Use simple vocabulary"),
				List.of("Use short sentences"),
				List.of("Use simple English")
			),
			new CreateScenarioRequest.BehaviorRulesRequest(
				List.of("NEVER break character.")
			),
			null,
			null
		);

		// when
		ResultActions result = mockMvc.perform(post(SCENARIO_URL)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		result
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.type").value("FAIL"));
	}

	@Test
	@DisplayName("scenarioContext가 없으면 생성에 실패한다")
	void createScenario_NoScenarioContext_Fail() throws Exception {
		// given
		CreateScenarioRequest request = new CreateScenarioRequest(
			"Cafe Order",
			Level.BEGINNER,
			"a cafe staff member",
			"a customer",
			"When all required information is collected",
			null,
			null,
			new CreateScenarioRequest.LanguageRulesRequest(
				List.of("Use simple vocabulary"),
				List.of("Use short sentences"),
				List.of("Use simple English")
			),
			new CreateScenarioRequest.BehaviorRulesRequest(
				List.of("NEVER break character.")
			),
			null,
			null
		);

		// when
		ResultActions result = mockMvc.perform(post(SCENARIO_URL)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		result
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.type").value("FAIL"));
	}

}
