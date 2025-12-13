package com.voco.voco.app.auth.application.interfaces;

import org.springframework.security.core.Authentication;

public interface JwtProvider {

	Long extractMemberId(String token);

	boolean validateToken(String token);

	Authentication getAuthentication(String token);
}