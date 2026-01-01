package com.voco.voco.app.call.presentation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.voco.voco.app.auth.presentation.controller.dto.in.SignInRequest;
import com.voco.voco.app.call.domain.model.CallEntity;
import com.voco.voco.app.call.presentation.controller.dto.in.CreateCallAnalysisRequest;
import com.voco.voco.app.member.domain.model.Level;
import com.voco.voco.app.member.domain.model.MemberEntity;

import jakarta.persistence.EntityManager;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("POST /api/v1/calls/{callId}/analyses")
class CreateCallAnalysisApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private static final String SIGN_IN_URL = "/api/v1/auth/sign-in";
	private static final String VALID_PASSWORD = "Password1!";

	private String accessToken;
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

		CallEntity call = CallEntity.create(
			member.getId(),
			1L,
			"Test Scenario",
			com.voco.voco.app.scenario.domain.model.Level.BEGINNER,
			"room-test-123"
		);
		entityManager.persist(call);
		entityManager.flush();
		callId = call.getId();

		SignInRequest signInRequest = new SignInRequest(testEmail, VALID_PASSWORD);
		String signInResponse = mockMvc.perform(post(SIGN_IN_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signInRequest)))
			.andReturn()
			.getResponse()
			.getContentAsString();

		accessToken = JsonPath.read(signInResponse, "$.item.accessToken");
	}

	private CreateCallAnalysisRequest createValidRequest() {
		List<CreateCallAnalysisRequest.ConversationRequest> conversation = List.of(
			new CreateCallAnalysisRequest.ConversationRequest("assistant", "Welcome! What would you like to order?"),
			new CreateCallAnalysisRequest.ConversationRequest("user", "I wanting coffee."),
			new CreateCallAnalysisRequest.ConversationRequest("assistant", "What size would you like?"),
			new CreateCallAnalysisRequest.ConversationRequest("user", "Medium, please.")
		);

		CreateCallAnalysisRequest.TaskSummaryRequest taskSummary = new CreateCallAnalysisRequest.TaskSummaryRequest(
			100,
			"completed",
			"시나리오가 완벽하게 완료되었습니다."
		);

		CreateCallAnalysisRequest.TaskCompletionRequest taskCompletion = new CreateCallAnalysisRequest.TaskCompletionRequest(
			taskSummary,
			null,
			null,
			null,
			null
		);

		List<CreateCallAnalysisRequest.ErrorRequest> errors = List.of(
			new CreateCallAnalysisRequest.ErrorRequest(
				2,
				"grammar",
				"verb_form",
				"wanting",
				"want",
				"현재 진행형 대신 단순 현재형을 사용해야 합니다.",
				"major",
				"I wanting coffee."
			)
		);

		CreateCallAnalysisRequest.ScoringRequest scoring = new CreateCallAnalysisRequest.ScoringRequest(
			100,
			"excellent",
			100,
			5,
			10
		);

		CreateCallAnalysisRequest.FeedbackRequest feedback = new CreateCallAnalysisRequest.FeedbackRequest(
			List.of("대부분의 문장을 정확하게 표현함"),
			List.of("동사 형태 사용에 주의 필요"),
			List.of("현재형 vs 현재진행형 구분"),
			List.of("상태 동사는 -ing 형태를 쓰지 않습니다")
		);

		CreateCallAnalysisRequest.LanguageAccuracyRequest languageAccuracy = new CreateCallAnalysisRequest.LanguageAccuracyRequest(
			scoring,
			feedback,
			"4개의 발화 중 1개의 문법 오류가 발견되었습니다.",
			errors,
			4,
			3,
			null,
			null
		);

		return new CreateCallAnalysisRequest(conversation, taskCompletion, languageAccuracy);
	}

	private String getUrl(Long callId) {
		return "/api/v1/calls/" + callId + "/analyses";
	}

	@Nested
	@DisplayName("성공 케이스")
	class Success {

		@Test
		@DisplayName("통화 분석 결과 저장에 성공한다")
		void createCallAnalysis_Success() throws Exception {
			// given
			CreateCallAnalysisRequest request = createValidRequest();

			// when & then
			mockMvc.perform(post(getUrl(callId))
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.item.analysisId").exists());
		}
	}

	@Nested
	@DisplayName("실패 케이스")
	class Failure {

		@Test
		@DisplayName("존재하지 않는 통화 ID로 요청하면 404를 반환한다")
		void createCallAnalysis_CallNotFound() throws Exception {
			// given
			CreateCallAnalysisRequest request = createValidRequest();

			// when & then
			mockMvc.perform(post(getUrl(99999L))
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
				.andDo(print())
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("필수 필드가 누락되면 400을 반환한다")
		void createCallAnalysis_ValidationFailed() throws Exception {
			// given - taskCompletion.summary가 null인 경우
			List<CreateCallAnalysisRequest.ConversationRequest> conversation = List.of(
				new CreateCallAnalysisRequest.ConversationRequest("assistant", "Hello")
			);

			CreateCallAnalysisRequest.TaskCompletionRequest taskCompletion = new CreateCallAnalysisRequest.TaskCompletionRequest(
				null, null, null, null, null
			);

			CreateCallAnalysisRequest.LanguageAccuracyRequest languageAccuracy = new CreateCallAnalysisRequest.LanguageAccuracyRequest(
				new CreateCallAnalysisRequest.ScoringRequest(100, null, null, null, null),
				new CreateCallAnalysisRequest.FeedbackRequest(
					List.of("강점"), List.of("개선점"), List.of("집중영역"), List.of("팁")
				),
				"설명",
				null, null, null, null, null
			);

			CreateCallAnalysisRequest request = new CreateCallAnalysisRequest(conversation, taskCompletion, languageAccuracy);

			// when & then
			mockMvc.perform(post(getUrl(callId))
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
				.andDo(print())
				.andExpect(status().isBadRequest());
		}
	}
}
