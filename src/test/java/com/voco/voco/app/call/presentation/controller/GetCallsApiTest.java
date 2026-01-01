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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.voco.voco.app.auth.presentation.controller.dto.in.SignInRequest;
import com.voco.voco.app.call.domain.model.CallAnalysisEntity;
import com.voco.voco.app.call.domain.model.CallEntity;
import com.voco.voco.app.call.domain.model.FeedbackEmbeddable;
import com.voco.voco.app.member.domain.model.Level;
import com.voco.voco.app.member.domain.model.MemberEntity;
import com.voco.voco.app.scenario.domain.model.BehaviorRulesEntity;
import com.voco.voco.app.scenario.domain.model.ConversationScenarioEntity;
import com.voco.voco.app.scenario.domain.model.LanguageRulesEntity;
import com.voco.voco.app.scenario.domain.model.ScenarioContextEntity;

import jakarta.persistence.EntityManager;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("GET /api/v1/calls")
class GetCallsApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private static final String CALLS_URL = "/api/v1/calls";
	private static final String SIGN_IN_URL = "/api/v1/auth/sign-in";
	private static final String VALID_PASSWORD = "Password1!";

	private String accessToken;
	private Long memberId;
	private Long scenarioId;

	private String uniqueEmail() {
		return "test-" + UUID.randomUUID() + "@example.com";
	}

	@BeforeEach
	void setUp() throws Exception {
		String testEmail = uniqueEmail();

		MemberEntity member = MemberEntity.create(
			"테스터",
			"Tester",
			testEmail,
			passwordEncoder.encode(VALID_PASSWORD),
			Level.BEGINNER
		);
		entityManager.persist(member);
		entityManager.flush();
		memberId = member.getId();

		ConversationScenarioEntity scenario = createScenario("Cafe Order");
		entityManager.persist(scenario);
		entityManager.flush();
		scenarioId = scenario.getId();

		SignInRequest signInRequest = new SignInRequest(testEmail, VALID_PASSWORD);
		String signInResponse = mockMvc.perform(post(SIGN_IN_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signInRequest)))
			.andReturn()
			.getResponse()
			.getContentAsString();

		accessToken = JsonPath.read(signInResponse, "$.item.accessToken");
	}

	private ConversationScenarioEntity createScenario(String name) {
		ConversationScenarioEntity scenario = ConversationScenarioEntity.create(
			name,
			com.voco.voco.app.scenario.domain.model.Level.BEGINNER,
			"ai role",
			"user role",
			"completion rule",
			List.of("detail")
		);

		ScenarioContextEntity context = ScenarioContextEntity.create("context", List.of("personality"));
		scenario.addScenarioContext(context);

		LanguageRulesEntity languageRules = LanguageRulesEntity.create(
			List.of("vocab"), List.of("sentence"), List.of("output")
		);
		scenario.addLanguageRules(languageRules);

		BehaviorRulesEntity behaviorRules = BehaviorRulesEntity.create(List.of("behavior"));
		scenario.addBehaviorRules(behaviorRules);

		return scenario;
	}

	private CallEntity createCall(Long memberId, Long scenarioId) {
		CallEntity call = CallEntity.create(
			memberId,
			scenarioId,
			"Cafe Order",
			com.voco.voco.app.scenario.domain.model.Level.BEGINNER,
			"room-test-" + UUID.randomUUID()
		);
		entityManager.persist(call);
		return call;
	}

	private void createCallWithAnalysis(Long memberId, Long scenarioId) {
		CallEntity call = createCall(memberId, scenarioId);

		CallAnalysisEntity analysis = CallAnalysisEntity.create(
			List.of(new CallAnalysisEntity.ConversationEntry("user", "Hello", null)),
			80,
			"Task completed",
			90,
			"Good accuracy",
			FeedbackEmbeddable.create(
				List.of("strength"),
				List.of("improvement"),
				List.of("focus"),
				List.of("tip")
			)
		);
		entityManager.persist(analysis);
		entityManager.flush();

		call.updateAnalysisId(analysis.getId());
		entityManager.flush();
	}

	@Test
	@DisplayName("통화 내역 목록 조회에 성공한다")
	void getCalls_Success() throws Exception {
		// given
		createCallWithAnalysis(memberId, scenarioId);
		createCallWithAnalysis(memberId, scenarioId);

		// when
		ResultActions result = mockMvc.perform(get(CALLS_URL)
			.header("Authorization", "Bearer " + accessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("SUCCESS"))
			.andExpect(jsonPath("$.item.content").isArray())
			.andExpect(jsonPath("$.item.content.length()").value(2));
	}

	@Test
	@DisplayName("통화 내역이 없으면 빈 목록을 반환한다")
	void getCalls_Empty() throws Exception {
		// when
		ResultActions result = mockMvc.perform(get(CALLS_URL)
			.header("Authorization", "Bearer " + accessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("SUCCESS"))
			.andExpect(jsonPath("$.item.content").isArray())
			.andExpect(jsonPath("$.item.content.length()").value(0));
	}

	@Test
	@DisplayName("페이징이 적용된다")
	void getCalls_Pagination() throws Exception {
		// given
		for (int i = 0; i < 15; i++) {
			createCallWithAnalysis(memberId, scenarioId);
		}

		// when
		ResultActions result = mockMvc.perform(get(CALLS_URL)
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
	@DisplayName("토큰이 없으면 조회에 실패한다")
	void getCalls_NoToken_Fail() throws Exception {
		// when
		ResultActions result = mockMvc.perform(get(CALLS_URL));

		// then
		result
			.andDo(print())
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.type").value("FAIL"));
	}
}
