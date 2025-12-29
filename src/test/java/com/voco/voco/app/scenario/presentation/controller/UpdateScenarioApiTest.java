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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.voco.voco.app.auth.presentation.controller.dto.in.SignInRequest;
import com.voco.voco.app.member.domain.model.MemberEntity;
import com.voco.voco.app.scenario.domain.model.BehaviorRulesEntity;
import com.voco.voco.app.scenario.domain.model.ConversationScenarioEntity;
import com.voco.voco.app.scenario.domain.model.ConversationSlotEntity;
import com.voco.voco.app.scenario.domain.model.ConversationStateEntity;
import com.voco.voco.app.scenario.domain.model.LanguageRulesEntity;
import com.voco.voco.app.scenario.domain.model.Level;
import com.voco.voco.app.scenario.domain.model.ScenarioContextEntity;
import com.voco.voco.app.scenario.presentation.controller.dto.in.UpdateScenarioRequest;

import jakarta.persistence.EntityManager;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("PUT /api/v1/scenarios/{scenarioId}")
class UpdateScenarioApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private static final String SCENARIO_URL = "/api/v1/scenarios";
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

		MemberEntity adminMember = MemberEntity.createAdmin(
			"관리자",
			"Admin",
			testEmail,
			passwordEncoder.encode(VALID_PASSWORD),
			com.voco.voco.app.member.domain.model.Level.BEGINNER
		);
		entityManager.persist(adminMember);
		entityManager.flush();

		SignInRequest signInRequest = new SignInRequest(testEmail, VALID_PASSWORD);
		String signInResponse = mockMvc.perform(post(SIGN_IN_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signInRequest)))
			.andReturn()
			.getResponse()
			.getContentAsString();

		accessToken = JsonPath.read(signInResponse, "$.item.accessToken");

		scenarioId = createScenario();
	}

	private Long createScenario() {
		ConversationScenarioEntity scenario = ConversationScenarioEntity.create(
			"Cafe Order",
			Level.BEGINNER,
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
			List.of("Use simple vocabulary only", "Avoid slang"),
			List.of("Use short sentences"),
			List.of("Use simple English only")
		);
		scenario.addLanguageRules(languageRules);

		BehaviorRulesEntity behaviorRules = BehaviorRulesEntity.create(
			List.of("NEVER break character.", "NEVER explain grammar")
		);
		scenario.addBehaviorRules(behaviorRules);

		ConversationStateEntity state1 = ConversationStateEntity.create(1, "Greeting");
		ConversationStateEntity state2 = ConversationStateEntity.create(2, "Ask for drink");
		scenario.addConversationState(state1);
		scenario.addConversationState(state2);

		ConversationSlotEntity slot = ConversationSlotEntity.create("drink_type", List.of("coffee", "latte"));
		scenario.addConversationSlot(slot);

		entityManager.persist(scenario);
		entityManager.flush();

		return scenario.getId();
	}

	private UpdateScenarioRequest createValidUpdateRequest() {
		return new UpdateScenarioRequest(
			"Updated Cafe Order",
			Level.INTERMEDIATE,
			"an updated cafe staff member",
			"an updated customer",
			"Updated completion rule",
			List.of("Updated completion detail 1", "Updated completion detail 2"),
			new UpdateScenarioRequest.ScenarioContextRequest(
				"The user is visiting an updated cafe.",
				List.of("Updated personality 1", "Updated personality 2")
			),
			new UpdateScenarioRequest.LanguageRulesRequest(
				List.of("Updated vocabulary rule"),
				List.of("Updated sentence rule"),
				List.of("Updated output constraint")
			),
			new UpdateScenarioRequest.BehaviorRulesRequest(
				List.of("Updated behavior rule")
			),
			List.of(
				new UpdateScenarioRequest.ConversationStateRequest(1, "Updated Greeting"),
				new UpdateScenarioRequest.ConversationStateRequest(2, "Updated Ask for drink"),
				new UpdateScenarioRequest.ConversationStateRequest(3, "New Confirm order")
			),
			List.of(
				new UpdateScenarioRequest.ConversationSlotRequest("drink_type", List.of("espresso", "cappuccino")),
				new UpdateScenarioRequest.ConversationSlotRequest("size", List.of("small", "medium", "large"))
			)
		);
	}

	@Test
	@DisplayName("시나리오 수정에 성공한다")
	void updateScenario_Success() throws Exception {
		// given
		UpdateScenarioRequest request = createValidUpdateRequest();

		// when
		ResultActions result = mockMvc.perform(put(SCENARIO_URL + "/" + scenarioId)
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
	@DisplayName("수정 후 상세 조회 시 변경된 값이 반환된다")
	void updateScenario_ThenGetDetail_ReturnsUpdatedValues() throws Exception {
		// given
		UpdateScenarioRequest request = createValidUpdateRequest();

		// when - update
		mockMvc.perform(put(SCENARIO_URL + "/" + scenarioId)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then - get detail
		ResultActions getResult = mockMvc.perform(get(SCENARIO_URL + "/" + scenarioId)
			.header("Authorization", "Bearer " + accessToken));

		getResult
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.item.name").value("Updated Cafe Order"))
			.andExpect(jsonPath("$.item.level").value("INTERMEDIATE"))
			.andExpect(jsonPath("$.item.aiRole").value("an updated cafe staff member"))
			.andExpect(jsonPath("$.item.userRole").value("an updated customer"))
			.andExpect(jsonPath("$.item.completionRule").value("Updated completion rule"))
			.andExpect(jsonPath("$.item.conversationStates.length()").value(3))
			.andExpect(jsonPath("$.item.conversationSlots.length()").value(2));
	}

	@Test
	@DisplayName("존재하지 않는 시나리오 수정 시 실패한다")
	void updateScenario_NotFound_Fail() throws Exception {
		// given
		UpdateScenarioRequest request = createValidUpdateRequest();

		// when
		ResultActions result = mockMvc.perform(put(SCENARIO_URL + "/999999")
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
	@DisplayName("토큰이 없으면 수정에 실패한다")
	void updateScenario_NoToken_Fail() throws Exception {
		// given
		UpdateScenarioRequest request = createValidUpdateRequest();

		// when
		ResultActions result = mockMvc.perform(put(SCENARIO_URL + "/" + scenarioId)
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
	@DisplayName("name이 없으면 수정에 실패한다")
	void updateScenario_NoName_Fail() throws Exception {
		// given
		UpdateScenarioRequest request = new UpdateScenarioRequest(
			null,
			Level.BEGINNER,
			"a cafe staff member",
			"a customer",
			"completion rule",
			null,
			new UpdateScenarioRequest.ScenarioContextRequest("context", List.of("personality")),
			new UpdateScenarioRequest.LanguageRulesRequest(List.of(), List.of(), List.of()),
			new UpdateScenarioRequest.BehaviorRulesRequest(List.of()),
			null,
			null
		);

		// when
		ResultActions result = mockMvc.perform(put(SCENARIO_URL + "/" + scenarioId)
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
	@DisplayName("level이 없으면 수정에 실패한다")
	void updateScenario_NoLevel_Fail() throws Exception {
		// given
		UpdateScenarioRequest request = new UpdateScenarioRequest(
			"Cafe Order",
			null,
			"a cafe staff member",
			"a customer",
			"completion rule",
			null,
			new UpdateScenarioRequest.ScenarioContextRequest("context", List.of("personality")),
			new UpdateScenarioRequest.LanguageRulesRequest(List.of(), List.of(), List.of()),
			new UpdateScenarioRequest.BehaviorRulesRequest(List.of()),
			null,
			null
		);

		// when
		ResultActions result = mockMvc.perform(put(SCENARIO_URL + "/" + scenarioId)
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
