package com.voco.voco.app.auth.infrastructure.adaptor;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voco.voco.app.auth.application.interfaces.dto.SocialUserInfo;
import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppleAuthClient {

	private static final String APPLE_PUBLIC_KEYS_URL = "https://appleid.apple.com/auth/keys";

	private final RestClient restClient;
	private final ObjectMapper objectMapper;

	public SocialUserInfo verifyToken(String idToken) {
		try {
			String[] tokenParts = idToken.split("\\.");
			String headerJson = new String(Base64.getUrlDecoder().decode(tokenParts[0]));
			Map<String, String> header = objectMapper.readValue(headerJson,
				objectMapper.getTypeFactory().constructMapType(Map.class, String.class, String.class));

			String kid = header.get("kid");
			PublicKey publicKey = getApplePublicKey(kid);

			Claims claims = Jwts.parser()
				.verifyWith(publicKey)
				.build()
				.parseSignedClaims(idToken)
				.getPayload();

			String providerId = claims.getSubject();
			String email = claims.get("email", String.class);

			return new SocialUserInfo(providerId, email);
		} catch (Exception e) {
			log.error("Apple token verification failed", e);
			throw new CoreException(ApiErrorType.INVALID_SOCIAL_TOKEN);
		}
	}

	private PublicKey getApplePublicKey(String kid) throws Exception {
		String response = restClient.get()
			.uri(APPLE_PUBLIC_KEYS_URL)
			.retrieve()
			.body(String.class);

		Map<String, Object> keysResponse = objectMapper.readValue(response,
			objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class));

		@SuppressWarnings("unchecked")
		List<Map<String, String>> keys = (List<Map<String, String>>)keysResponse.get("keys");

		Map<String, String> matchingKey = keys.stream()
			.filter(key -> kid.equals(key.get("kid")))
			.findFirst()
			.orElseThrow(() -> new CoreException(ApiErrorType.INVALID_SOCIAL_TOKEN));

		byte[] nBytes = Base64.getUrlDecoder().decode(matchingKey.get("n"));
		byte[] eBytes = Base64.getUrlDecoder().decode(matchingKey.get("e"));

		BigInteger n = new BigInteger(1, nBytes);
		BigInteger e = new BigInteger(1, eBytes);

		RSAPublicKeySpec spec = new RSAPublicKeySpec(n, e);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");

		return keyFactory.generatePublic(spec);
	}
}
