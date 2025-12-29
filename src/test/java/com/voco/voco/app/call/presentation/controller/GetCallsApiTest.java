package com.voco.voco.app.call.presentation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Set;
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
import com.voco.voco.app.call.domain.model.CallEntity;
import com.voco.voco.app.call.infrastructure.repository.CallJpaRepository;
import com.voco.voco.app.member.domain.interfaces.MemberQueryRepository;
import com.voco.voco.app.member.domain.model.Category;
import com.voco.voco.app.member.domain.model.Level;
import com.voco.voco.app.member.presentation.controller.dto.in.SignUpRequest;

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
	private CallJpaRepository callJpaRepository;

	@Autowired
	private MemberQueryRepository memberQueryRepository;

	private static final String CALLS_URL = "/api/v1/calls";
	private static final String SIGN_UP_URL = "/api/v1/members/sign-up";
	private static final String SIGN_IN_URL = "/api/v1/auth/sign-in";
	private static final String VALID_PASSWORD = "Password1!";

	private String accessToken;
	private Long memberId;
	private String testEmail;

	private String uniqueEmail() {
		return "test-" + UUID.randomUUID() + "@example.com";
	}

	@BeforeEach
	void setUp() throws Exception {
		testEmail = uniqueEmail();

		SignUpRequest signUpRequest = new SignUpRequest(
			"홍길동",
			"Hong Gildong",
			testEmail,
			VALID_PASSWORD,
			Level.BEGINNER,
			Set.of(Category.DAILY)
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
		memberId = memberQueryRepository.findByEmail(testEmail)
			.orElseThrow()
			.getId();
	}

	private ScenarioEntity createScenario(String title, String description,
		com.voco.voco.app.scenario.domain.model.Level level,
		com.voco.voco.app.scenario.domain.model.Category category) {
		ScenarioEntity scenario = ScenarioEntity.create(title, description, level, category, "test content");
		entityManager.persist(scenario);
		entityManager.flush();
		return scenario;
	}

	private CallEntity createCall(Long scenarioId) {
		CallEntity call = CallEntity.create(memberId, scenarioId);
		return callJpaRepository.save(call);
	}

	@Test
	@DisplayName("통화 내역 조회에 성공한다")
	void getCalls_Success() throws Exception {
		// given
		ScenarioEntity scenario1 = createScenario("카페 주문", "카페에서 음료 주문하기",
			com.voco.voco.app.scenario.domain.model.Level.BEGINNER,
			com.voco.voco.app.scenario.domain.model.Category.DAILY);
		ScenarioEntity scenario2 = createScenario("비즈니스 미팅", "회의 일정 조율하기",
			com.voco.voco.app.scenario.domain.model.Level.INTERMEDIATE,
			com.voco.voco.app.scenario.domain.model.Category.BUSINESS);

		createCall(scenario1.getId());
		createCall(scenario2.getId());

		// when
		ResultActions result = mockMvc.perform(get(CALLS_URL)
			.header("Authorization", "Bearer " + accessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("SUCCESS"))
			.andExpect(jsonPath("$.item.content").isArray())
			.andExpect(jsonPath("$.item.content.length()").value(2))
			.andExpect(jsonPath("$.item.content[0].scenarioTitle").exists())
			.andExpect(jsonPath("$.item.content[0].scenarioLevel").exists())
			.andExpect(jsonPath("$.item.content[0].scenarioCategory").exists());
	}

	@Test
	@DisplayName("통화 내역이 없으면 빈 리스트를 반환한다")
	void getCalls_Empty_ReturnsEmptyList() throws Exception {
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
	@DisplayName("페이징이 올바르게 동작한다")
	void getCalls_Pagination_Success() throws Exception {
		// given
		ScenarioEntity scenario = createScenario("테스트 시나리오", "테스트 설명",
			com.voco.voco.app.scenario.domain.model.Level.BEGINNER,
			com.voco.voco.app.scenario.domain.model.Category.DAILY);

		for (int i = 0; i < 15; i++) {
			createCall(scenario.getId());
		}

		// when
		ResultActions result = mockMvc.perform(get(CALLS_URL)
			.header("Authorization", "Bearer " + accessToken)
			.param("page", "0")
			.param("size", "10"));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.item.content.length()").value(10))
			.andExpect(jsonPath("$.item.totalElements").value(15))
			.andExpect(jsonPath("$.item.totalPages").value(2));
	}

	@Test
	@DisplayName("다른 사용자의 통화 내역은 조회되지 않는다")
	void getCalls_OtherUserCalls_NotIncluded() throws Exception {
		// given
		ScenarioEntity scenario = createScenario("테스트 시나리오", "테스트 설명",
			com.voco.voco.app.scenario.domain.model.Level.BEGINNER,
			com.voco.voco.app.scenario.domain.model.Category.DAILY);

		createCall(scenario.getId());

		String otherEmail = uniqueEmail();
		SignUpRequest otherSignUpRequest = new SignUpRequest(
			"김철수",
			"Kim Cheolsu",
			otherEmail,
			VALID_PASSWORD,
			Level.INTERMEDIATE,
			Set.of(Category.BUSINESS)
		);
		mockMvc.perform(post(SIGN_UP_URL)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(otherSignUpRequest)));

		SignInRequest otherSignInRequest = new SignInRequest(otherEmail, VALID_PASSWORD);
		String otherSignInResponse = mockMvc.perform(post(SIGN_IN_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(otherSignInRequest)))
			.andReturn()
			.getResponse()
			.getContentAsString();

		String otherAccessToken = JsonPath.read(otherSignInResponse, "$.item.accessToken");

		// when
		ResultActions result = mockMvc.perform(get(CALLS_URL)
			.header("Authorization", "Bearer " + otherAccessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.item.content").isArray())
			.andExpect(jsonPath("$.item.content.length()").value(0));
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
			.andExpect(jsonPath("$.type").value("FAIL"))
			.andExpect(jsonPath("$.exception.errorNo").value("AUTH-1"));
	}
}
