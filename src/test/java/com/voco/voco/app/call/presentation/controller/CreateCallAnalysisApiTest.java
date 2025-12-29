package com.voco.voco.app.call.presentation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;
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

		CallEntity call = CallEntity.create(member.getId(), 1L, "room-test-123");
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
		return new CreateCallAnalysisRequest(
			Map.of(
				"slot_analysis", Map.of("total_slots", 3, "filled_slots", 3),
				"summary", Map.of("overall_completion_score", 100, "status", "completed")
			),
			new CreateCallAnalysisRequest.LanguageAccuracyRequest(
				4,
				3,
				List.of(
					Map.of("turn", 2, "original_utterance", "I wanting coffee.", "is_correct", false),
					Map.of("turn", 4, "original_utterance", "Medium, please.", "is_correct", true)
				),
				List.of(
					Map.of("turn", 2, "error_type", "grammar", "severity", "major")
				),
				Map.of("total_errors", 1, "by_type", Map.of("grammar", 1)),
				new CreateCallAnalysisRequest.ScoringRequest(
					100, 5, 0, 5, 0, 10, 100, "excellent"
				),
				new CreateCallAnalysisRequest.FeedbackRequest(
					List.of("대부분의 문장을 정확하게 표현함"),
					List.of("동사 형태 사용에 주의 필요"),
					List.of("현재형 vs 현재진행형 구분"),
					List.of("상태 동사는 -ing 형태를 쓰지 않습니다")
				),
				"4개의 발화 중 1개의 문법 오류가 발견되었습니다."
			)
		);
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
	}
}
