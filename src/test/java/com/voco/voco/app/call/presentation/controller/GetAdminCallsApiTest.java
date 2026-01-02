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
@DisplayName("GET /api/v1/admin/calls")
class GetAdminCallsApiTest {

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
	private Long scenarioId;

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
		scenarioId = scenario.getId();

		// 통화 데이터 생성
		createCallWithAnalysis(userMember.getId(), scenarioId);
		createCallWithAnalysis(userMember.getId(), scenarioId);

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
			"AI 역할",
			"user role",
			"사용자 역할",
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

	private void createCallWithAnalysis(Long memberId, Long scenarioId) {
		CallEntity call = CallEntity.create(
			memberId,
			scenarioId,
			"Cafe Order",
			com.voco.voco.app.scenario.domain.model.Level.BEGINNER,
			"room-test-" + UUID.randomUUID()
		);
		entityManager.persist(call);

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
	@DisplayName("관리자 통화 내역 목록 조회에 성공한다")
	void getAdminCalls_Success() throws Exception {
		// when
		ResultActions result = mockMvc.perform(get(ADMIN_CALLS_URL)
			.header("Authorization", "Bearer " + adminAccessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("SUCCESS"))
			.andExpect(jsonPath("$.item.content").isArray())
			.andExpect(jsonPath("$.item.content.length()").value(2))
			.andExpect(jsonPath("$.item.content[0].callId").exists())
			.andExpect(jsonPath("$.item.content[0].scenarioName").exists())
			.andExpect(jsonPath("$.item.content[0].memberName").exists())
			.andExpect(jsonPath("$.item.content[0].memberId").exists());
	}

	@Test
	@DisplayName("일반 사용자는 관리자 API에 접근할 수 없다")
	void getAdminCalls_NotAdmin_Fail() throws Exception {
		// when
		ResultActions result = mockMvc.perform(get(ADMIN_CALLS_URL)
			.header("Authorization", "Bearer " + userAccessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.type").value("FAIL"));
	}

	@Test
	@DisplayName("토큰이 없으면 조회에 실패한다")
	void getAdminCalls_NoToken_Fail() throws Exception {
		// when
		ResultActions result = mockMvc.perform(get(ADMIN_CALLS_URL));

		// then
		result
			.andDo(print())
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.type").value("FAIL"));
	}

	@Test
	@DisplayName("페이징이 적용된다")
	void getAdminCalls_Pagination() throws Exception {
		// when
		ResultActions result = mockMvc.perform(get(ADMIN_CALLS_URL)
			.header("Authorization", "Bearer " + adminAccessToken)
			.param("page", "1")
			.param("size", "10"));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("SUCCESS"))
			.andExpect(jsonPath("$.item.page.number").value(1))
			.andExpect(jsonPath("$.item.page.size").value(10));
	}
}
