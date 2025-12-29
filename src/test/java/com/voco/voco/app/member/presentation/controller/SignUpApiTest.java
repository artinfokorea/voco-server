package com.voco.voco.app.member.presentation.controller;

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
import com.voco.voco.app.member.domain.model.Level;
import com.voco.voco.app.member.presentation.controller.dto.in.SignUpRequest;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("POST /api/v1/members/sign-up")
class SignUpApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private static final String URL = "/api/v1/members/sign-up";

	private String uniqueEmail() {
		return "test-" + UUID.randomUUID() + "@example.com";
	}

	@Test
	@DisplayName("유효한 요청으로 회원가입에 성공한다")
	void signUp_Success() throws Exception {
		// given
		SignUpRequest request = new SignUpRequest(
			"홍길동",
			"Hong Gildong",
			uniqueEmail(),
			"Password1!",
			Level.BEGINNER
		);

		// when
		ResultActions result = mockMvc.perform(post(URL)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("SUCCESS"))
			.andExpect(jsonPath("$.item.id").isNumber());
	}

	@Test
	@DisplayName("중복된 이메일로 회원가입 시 실패한다")
	void signUp_DuplicateEmail_Fail() throws Exception {
		// given
		String email = uniqueEmail();
		SignUpRequest request = new SignUpRequest(
			"홍길동",
			"Hong Gildong",
			email,
			"Password1!",
			Level.BEGINNER
		);

		// 첫 번째 회원가입 (성공)
		mockMvc.perform(post(URL)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// when - 같은 이메일로 두 번째 회원가입 시도
		SignUpRequest duplicateRequest = new SignUpRequest(
			"김철수",
			"Kim Cheolsu",
			email,
			"Password1!",
			Level.INTERMEDIATE
		);
		ResultActions result = mockMvc.perform(post(URL)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(duplicateRequest)));

		// then
		result
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.type").value("FAIL"))
			.andExpect(jsonPath("$.exception.errorNo").value("MEMBER-4"));
	}

	@Test
	@DisplayName("유효하지 않은 비밀번호로 회원가입 시 실패한다")
	void signUp_InvalidPassword_Fail() throws Exception {
		// given
		SignUpRequest request = new SignUpRequest(
			"홍길동",
			"Hong Gildong",
			uniqueEmail(),
			"short",
			Level.BEGINNER
		);

		// when
		ResultActions result = mockMvc.perform(post(URL)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		result
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.type").value("FAIL"))
			.andExpect(jsonPath("$.exception.errorNo").value("MEMBER-2"));
	}

	@Test
	@DisplayName("필수 값이 없으면 회원가입에 실패한다")
	void signUp_MissingRequiredField_Fail() throws Exception {
		// given
		String requestJson = """
			{
				"koreanName": "홍길동",
				"englishName": "Hong Gildong",
				"password": "Password1!",
				"level": "BEGINNER"
			}
			""";

		// when
		ResultActions result = mockMvc.perform(post(URL)
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestJson));

		// then
		result
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("잘못된 이메일 형식으로 회원가입 시 실패한다")
	void signUp_InvalidEmailFormat_Fail() throws Exception {
		// given
		SignUpRequest request = new SignUpRequest(
			"홍길동",
			"Hong Gildong",
			"invalid-email",
			"Password1!",
			Level.BEGINNER
		);

		// when
		ResultActions result = mockMvc.perform(post(URL)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		result
			.andDo(print())
			.andExpect(status().isBadRequest());
	}
}
