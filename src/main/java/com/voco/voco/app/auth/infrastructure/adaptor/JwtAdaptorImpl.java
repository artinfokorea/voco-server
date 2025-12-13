package com.voco.voco.app.auth.infrastructure.adaptor;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;
import com.voco.voco.common.interfaces.JwtAdaptor;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtAdaptorImpl implements JwtAdaptor {

	private static final long ACCESS_TOKEN_VALIDITY = 1000L * 60 * 30; // 30분
	private static final long REFRESH_TOKEN_VALIDITY = 1000L * 60 * 60 * 24 * 7; // 7일

	private final SecretKey secretKey;

	public JwtAdaptorImpl(@Value("${jwt.secret-key}") String secretKey) {
		this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public String createAccessToken(Long memberId) {
		return createToken(memberId, ACCESS_TOKEN_VALIDITY);
	}

	@Override
	public String createRefreshToken(Long memberId) {
		return createToken(memberId, REFRESH_TOKEN_VALIDITY);
	}

	private String createToken(Long memberId, long validity) {
		Date now = new Date();
		Date expiration = new Date(now.getTime() + validity);

		return Jwts.builder()
			.claim("memberId", memberId)
			.issuedAt(now)
			.expiration(expiration)
			.signWith(secretKey)
			.compact();
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