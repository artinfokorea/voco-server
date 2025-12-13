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
import com.voco.voco.app.call.domain.model.CallAnalysisEntity;
import com.voco.voco.app.call.domain.model.CallEntity;
import com.voco.voco.app.call.infrastructure.repository.CallAnalysisJpaRepository;
import com.voco.voco.app.call.infrastructure.repository.CallJpaRepository;
import com.voco.voco.app.member.domain.interfaces.MemberQueryRepository;
import com.voco.voco.app.member.domain.model.Category;
import com.voco.voco.app.member.domain.model.Level;
import com.voco.voco.app.member.presentation.controller.dto.in.SignUpRequest;
import com.voco.voco.app.scenario.domain.model.ScenarioEntity;

import jakarta.persistence.EntityManager;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("GET /api/v1/call-analyses/{id}")
class GetCallAnalysisApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private CallAnalysisJpaRepository callAnalysisJpaRepository;

	@Autowired
	private CallJpaRepository callJpaRepository;

	@Autowired
	private MemberQueryRepository memberQueryRepository;

	@Autowired
	private EntityManager entityManager;

	private static final String CALL_ANALYSES_URL = "/api/v1/call-analyses";
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

	private ScenarioEntity createScenario() {
		ScenarioEntity scenario = ScenarioEntity.create(
			"테스트 시나리오",
			"테스트 설명",
			com.voco.voco.app.scenario.domain.model.Level.BEGINNER,
			com.voco.voco.app.scenario.domain.model.Category.DAILY,
			"test content"
		);
		entityManager.persist(scenario);
		entityManager.flush();
		return scenario;
	}

	private CallAnalysisEntity createCallAnalysisWithCall() {
		ScenarioEntity scenario = createScenario();

		CallAnalysisEntity analysis = CallAnalysisEntity.create(
			"{\"pronunciation\": 85, \"grammar\": 90}",
			85,
			"전반적으로 좋은 발음과 문법을 사용했습니다."
		);
		callAnalysisJpaRepository.save(analysis);

		CallEntity call = CallEntity.create(memberId, scenario.getId());
		call.updateAnalysisId(analysis.getId());
		callJpaRepository.save(call);

		return analysis;
	}

	@Test
	@DisplayName("분석 결과 조회에 성공한다")
	void getCallAnalysis_Success() throws Exception {
		// given
		CallAnalysisEntity analysis = createCallAnalysisWithCall();

		// when
		ResultActions result = mockMvc.perform(get(CALL_ANALYSES_URL + "/" + analysis.getId())
			.header("Authorization", "Bearer " + accessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("SUCCESS"))
			.andExpect(jsonPath("$.item.id").value(analysis.getId()))
			.andExpect(jsonPath("$.item.content").exists())
			.andExpect(jsonPath("$.item.score").value(85))
			.andExpect(jsonPath("$.item.summary").exists());
	}

	@Test
	@DisplayName("다른 회원의 분석 결과 조회 시 실패한다")
	void getCallAnalysis_OtherMember_Forbidden() throws Exception {
		// given
		CallAnalysisEntity analysis = createCallAnalysisWithCall();

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
		ResultActions result = mockMvc.perform(get(CALL_ANALYSES_URL + "/" + analysis.getId())
			.header("Authorization", "Bearer " + otherAccessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.type").value("FAIL"))
			.andExpect(jsonPath("$.exception.errorNo").value("CALL-2"));
	}

	@Test
	@DisplayName("존재하지 않는 분석 결과 조회 시 실패한다")
	void getCallAnalysis_NotFound_Fail() throws Exception {
		// when
		ResultActions result = mockMvc.perform(get(CALL_ANALYSES_URL + "/999999")
			.header("Authorization", "Bearer " + accessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.type").value("FAIL"))
			.andExpect(jsonPath("$.exception.errorNo").value("CALL-2"));
	}

	@Test
	@DisplayName("토큰이 없으면 조회에 실패한다")
	void getCallAnalysis_NoToken_Fail() throws Exception {
		// when
		ResultActions result = mockMvc.perform(get(CALL_ANALYSES_URL + "/1"));

		// then
		result
			.andDo(print())
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.type").value("FAIL"))
			.andExpect(jsonPath("$.exception.errorNo").value("AUTH-1"));
	}
}
