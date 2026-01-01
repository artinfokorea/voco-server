package com.voco.voco.app.call.presentation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;
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
import com.voco.voco.app.call.domain.model.CallAnalysisRawEntity;
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
@DisplayName("GET /api/v1/admin/calls/{callId}/analysis-raw")
class GetAdminCallAnalysisRawApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private static final String ADMIN_CALLS_URL = "/api/v1/admin/calls";
	private static final String SIGN_IN_URL = "/api/v1/auth/sign-in";
	private static final String VALID_PASSWORD = "Password1!";

	private String adminAccessToken;
	private String userAccessToken;
	private Long callIdWithRaw;
	private Long callIdWithoutRaw;

	private String uniqueEmail() {
		return "test-" + UUID.randomUUID() + "@example.com";
	}

	@BeforeEach
	void setUp() throws Exception {
		// Admin 유저 생성
		String adminEmail = uniqueEmail();
		MemberEntity adminMember = MemberEntity.createAdmin(
			"관리자",
			"Admin",
			adminEmail,
			passwordEncoder.encode(VALID_PASSWORD),
			Level.BEGINNER
		);
		entityManager.persist(adminMember);

		// 일반 유저 생성
		String userEmail = uniqueEmail();
		MemberEntity userMember = MemberEntity.create(
			"일반사용자",
			"User",
			userEmail,
			passwordEncoder.encode(VALID_PASSWORD),
			Level.BEGINNER
		);
		entityManager.persist(userMember);
		entityManager.flush();

		// 시나리오 생성
		ConversationScenarioEntity scenario = createScenario("Cafe Order");
		entityManager.persist(scenario);
		entityManager.flush();

		// Raw 데이터가 있는 통화 생성
		callIdWithRaw = createCallWithAnalysisAndRaw(userMember.getId(), scenario.getId());

		// Raw 데이터가 없는 통화 생성
		callIdWithoutRaw = createCallWithoutAnalysis(userMember.getId(), scenario.getId());

		// Admin 로그인
		SignInRequest adminSignInRequest = new SignInRequest(adminEmail, VALID_PASSWORD);
		String adminSignInResponse = mockMvc.perform(post(SIGN_IN_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(adminSignInRequest)))
			.andReturn()
			.getResponse()
			.getContentAsString();
		adminAccessToken = JsonPath.read(adminSignInResponse, "$.item.accessToken");

		// User 로그인
		SignInRequest userSignInRequest = new SignInRequest(userEmail, VALID_PASSWORD);
		String userSignInResponse = mockMvc.perform(post(SIGN_IN_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userSignInRequest)))
			.andReturn()
			.getResponse()
			.getContentAsString();
		userAccessToken = JsonPath.read(userSignInResponse, "$.item.accessToken");
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

	private Long createCallWithAnalysisAndRaw(Long memberId, Long scenarioId) {
		CallEntity call = CallEntity.create(
			memberId,
			scenarioId,
			"Cafe Order",
			com.voco.voco.app.scenario.domain.model.Level.BEGINNER,
			"room-test-" + UUID.randomUUID()
		);
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

		// Raw 데이터 생성
		CallAnalysisRawEntity raw = CallAnalysisRawEntity.create(
			analysis.getId(),
			List.of(
				new CallAnalysisRawEntity.ConversationRaw("assistant", "Welcome!"),
				new CallAnalysisRawEntity.ConversationRaw("user", "Hello!")
			),
			Map.of("summary", Map.of("overall_completion_score", 80)),
			Map.of("scoring", Map.of("final_score", 90))
		);
		entityManager.persist(raw);
		entityManager.flush();

		return call.getId();
	}

	private Long createCallWithoutAnalysis(Long memberId, Long scenarioId) {
		CallEntity call = CallEntity.create(
			memberId,
			scenarioId,
			"Cafe Order",
			com.voco.voco.app.scenario.domain.model.Level.BEGINNER,
			"room-test-" + UUID.randomUUID()
		);
		entityManager.persist(call);
		entityManager.flush();
		return call.getId();
	}

	@Test
	@DisplayName("관리자 통화 분석 Raw 데이터 조회에 성공한다")
	void getAdminCallAnalysisRaw_Success() throws Exception {
		// when
		ResultActions result = mockMvc.perform(get(ADMIN_CALLS_URL + "/" + callIdWithRaw + "/analysis-raw")
			.header("Authorization", "Bearer " + adminAccessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("SUCCESS"))
			.andExpect(jsonPath("$.item.conversation").isArray())
			.andExpect(jsonPath("$.item.conversation.length()").value(2))
			.andExpect(jsonPath("$.item.conversation[0].role").value("assistant"))
			.andExpect(jsonPath("$.item.conversation[1].role").value("user"))
			.andExpect(jsonPath("$.item.taskCompletion").exists())
			.andExpect(jsonPath("$.item.languageAccuracy").exists());
	}

	@Test
	@DisplayName("분석 Raw 데이터가 없으면 404를 반환한다")
	void getAdminCallAnalysisRaw_NotFound() throws Exception {
		// when
		ResultActions result = mockMvc.perform(get(ADMIN_CALLS_URL + "/" + callIdWithoutRaw + "/analysis-raw")
			.header("Authorization", "Bearer " + adminAccessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.type").value("FAIL"))
			.andExpect(jsonPath("$.exception.errorNo").value("CALL-2"));
	}

	@Test
	@DisplayName("일반 사용자는 관리자 API에 접근할 수 없다")
	void getAdminCallAnalysisRaw_NotAdmin_Fail() throws Exception {
		// when
		ResultActions result = mockMvc.perform(get(ADMIN_CALLS_URL + "/" + callIdWithRaw + "/analysis-raw")
			.header("Authorization", "Bearer " + userAccessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.type").value("FAIL"));
	}

	@Test
	@DisplayName("토큰이 없으면 조회에 실패한다")
	void getAdminCallAnalysisRaw_NoToken_Fail() throws Exception {
		// when
		ResultActions result = mockMvc.perform(get(ADMIN_CALLS_URL + "/" + callIdWithRaw + "/analysis-raw"));

		// then
		result
			.andDo(print())
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.type").value("FAIL"));
	}
}
