package com.voco.voco.app.auth.application.usecase;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voco.voco.app.auth.application.usecase.dto.in.SignInUseCaseDto;
import com.voco.voco.app.auth.application.usecase.dto.out.TokenInfo;
import com.voco.voco.app.auth.domain.interfaces.TokenCommandRepository;
import com.voco.voco.app.auth.domain.interfaces.TokenQueryRepository;
import com.voco.voco.app.auth.domain.model.TokenEntity;
import com.voco.voco.app.member.domain.interfaces.MemberQueryRepository;
import com.voco.voco.app.member.domain.model.MemberEntity;
import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;
import com.voco.voco.common.interfaces.JwtAdaptor;
import com.voco.voco.common.interfaces.PasswordAdaptor;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SignInUseCase {

	private static final long ACCESS_TOKEN_VALIDITY_MINUTES = 30;
	private static final long REFRESH_TOKEN_VALIDITY_DAYS = 7;

	private final MemberQueryRepository memberQueryRepository;
	private final TokenQueryRepository tokenQueryRepository;
	private final TokenCommandRepository tokenCommandRepository;
	private final JwtAdaptor jwtAdaptor;
	private final PasswordAdaptor passwordAdaptor;

	public TokenInfo execute(SignInUseCaseDto dto) {
		MemberEntity member = memberQueryRepository.findByEmail(dto.email())
			.orElseThrow(() -> new CoreException(ApiErrorType.MEMBER_NOT_FOUND));

		if (!passwordAdaptor.matches(dto.password(), member.getPassword())) {
			throw new CoreException(ApiErrorType.INVALID_PASSWORD_MISMATCH);
		}

		String accessToken = jwtAdaptor.createAccessToken(member.getId());
		String refreshToken = jwtAdaptor.createRefreshToken(member.getId());

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime accessTokenExpiredAt = now.plusMinutes(ACCESS_TOKEN_VALIDITY_MINUTES);
		LocalDateTime refreshTokenExpiredAt = now.plusDays(REFRESH_TOKEN_VALIDITY_DAYS);

		tokenQueryRepository.findByMemberId(member.getId())
			.ifPresentOrElse(
				token -> token.updateTokens(accessToken, refreshToken, accessTokenExpiredAt, refreshTokenExpiredAt),
				() -> tokenCommandRepository.save(
					TokenEntity.create(member.getId(), accessToken, refreshToken, accessTokenExpiredAt,
						refreshTokenExpiredAt)
				)
			);

		return new TokenInfo(accessToken, refreshToken);
	}
}