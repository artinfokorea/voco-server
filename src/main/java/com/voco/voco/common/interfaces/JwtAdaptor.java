package com.voco.voco.common.interfaces;

import org.springframework.security.core.Authentication;

public interface JwtAdaptor {

	String createAccessToken(Long memberId, long validityMinutes);

	String createRefreshToken(Long memberId, long validityDays);

	Long extractMemberId(String token);

	Long extractMemberIdIgnoreExpiration(String token);

	boolean validateToken(String token);

	Authentication getAuthentication(String token);
}
