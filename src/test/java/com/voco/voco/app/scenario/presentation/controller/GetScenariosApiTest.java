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
import com.voco.voco.app.scenario.domain.model.LanguageRulesEntity;
import com.voco.voco.app.scenario.domain.model.ScenarioContextEntity;

import jakarta.persistence.EntityManager;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("GET /api/v1/scenarios")
class GetScenariosApiTest {

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
	}

	private void createScenario(String name, com.voco.voco.app.scenario.domain.model.Level level) {
		ConversationScenarioEntity scenario = ConversationScenarioEntity.create(
			name,
			level,
			"ai role",
			"AI 역할",
			"user role",
			"사용자 역할",
			"completion rule",
			List.of("detail")
		);

		ScenarioContextEntity context = ScenarioContextEntity.create(
			"context",
			List.of("personality")
		);
		scenario.addScenarioContext(context);

		LanguageRulesEntity languageRules = LanguageRulesEntity.create(
			List.of("vocab"),
			List.of("sentence"),
			List.of("output")
		);
		scenario.addLanguageRules(languageRules);

		BehaviorRulesEntity behaviorRules = BehaviorRulesEntity.create(
			List.of("behavior")
		);
		scenario.addBehaviorRules(behaviorRules);

		entityManager.persist(scenario);
	}

	@Test
	@DisplayName("시나리오 목록 조회에 성공한다")
	void getScenarios_Success() throws Exception {
		// given
		createScenario("Cafe Order", com.voco.voco.app.scenario.domain.model.Level.BEGINNER);
		createScenario("Hotel Reservation", com.voco.voco.app.scenario.domain.model.Level.INTERMEDIATE);
		entityManager.flush();

		// when
		ResultActions result = mockMvc.perform(get(SCENARIO_URL)
			.header("Authorization", "Bearer " + accessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("SUCCESS"))
			.andExpect(jsonPath("$.item.content").isArray())
			.andExpect(jsonPath("$.item.content.length()").value(2))
			.andExpect(jsonPath("$.item.page.totalElements").value(2));
	}

	@Test
	@DisplayName("레벨로 필터링하여 조회한다")
	void getScenarios_FilterByLevel_Success() throws Exception {
		// given
		createScenario("Cafe Order", com.voco.voco.app.scenario.domain.model.Level.BEGINNER);
		createScenario("Hotel Reservation", com.voco.voco.app.scenario.domain.model.Level.INTERMEDIATE);
		createScenario("Business Meeting", com.voco.voco.app.scenario.domain.model.Level.BEGINNER);
		entityManager.flush();

		// when
		ResultActions result = mockMvc.perform(get(SCENARIO_URL)
			.header("Authorization", "Bearer " + accessToken)
			.param("level", "BEGINNER"));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("SUCCESS"))
			.andExpect(jsonPath("$.item.content.length()").value(2))
			.andExpect(jsonPath("$.item.page.totalElements").value(2));
	}

	@Test
	@DisplayName("페이징이 적용된다")
	void getScenarios_Pagination_Success() throws Exception {
		// given
		for (int i = 0; i < 15; i++) {
			createScenario("Scenario " + i, com.voco.voco.app.scenario.domain.model.Level.BEGINNER);
		}
		entityManager.flush();

		// when
		ResultActions result = mockMvc.perform(get(SCENARIO_URL)
			.header("Authorization", "Bearer " + accessToken)
			.param("page", "1")
			.param("size", "10"));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("SUCCESS"))
			.andExpect(jsonPath("$.item.content.length()").value(10))
			.andExpect(jsonPath("$.item.page.totalElements").value(15))
			.andExpect(jsonPath("$.item.page.totalPages").value(2));
	}

	@Test
	@DisplayName("응답에 scenarioId, name, level이 포함된다")
	void getScenarios_ResponseFields() throws Exception {
		// given
		createScenario("Cafe Order", com.voco.voco.app.scenario.domain.model.Level.BEGINNER);
		entityManager.flush();

		// when
		ResultActions result = mockMvc.perform(get(SCENARIO_URL)
			.header("Authorization", "Bearer " + accessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.item.content[0].scenarioId").isNumber())
			.andExpect(jsonPath("$.item.content[0].name").value("Cafe Order"))
			.andExpect(jsonPath("$.item.content[0].level").value("BEGINNER"));
	}

	@Test
	@DisplayName("토큰이 없으면 조회에 실패한다")
	void getScenarios_NoToken_Fail() throws Exception {
		// when
		ResultActions result = mockMvc.perform(get(SCENARIO_URL));

		// then
		result
			.andDo(print())
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.type").value("FAIL"))
			.andExpect(jsonPath("$.exception.errorNo").value("AUTH-1"));
	}

	@Test
	@DisplayName("시나리오가 없으면 빈 목록을 반환한다")
	void getScenarios_Empty_Success() throws Exception {
		// when
		ResultActions result = mockMvc.perform(get(SCENARIO_URL)
			.header("Authorization", "Bearer " + accessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("SUCCESS"))
			.andExpect(jsonPath("$.item.content").isArray())
			.andExpect(jsonPath("$.item.content.length()").value(0))
			.andExpect(jsonPath("$.item.page.totalElements").value(0));
	}
}
