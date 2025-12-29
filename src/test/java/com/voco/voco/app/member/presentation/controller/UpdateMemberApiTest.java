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
import com.jayway.jsonpath.JsonPath;
import com.voco.voco.app.auth.presentation.controller.dto.in.SignInRequest;
import com.voco.voco.app.member.domain.model.Level;
import com.voco.voco.app.member.presentation.controller.dto.in.SignUpRequest;
import com.voco.voco.app.member.presentation.controller.dto.in.UpdateMemberRequest;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("PUT /api/v1/members")
class UpdateMemberApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private static final String MEMBER_URL = "/api/v1/members";
	private static final String SIGN_UP_URL = "/api/v1/members/sign-up";
	private static final String SIGN_IN_URL = "/api/v1/auth/sign-in";
	private static final String VALID_PASSWORD = "Password1!";

	private String uniqueEmail() {
		return "test-" + UUID.randomUUID() + "@example.com";
	}

	private String signUpAndSignIn(String email) throws Exception {
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

		SignInRequest signInRequest = new SignInRequest(email, VALID_PASSWORD);
		String signInResponse = mockMvc.perform(post(SIGN_IN_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signInRequest)))
			.andReturn()
			.getResponse()
			.getContentAsString();

		return JsonPath.read(signInResponse, "$.item.accessToken");
	}

	@Test
	@DisplayName("유효한 요청으로 회원 정보 수정에 성공한다")
	void updateMember_Success() throws Exception {
		// given
		String email = uniqueEmail();
		String accessToken = signUpAndSignIn(email);

		UpdateMemberRequest request = new UpdateMemberRequest(
			"New English Name",
			Level.INTERMEDIATE
		);

		// when
		ResultActions result = mockMvc.perform(put(MEMBER_URL)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("SUCCESS"));
	}

	@Test
	@DisplayName("인증 없이 회원 정보 수정 시 실패한다")
	void updateMember_Unauthorized_Fail() throws Exception {
		// given
		UpdateMemberRequest request = new UpdateMemberRequest(
			"New English Name",
			Level.INTERMEDIATE
		);

		// when
		ResultActions result = mockMvc.perform(put(MEMBER_URL)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		result
			.andDo(print())
			.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("영문 이름이 비어있으면 수정에 실패한다")
	void updateMember_BlankEnglishName_Fail() throws Exception {
		// given
		String email = uniqueEmail();
		String accessToken = signUpAndSignIn(email);

		UpdateMemberRequest request = new UpdateMemberRequest(
			"",
			Level.INTERMEDIATE
		);

		// when
		ResultActions result = mockMvc.perform(put(MEMBER_URL)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		result
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("레벨이 null이면 수정에 실패한다")
	void updateMember_NullLevel_Fail() throws Exception {
		// given
		String email = uniqueEmail();
		String accessToken = signUpAndSignIn(email);

		String requestJson = """
			{
				"englishName": "New English Name"
			}
			""";

		// when
		ResultActions result = mockMvc.perform(put(MEMBER_URL)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(requestJson));

		// then
		result
			.andDo(print())
			.andExpect(status().isBadRequest());
	}
}
