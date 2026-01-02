package com.voco.voco.app.auth.presentation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voco.voco.app.auth.presentation.controller.dto.in.RefreshTokenRequest;
import com.voco.voco.app.auth.presentation.controller.dto.in.SignInRequest;
import com.voco.voco.app.member.domain.model.Level;
import com.voco.voco.app.member.domain.model.MemberEntity;
import com.voco.voco.app.member.infrastructure.repository.MemberJpaRepository;
import com.voco.voco.app.member.presentation.controller.dto.in.SignUpRequest;
import com.voco.voco.common.interfaces.PasswordAdaptor;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("POST /api/v1/auth/admin/refresh")
class AdminRefreshTokenApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MemberJpaRepository memberJpaRepository;

	@Autowired
	private PasswordAdaptor passwordAdaptor;

	private static final String ADMIN_REFRESH_URL = "/api/v1/auth/admin/refresh";
	private static final String ADMIN_SIGN_IN_URL = "/api/v1/auth/admin/sign-in";
	private static final String USER_SIGN_IN_URL = "/api/v1/auth/sign-in";
	private static final String SIGN_UP_URL = "/api/v1/members/sign-up";
	private static final String VALID_PASSWORD = "Password1!";

	private String uniqueEmail() {
		return "test-" + UUID.randomUUID() + "@example.com";
	}

	private void createAdminMember(String email) {
		String encodedPassword = passwordAdaptor.encode(VALID_PASSWORD);
		MemberEntity admin = MemberEntity.createAdmin("관리자", "Admin", email, encodedPassword, Level.BEGINNER);
		memberJpaRepository.save(admin);
	}

	private void createUserMember(String email) throws Exception {
		SignUpRequest signUpRequest = new SignUpRequest(
			"홍길동",
			"Hong Gildong",
			email,
			VALID_PASSWORD,
			Level.BEGINNER
		);
		mockMvc.perform(post(SIGN_UP_URL)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(signUpRequest)));
	}

	private JsonNode adminSignIn(String email) throws Exception {
		SignInRequest signInRequest = new SignInRequest(email, VALID_PASSWORD);
		MvcResult result = mockMvc.perform(post(ADMIN_SIGN_IN_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signInRequest)))
			.andReturn();

		return objectMapper.readTree(result.getResponse().getContentAsString()).get("item");
	}

	private JsonNode userSignIn(String email) throws Exception {
		SignInRequest signInRequest = new SignInRequest(email, VALID_PASSWORD);
		MvcResult result = mockMvc.perform(post(USER_SIGN_IN_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signInRequest)))
			.andReturn();

		return objectMapper.readTree(result.getResponse().getContentAsString()).get("item");
	}

	@Test
	@DisplayName("관리자가 유효한 토큰으로 갱신에 성공한다")
	void adminRefresh_Success() throws Exception {
		// given
		String email = uniqueEmail();
		createAdminMember(email);
		JsonNode tokens = adminSignIn(email);

		RefreshTokenRequest request = new RefreshTokenRequest(
			tokens.get("accessToken").asText(),
			tokens.get("refreshToken").asText()
		);

		// when
		ResultActions result = mockMvc.perform(post(ADMIN_REFRESH_URL)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("SUCCESS"))
			.andExpect(jsonPath("$.item.accessToken").isString())
			.andExpect(jsonPath("$.item.refreshToken").isString());
	}

	@Test
	@DisplayName("일반 사용자가 관리자 토큰 갱신 API를 호출하면 실패한다")
	void adminRefresh_NotAdmin_Fail() throws Exception {
		// given
		String email = uniqueEmail();
		createUserMember(email);
		JsonNode tokens = userSignIn(email);

		RefreshTokenRequest request = new RefreshTokenRequest(
			tokens.get("accessToken").asText(),
			tokens.get("refreshToken").asText()
		);

		// when
		ResultActions result = mockMvc.perform(post(ADMIN_REFRESH_URL)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		result
			.andDo(print())
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.type").value("FAIL"));
	}

	@Test
	@DisplayName("다른 관리자의 토큰 조합으로 갱신 시 실패한다")
	void adminRefresh_MemberIdMismatch_Fail() throws Exception {
		// given
		String email1 = uniqueEmail();
		String email2 = uniqueEmail();
		createAdminMember(email1);
		createAdminMember(email2);

		JsonNode tokens1 = adminSignIn(email1);
		JsonNode tokens2 = adminSignIn(email2);

		RefreshTokenRequest request = new RefreshTokenRequest(
			tokens1.get("accessToken").asText(),
			tokens2.get("refreshToken").asText()
		);

		// when
		ResultActions result = mockMvc.perform(post(ADMIN_REFRESH_URL)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		result
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.type").value("FAIL"));
	}

	@Test
	@DisplayName("갱신 후 새로운 토큰으로 다시 갱신할 수 있다")
	void adminRefresh_ThenRefreshAgain_Success() throws Exception {
		// given
		String email = uniqueEmail();
		createAdminMember(email);
		JsonNode tokens = adminSignIn(email);

		RefreshTokenRequest firstRequest = new RefreshTokenRequest(
			tokens.get("accessToken").asText(),
			tokens.get("refreshToken").asText()
		);

		MvcResult firstResult = mockMvc.perform(post(ADMIN_REFRESH_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(firstRequest)))
			.andExpect(status().isOk())
			.andReturn();

		JsonNode newTokens = objectMapper.readTree(firstResult.getResponse().getContentAsString()).get("item");

		// when
		RefreshTokenRequest secondRequest = new RefreshTokenRequest(
			newTokens.get("accessToken").asText(),
			newTokens.get("refreshToken").asText()
		);

		ResultActions result = mockMvc.perform(post(ADMIN_REFRESH_URL)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(secondRequest)));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("SUCCESS"))
			.andExpect(jsonPath("$.item.accessToken").isString())
			.andExpect(jsonPath("$.item.refreshToken").isString());
	}
}
