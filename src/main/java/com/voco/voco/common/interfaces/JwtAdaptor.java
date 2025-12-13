package com.voco.voco.common.interfaces;

import org.springframework.security.core.Authentication;

public interface JwtAdaptor {

	Long extractMemberId(String token);

	boolean validateToken(String token);

	Authentication getAuthentication(String token);
}
