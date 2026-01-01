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
@DisplayName("GET /api/v1/calls/{callId}")
class GetCallDetailApiTest {

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
	private Long callId;

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

		callId = createCallWithAnalysis(memberId, scenarioId);

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

	private Long createCallWithAnalysis(Long memberId, Long scenarioId) {
		CallEntity call = CallEntity.create(memberId, scenarioId, "room-test-" + UUID.randomUUID());
		entityManager.persist(call);

		CallAnalysisEntity analysis = CallAnalysisEntity.create(
			List.of(
				new CallAnalysisEntity.ConversationEntry("assistant", "Welcome!", null),
				new CallAnalysisEntity.ConversationEntry("user", "Hello!", null)
			),
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

		return call.getId();
	}

	@Test
	@DisplayName("통화 상세 조회에 성공한다")
	void getCallDetail_Success() throws Exception {
		// when
		ResultActions result = mockMvc.perform(get(CALLS_URL + "/" + callId)
			.header("Authorization", "Bearer " + accessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("SUCCESS"))
			.andExpect(jsonPath("$.item.scenarioName").value("Cafe Order"))
			.andExpect(jsonPath("$.item.scenarioLevel").value("BEGINNER"))
			.andExpect(jsonPath("$.item.conversation").isArray())
			.andExpect(jsonPath("$.item.conversation.length()").value(2))
			.andExpect(jsonPath("$.item.taskCompletionScore").value(80))
			.andExpect(jsonPath("$.item.languageAccuracyScore").value(90))
			.andExpect(jsonPath("$.item.feedback").exists());
	}

	@Test
	@DisplayName("존재하지 않는 통화 조회 시 실패한다")
	void getCallDetail_NotFound() throws Exception {
		// when
		ResultActions result = mockMvc.perform(get(CALLS_URL + "/999999")
			.header("Authorization", "Bearer " + accessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.type").value("FAIL"))
			.andExpect(jsonPath("$.exception.errorNo").value("CALL-1"));
	}

	@Test
	@DisplayName("토큰이 없으면 조회에 실패한다")
	void getCallDetail_NoToken_Fail() throws Exception {
		// when
		ResultActions result = mockMvc.perform(get(CALLS_URL + "/" + callId));

		// then
		result
			.andDo(print())
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.type").value("FAIL"));
	}

	@Test
	@DisplayName("다른 사용자의 통화는 조회할 수 없다")
	void getCallDetail_OtherUserCall_NotFound() throws Exception {
		// given
		MemberEntity otherMember = MemberEntity.create(
			"다른사용자",
			"Other",
			uniqueEmail(),
			passwordEncoder.encode(VALID_PASSWORD),
			Level.BEGINNER
		);
		entityManager.persist(otherMember);
		entityManager.flush();

		Long otherCallId = createCallWithAnalysis(otherMember.getId(), scenarioId);

		// when
		ResultActions result = mockMvc.perform(get(CALLS_URL + "/" + otherCallId)
			.header("Authorization", "Bearer " + accessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.type").value("FAIL"));
	}
}
