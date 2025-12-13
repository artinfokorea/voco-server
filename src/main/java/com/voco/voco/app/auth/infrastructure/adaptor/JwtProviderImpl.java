package com.voco.voco.app.auth.infrastructure.adaptor;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.voco.voco.app.auth.application.interfaces.JwtProvider;
import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtProviderImpl implements JwtProvider {

	private final SecretKey secretKey;

	public JwtProviderImpl(@Value("${jwt.secret-key}") String secretKey) {
		this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public Long extractMemberId(String token) {
		Claims claims = parseClaims(token);
		return claims.get("memberId", Long.class);
	}

	@Override
	public boolean validateToken(String token) {
		try {
			parseClaims(token);
			return true;
		} catch (ExpiredJwtException e) {
			throw new CoreException(ApiErrorType.TOKEN_EXPIRED);
		} catch (JwtException e) {
			throw new CoreException(ApiErrorType.INVALID_TOKEN);
		}
	}

	@Override
	public Authentication getAuthentication(String token) {
		Long memberId = extractMemberId(token);
		return new UsernamePasswordAuthenticationToken(memberId, null, null);
	}

	private Claims parseClaims(String token) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}
}