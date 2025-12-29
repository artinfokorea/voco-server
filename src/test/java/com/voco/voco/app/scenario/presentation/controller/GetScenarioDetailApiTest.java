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
import com.voco.voco.app.member.domain.model.Level;
import com.voco.voco.app.member.presentation.controller.dto.in.SignUpRequest;
import com.voco.voco.app.scenario.domain.model.BehaviorRulesEntity;
import com.voco.voco.app.scenario.domain.model.ConversationScenarioEntity;
import com.voco.voco.app.scenario.domain.model.ConversationSlotEntity;
import com.voco.voco.app.scenario.domain.model.ConversationStateEntity;
import com.voco.voco.app.scenario.domain.model.LanguageRulesEntity;
import com.voco.voco.app.scenario.domain.model.ScenarioContextEntity;

import jakarta.persistence.EntityManager;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("GET /api/v1/scenarios/{scenarioId}")
class GetScenarioDetailApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private EntityManager entityManager;

	private static final String SCENARIO_URL = "/api/v1/scenarios";
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

	@Test
	@DisplayName("시나리오 상세 조회에 성공한다")
	void getScenarioDetail_Success() throws Exception {
		// when
		ResultActions result = mockMvc.perform(get(SCENARIO_URL + "/" + scenarioId)
			.header("Authorization", "Bearer " + accessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("SUCCESS"))
			.andExpect(jsonPath("$.item.scenarioId").value(scenarioId))
			.andExpect(jsonPath("$.item.name").value("Cafe Order"))
			.andExpect(jsonPath("$.item.level").value("BEGINNER"))
			.andExpect(jsonPath("$.item.aiRole").value("a cafe staff member"))
			.andExpect(jsonPath("$.item.userRole").value("a customer"));
	}

	@Test
	@DisplayName("시나리오 컨텍스트 정보가 포함된다")
	void getScenarioDetail_ContainsContext() throws Exception {
		// when
		ResultActions result = mockMvc.perform(get(SCENARIO_URL + "/" + scenarioId)
			.header("Authorization", "Bearer " + accessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.item.scenarioContext.context").value("The user is visiting a cafe for the first time."))
			.andExpect(jsonPath("$.item.scenarioContext.personality").isArray())
			.andExpect(jsonPath("$.item.scenarioContext.personality.length()").value(2));
	}

	@Test
	@DisplayName("언어 규칙 정보가 포함된다")
	void getScenarioDetail_ContainsLanguageRules() throws Exception {
		// when
		ResultActions result = mockMvc.perform(get(SCENARIO_URL + "/" + scenarioId)
			.header("Authorization", "Bearer " + accessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.item.languageRules.vocabularyRules").isArray())
			.andExpect(jsonPath("$.item.languageRules.vocabularyRules.length()").value(2))
			.andExpect(jsonPath("$.item.languageRules.sentenceRules").isArray())
			.andExpect(jsonPath("$.item.languageRules.outputConstraints").isArray());
	}

	@Test
	@DisplayName("행동 규칙 정보가 포함된다")
	void getScenarioDetail_ContainsBehaviorRules() throws Exception {
		// when
		ResultActions result = mockMvc.perform(get(SCENARIO_URL + "/" + scenarioId)
			.header("Authorization", "Bearer " + accessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.item.behaviorRules.rules").isArray())
			.andExpect(jsonPath("$.item.behaviorRules.rules.length()").value(2));
	}

	@Test
	@DisplayName("대화 상태 목록이 포함된다")
	void getScenarioDetail_ContainsConversationStates() throws Exception {
		// when
		ResultActions result = mockMvc.perform(get(SCENARIO_URL + "/" + scenarioId)
			.header("Authorization", "Bearer " + accessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.item.conversationStates").isArray())
			.andExpect(jsonPath("$.item.conversationStates.length()").value(2))
			.andExpect(jsonPath("$.item.conversationStates[0].stateOrder").value(1))
			.andExpect(jsonPath("$.item.conversationStates[0].stateName").value("Greeting"));
	}

	@Test
	@DisplayName("대화 슬롯 목록이 포함된다")
	void getScenarioDetail_ContainsConversationSlots() throws Exception {
		// when
		ResultActions result = mockMvc.perform(get(SCENARIO_URL + "/" + scenarioId)
			.header("Authorization", "Bearer " + accessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.item.conversationSlots").isArray())
			.andExpect(jsonPath("$.item.conversationSlots.length()").value(1))
			.andExpect(jsonPath("$.item.conversationSlots[0].slotKey").value("drink_type"))
			.andExpect(jsonPath("$.item.conversationSlots[0].allowedValues").isArray());
	}

	@Test
	@DisplayName("완료 규칙 정보가 포함된다")
	void getScenarioDetail_ContainsCompletionRule() throws Exception {
		// when
		ResultActions result = mockMvc.perform(get(SCENARIO_URL + "/" + scenarioId)
			.header("Authorization", "Bearer " + accessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.item.completionRule").value("When all required information is collected"))
			.andExpect(jsonPath("$.item.completionRuleDetail").isArray())
			.andExpect(jsonPath("$.item.completionRuleDetail.length()").value(2));
	}

	@Test
	@DisplayName("존재하지 않는 시나리오 조회 시 실패한다")
	void getScenarioDetail_NotFound_Fail() throws Exception {
		// when
		ResultActions result = mockMvc.perform(get(SCENARIO_URL + "/999999")
			.header("Authorization", "Bearer " + accessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.type").value("FAIL"))
			.andExpect(jsonPath("$.exception.errorNo").value("SCENARIO-1"));
	}

	@Test
	@DisplayName("토큰이 없으면 조회에 실패한다")
	void getScenarioDetail_NoToken_Fail() throws Exception {
		// when
		ResultActions result = mockMvc.perform(get(SCENARIO_URL + "/" + scenarioId));

		// then
		result
			.andDo(print())
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.type").value("FAIL"))
			.andExpect(jsonPath("$.exception.errorNo").value("AUTH-1"));
	}
}
