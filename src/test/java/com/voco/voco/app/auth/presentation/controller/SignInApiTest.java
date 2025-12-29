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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voco.voco.app.auth.presentation.controller.dto.in.SignInRequest;
import com.voco.voco.app.member.domain.model.Level;
import com.voco.voco.app.member.presentation.controller.dto.in.SignUpRequest;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("POST /api/v1/auth/sign-in")
class SignInApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private static final String SIGN_IN_URL = "/api/v1/auth/sign-in";
	private static final String SIGN_UP_URL = "/api/v1/members/sign-up";
	private static final String VALID_PASSWORD = "Password1!";

	private String uniqueEmail() {
		return "test-" + UUID.randomUUID() + "@example.com";
	}

	private void createMember(String email, String password) throws Exception {
		SignUpRequest signUpRequest = new SignUpRequest(
			"홍길동",
			"Hong Gildong",
			email,
			password,
			Level.BEGINNER
		);
		mockMvc.perform(post(SIGN_UP_URL)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(signUpRequest)));
	}

	@Test
	@DisplayName("유효한 요청으로 로그인에 성공한다")
	void signIn_Success() throws Exception {
		// given
		String email = uniqueEmail();
		createMember(email, VALID_PASSWORD);

		SignInRequest request = new SignInRequest(email, VALID_PASSWORD);

		// when
		ResultActions result = mockMvc.perform(post(SIGN_IN_URL)
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
	@DisplayName("존재하지 않는 이메일로 로그인 시 실패한다")
	void signIn_MemberNotFound_Fail() throws Exception {
		// given
		SignInRequest request = new SignInRequest("notfound@example.com", VALID_PASSWORD);

		// when
		ResultActions result = mockMvc.perform(post(SIGN_IN_URL)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		result
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.type").value("FAIL"))
			.andExpect(jsonPath("$.exception.errorNo").value("MEMBER-1"));
	}

	@Test
	@DisplayName("비밀번호가 일치하지 않으면 로그인에 실패한다")
	void signIn_InvalidPassword_Fail() throws Exception {
		// given
		String email = uniqueEmail();
		createMember(email, VALID_PASSWORD);

		SignInRequest request = new SignInRequest(email, "WrongPassword1!");

		// when
		ResultActions result = mockMvc.perform(post(SIGN_IN_URL)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		result
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.type").value("FAIL"))
			.andExpect(jsonPath("$.exception.errorNo").value("MEMBER-3"));
	}

	@Test
	@DisplayName("필수 값이 없으면 로그인에 실패한다")
	void signIn_MissingRequiredField_Fail() throws Exception {
		// given
		String requestJson = """
			{
				"email": "test@example.com"
			}
			""";

		// when
		ResultActions result = mockMvc.perform(post(SIGN_IN_URL)
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestJson));

		// then
		result
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("잘못된 이메일 형식으로 로그인 시 실패한다")
	void signIn_InvalidEmailFormat_Fail() throws Exception {
		// given
		SignInRequest request = new SignInRequest("invalid-email", VALID_PASSWORD);

		// when
		ResultActions result = mockMvc.perform(post(SIGN_IN_URL)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		result
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("동일한 회원이 다시 로그인하면 토큰이 갱신된다")
	void signIn_ReLogin_TokenUpdated() throws Exception {
		// given
		String email = uniqueEmail();
		createMember(email, VALID_PASSWORD);

		SignInRequest request = new SignInRequest(email, VALID_PASSWORD);

		// 첫 번째 로그인
		mockMvc.perform(post(SIGN_IN_URL)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// when - 두 번째 로그인
		ResultActions result = mockMvc.perform(post(SIGN_IN_URL)
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
}
