package com.voco.voco.app.member.presentation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import com.voco.voco.app.member.domain.model.Level;
import com.voco.voco.app.member.domain.model.MemberEntity;

import jakarta.persistence.EntityManager;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("GET /api/v1/members/me")
class GetMyInfoApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private static final String ME_URL = "/api/v1/members/me";
	private static final String SIGN_IN_URL = "/api/v1/auth/sign-in";
	private static final String VALID_PASSWORD = "Password1!";

	private String accessToken;
	private String testEmail;

	@BeforeEach
	void setUp() throws Exception {
		testEmail = "test-" + UUID.randomUUID() + "@example.com";

		MemberEntity member = MemberEntity.create(
			"홍길동",
			"Gildong",
			testEmail,
			passwordEncoder.encode(VALID_PASSWORD),
			Level.INTERMEDIATE
		);
		entityManager.persist(member);
		entityManager.flush();

		SignInRequest signInRequest = new SignInRequest(testEmail, VALID_PASSWORD);
		String signInResponse = mockMvc.perform(post(SIGN_IN_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signInRequest)))
			.andReturn()
			.getResponse()
			.getContentAsString();

		accessToken = JsonPath.read(signInResponse, "$.item.accessToken");
	}

	@Test
	@DisplayName("내 정보 조회에 성공한다")
	void getMyInfo_Success() throws Exception {
		// when
		ResultActions result = mockMvc.perform(get(ME_URL)
			.header("Authorization", "Bearer " + accessToken));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("SUCCESS"))
			.andExpect(jsonPath("$.item.koreanName").value("홍길동"))
			.andExpect(jsonPath("$.item.englishName").value("Gildong"))
			.andExpect(jsonPath("$.item.email").value(testEmail))
			.andExpect(jsonPath("$.item.level").value("INTERMEDIATE"));
	}

	@Test
	@DisplayName("토큰이 없으면 조회에 실패한다")
	void getMyInfo_NoToken_Fail() throws Exception {
		// when
		ResultActions result = mockMvc.perform(get(ME_URL));

		// then
		result
			.andDo(print())
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.type").value("FAIL"));
	}

}
