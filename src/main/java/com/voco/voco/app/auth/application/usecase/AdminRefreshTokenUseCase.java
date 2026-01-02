package com.voco.voco.app.auth.application.usecase;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voco.voco.app.auth.application.usecase.dto.in.RefreshTokenUseCaseDto;
import com.voco.voco.app.auth.application.usecase.dto.out.TokenInfo;
import com.voco.voco.app.auth.domain.interfaces.TokenQueryRepository;
import com.voco.voco.app.auth.domain.model.TokenEntity;
import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;
import com.voco.voco.common.interfaces.AdminChecker;
import com.voco.voco.common.interfaces.JwtAdaptor;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminRefreshTokenUseCase {

	private static final long ACCESS_TOKEN_VALIDITY_MINUTES = 30;
	private static final long REFRESH_TOKEN_VALIDITY_DAYS = 180;

	private final TokenQueryRepository tokenQueryRepository;
	private final JwtAdaptor jwtAdaptor;
	private final AdminChecker adminChecker;

	public TokenInfo execute(RefreshTokenUseCaseDto dto) {
		Long accessTokenMemberId = jwtAdaptor.extractMemberIdIgnoreExpiration(dto.accessToken());
		Long refreshTokenMemberId = jwtAdaptor.extractMemberId(dto.refreshToken());

		if (!accessTokenMemberId.equals(refreshTokenMemberId)) {
			throw new CoreException(ApiErrorType.INVALID_TOKEN);
		}

		adminChecker.validateAdmin(accessTokenMemberId);

		TokenEntity token = tokenQueryRepository.findByMemberId(accessTokenMemberId)
			.orElseThrow(() -> new CoreException(ApiErrorType.TOKEN_NOT_FOUND));

		if (!token.getRefreshToken().equals(dto.refreshToken())) {
			throw new CoreException(ApiErrorType.INVALID_TOKEN);
		}

		if (token.isRefreshTokenExpired()) {
			throw new CoreException(ApiErrorType.TOKEN_EXPIRED);
		}

		String newAccessToken = jwtAdaptor.createAccessToken(accessTokenMemberId, ACCESS_TOKEN_VALIDITY_MINUTES);
		String newRefreshToken = jwtAdaptor.createRefreshToken(accessTokenMemberId, REFRESH_TOKEN_VALIDITY_DAYS);

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime accessTokenExpiredAt = now.plusMinutes(ACCESS_TOKEN_VALIDITY_MINUTES);
		LocalDateTime refreshTokenExpiredAt = now.plusDays(REFRESH_TOKEN_VALIDITY_DAYS);

		token.updateTokens(newAccessToken, newRefreshToken, accessTokenExpiredAt, refreshTokenExpiredAt);

		return new TokenInfo(newAccessToken, newRefreshToken);
	}
}