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
public class GoogleAuthClient {

	private static final String GOOGLE_USERINFO_URL = "https://www.googleapis.com/oauth2/v3/userinfo";

	private final RestClient restClient;
	private final ObjectMapper objectMapper;

	public SocialUserInfo verifyToken(String accessToken) {
		try {
			String response = restClient.get()
				.uri(GOOGLE_USERINFO_URL)
				.header("Authorization", "Bearer " + accessToken)
				.retrieve()
				.body(String.class);

			Map<String, Object> userInfo = objectMapper.readValue(response,
				objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class));

			String providerId = (String)userInfo.get("sub");
			String email = (String)userInfo.get("email");

			if (providerId == null) {
				throw new CoreException(ApiErrorType.INVALID_SOCIAL_TOKEN);
			}

			return new SocialUserInfo(providerId, email);
		} catch (CoreException e) {
			throw e;
		} catch (Exception e) {
			log.error("Google token verification failed", e);
			throw new CoreException(ApiErrorType.INVALID_SOCIAL_TOKEN);
		}
	}
}
