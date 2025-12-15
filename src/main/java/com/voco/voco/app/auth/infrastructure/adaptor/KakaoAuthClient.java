package com.voco.voco.app.auth.infrastructure.adaptor;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voco.voco.app.auth.application.interfaces.dto.SocialUserInfo;
import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoAuthClient {

	private static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";

	private final RestClient restClient;
	private final ObjectMapper objectMapper;

	public SocialUserInfo verifyToken(String accessToken) {
		try {
			String response = restClient.get()
				.uri(KAKAO_USER_INFO_URL)
				.header("Authorization", "Bearer " + accessToken)
				.retrieve()
				.body(String.class);

			Map<String, Object> userInfo = objectMapper.readValue(response,
				objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class));

			String providerId = String.valueOf(userInfo.get("id"));

			@SuppressWarnings("unchecked")
			Map<String, Object> kakaoAccount = (Map<String, Object>)userInfo.get("kakao_account");
			String email = kakaoAccount != null ? (String)kakaoAccount.get("email") : null;

			return new SocialUserInfo(providerId, email);
		} catch (CoreException e) {
			throw e;
		} catch (Exception e) {
			log.error("Kakao token verification failed", e);
			throw new CoreException(ApiErrorType.INVALID_SOCIAL_TOKEN);
		}
	}
}
