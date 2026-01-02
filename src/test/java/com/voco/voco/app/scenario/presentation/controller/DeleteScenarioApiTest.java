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
import com.voco.voco.app.scenario.domain.model.ConversationScenarioEntity;
import com.voco.voco.app.scenario.domain.model.Level;
import com.voco.voco.app.scenario.domain.model.ScenarioContextEntity;
import com.voco.voco.app.scenario.domain.model.LanguageRulesEntity;
import com.voco.voco.app.scenario.domain.model.BehaviorRulesEntity;

import jakarta.persistence.EntityManager;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("DELETE /api/v1/scenarios/{scenarioId}")
class DeleteScenarioApiTest {

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
			"카페 직원",
			"a customer",
			"손님",
			"When all required information is collected",
			List.of("Confirm the order")
		);

		ScenarioContextEntity context = ScenarioContextEntity.create(
			"The user is visiting a cafe.",
			List.of("Friendly")
		);
		scenario.addScenarioContext(context);

		LanguageRulesEntity languageRules = LanguageRulesEntity.create(
			List.of("Use simple vocabulary"),
			List.of("Use short sentences"),
			List.of("Use simple English")
		);
		scenario.addLanguageRules(languageRules);

		BehaviorRulesEntity behaviorRules = BehaviorRulesEntity.create(
			List.of("NEVER break character")
		);
		scenario.addBehaviorRules(behaviorRules);

		entityManager.persist(scenario);
		entityManager.flush();

		return scenario.getId();
	}

	@Test
	@DisplayName("시나리오 삭제에 성공한다")
	void deleteScenario_Success() throws Exception {
		// when
		ResultActions result = mockMvc.perform(delete(SCENARIO_URL + "/" + scenarioId)
			.header("Authorization", "Bearer " + accessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("SUCCESS"));
	}

	@Test
	@DisplayName("삭제 후 상세 조회 시 실패한다 (소프트 삭제)")
	void deleteScenario_ThenGetDetail_Fail() throws Exception {
		// given - delete
		mockMvc.perform(delete(SCENARIO_URL + "/" + scenarioId)
			.header("Authorization", "Bearer " + accessToken));

		// when - get detail
		ResultActions getResult = mockMvc.perform(get(SCENARIO_URL + "/" + scenarioId)
			.header("Authorization", "Bearer " + accessToken));

		// then
		getResult
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.type").value("FAIL"))
			.andExpect(jsonPath("$.exception.errorNo").value("SCENARIO-1"));
	}

	@Test
	@DisplayName("삭제 후 목록 조회에서 제외된다")
	void deleteScenario_ThenNotInList() throws Exception {
		// given - delete
		mockMvc.perform(delete(SCENARIO_URL + "/" + scenarioId)
			.header("Authorization", "Bearer " + accessToken));

		// when - get list
		ResultActions listResult = mockMvc.perform(get(SCENARIO_URL)
			.header("Authorization", "Bearer " + accessToken));

		// then
		listResult
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.item.content").isEmpty());
	}

	@Test
	@DisplayName("존재하지 않는 시나리오 삭제 시 실패한다")
	void deleteScenario_NotFound_Fail() throws Exception {
		// when
		ResultActions result = mockMvc.perform(delete(SCENARIO_URL + "/999999")
			.header("Authorization", "Bearer " + accessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.type").value("FAIL"))
			.andExpect(jsonPath("$.exception.errorNo").value("SCENARIO-1"));
	}

	@Test
	@DisplayName("토큰이 없으면 삭제에 실패한다")
	void deleteScenario_NoToken_Fail() throws Exception {
		// when
		ResultActions result = mockMvc.perform(delete(SCENARIO_URL + "/" + scenarioId));

		// then
		result
			.andDo(print())
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.type").value("FAIL"))
			.andExpect(jsonPath("$.exception.errorNo").value("AUTH-1"));
	}

	@Test
	@DisplayName("이미 삭제된 시나리오 재삭제 시 실패한다")
	void deleteScenario_AlreadyDeleted_Fail() throws Exception {
		// given - first delete
		mockMvc.perform(delete(SCENARIO_URL + "/" + scenarioId)
			.header("Authorization", "Bearer " + accessToken));

		// when - second delete
		ResultActions result = mockMvc.perform(delete(SCENARIO_URL + "/" + scenarioId)
			.header("Authorization", "Bearer " + accessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.type").value("FAIL"))
			.andExpect(jsonPath("$.exception.errorNo").value("SCENARIO-1"));
	}
}
