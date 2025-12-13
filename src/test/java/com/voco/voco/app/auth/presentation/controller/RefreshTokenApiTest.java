package com.voco.voco.app.auth.presentation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Set;
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
import com.voco.voco.app.member.domain.model.Category;
import com.voco.voco.app.member.domain.model.Level;
import com.voco.voco.app.member.presentation.controller.dto.in.SignUpRequest;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("POST /api/v1/auth/refresh")
class RefreshTokenApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private static final String REFRESH_URL = "/api/v1/auth/refresh";
	private static final String SIGN_IN_URL = "/api/v1/auth/sign-in";
	private static final String SIGN_UP_URL = "/api/v1/members/sign-up";
	private static final String VALID_PASSWORD = "Password1!";

	private String uniqueEmail() {
		return "test-" + UUID.randomUUID() + "@example.com";
	}

	private void createMember(String email) throws Exception {
		SignUpRequest signUpRequest = new SignUpRequest(
			"홍길동",
			"Hong Gildong",
			email,
			VALID_PASSWORD,
			Level.BEGINNER,
			Set.of(Category.DAILY)
		);
		mockMvc.perform(post(SIGN_UP_URL)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(signUpRequest)));
	}

	private JsonNode signIn(String email) throws Exception {
		SignInRequest signInRequest = new SignInRequest(email, VALID_PASSWORD);
		MvcResult result = mockMvc.perform(post(SIGN_IN_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signInRequest)))
			.andReturn();

		return objectMapper.readTree(result.getResponse().getContentAsString()).get("item");
	}

	@Test
	@DisplayName("유효한 토큰으로 갱신에 성공한다")
	void refresh_Success() throws Exception {
		// given
		String email = uniqueEmail();
		createMember(email);
		JsonNode tokens = signIn(email);

		RefreshTokenRequest request = new RefreshTokenRequest(
			tokens.get("accessToken").asText(),
			tokens.get("refreshToken").asText()
		);

		// when
		ResultActions result = mockMvc.perform(post(REFRESH_URL)
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
	@DisplayName("다른 회원의 토큰 조합으로 갱신 시 실패한다")
	void refresh_MemberIdMismatch_Fail() throws Exception {
		// given
		String email1 = uniqueEmail();
		String email2 = uniqueEmail();
		createMember(email1);
		createMember(email2);

		JsonNode tokens1 = signIn(email1);
		JsonNode tokens2 = signIn(email2);

		// 회원1의 accessToken + 회원2의 refreshToken 조합
		RefreshTokenRequest request = new RefreshTokenRequest(
			tokens1.get("accessToken").asText(),
			tokens2.get("refreshToken").asText()
		);

		// when
		ResultActions result = mockMvc.perform(post(REFRESH_URL)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		result
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.type").value("FAIL"))
			.andExpect(jsonPath("$.exception.errorNo").value("AUTH-6"));
	}

	@Test
	@DisplayName("필수 값이 없으면 갱신에 실패한다")
	void refresh_MissingRequiredField_Fail() throws Exception {
		// given
		String requestJson = """
			{
				"accessToken": "someAccessToken"
			}
			""";

		// when
		ResultActions result = mockMvc.perform(post(REFRESH_URL)
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestJson));

		// then
		result
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("갱신 후 새로운 토큰으로 다시 갱신할 수 있다")
	void refresh_ThenRefreshAgain_Success() throws Exception {
		// given
		String email = uniqueEmail();
		createMember(email);
		JsonNode tokens = signIn(email);

		RefreshTokenRequest firstRequest = new RefreshTokenRequest(
			tokens.get("accessToken").asText(),
			tokens.get("refreshToken").asText()
		);

		// 첫 번째 갱신
		MvcResult firstResult = mockMvc.perform(post(REFRESH_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(firstRequest)))
			.andExpect(status().isOk())
			.andReturn();

		JsonNode newTokens = objectMapper.readTree(firstResult.getResponse().getContentAsString()).get("item");

		// when - 새로운 토큰으로 두 번째 갱신
		RefreshTokenRequest secondRequest = new RefreshTokenRequest(
			newTokens.get("accessToken").asText(),
			newTokens.get("refreshToken").asText()
		);

		ResultActions result = mockMvc.perform(post(REFRESH_URL)
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