package com.voco.voco.app.auth.application.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.voco.voco.app.auth.application.usecase.dto.in.RefreshTokenUseCaseDto;
import com.voco.voco.app.auth.application.usecase.dto.out.TokenInfo;
import com.voco.voco.app.auth.domain.interfaces.TokenQueryRepository;
import com.voco.voco.app.auth.domain.model.TokenEntity;
import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;
import com.voco.voco.common.interfaces.JwtAdaptor;

@ExtendWith(MockitoExtension.class)
class RefreshTokenUseCaseTest {

	@InjectMocks
	private RefreshTokenUseCase refreshTokenUseCase;

	@Mock
	private TokenQueryRepository tokenQueryRepository;

	@Mock
	private JwtAdaptor jwtAdaptor;

	private static final Long MEMBER_ID = 1L;
	private static final String OLD_ACCESS_TOKEN = "oldAccessToken";
	private static final String OLD_REFRESH_TOKEN = "oldRefreshToken";
	private static final String NEW_ACCESS_TOKEN = "newAccessToken";
	private static final String NEW_REFRESH_TOKEN = "newRefreshToken";

	@Nested
	@DisplayName("execute 메서드")
	class Execute {

		@Test
		@DisplayName("유효한 토큰으로 갱신에 성공한다")
		void refresh_Success() {
			// given
			RefreshTokenUseCaseDto dto = new RefreshTokenUseCaseDto(OLD_ACCESS_TOKEN, OLD_REFRESH_TOKEN);
			TokenEntity token = mock(TokenEntity.class);

			given(jwtAdaptor.extractMemberIdIgnoreExpiration(dto.accessToken())).willReturn(MEMBER_ID);
			given(jwtAdaptor.extractMemberId(dto.refreshToken())).willReturn(MEMBER_ID);
			given(tokenQueryRepository.findByMemberId(MEMBER_ID)).willReturn(Optional.of(token));
			given(token.getRefreshToken()).willReturn(OLD_REFRESH_TOKEN);
			given(token.isRefreshTokenExpired()).willReturn(false);
			given(jwtAdaptor.createAccessToken(eq(MEMBER_ID), anyLong())).willReturn(NEW_ACCESS_TOKEN);
			given(jwtAdaptor.createRefreshToken(eq(MEMBER_ID), anyLong())).willReturn(NEW_REFRESH_TOKEN);

			// when
			TokenInfo result = refreshTokenUseCase.execute(dto);

			// then
			assertThat(result.accessToken()).isEqualTo(NEW_ACCESS_TOKEN);
			assertThat(result.refreshToken()).isEqualTo(NEW_REFRESH_TOKEN);
			then(token).should().updateTokens(eq(NEW_ACCESS_TOKEN), eq(NEW_REFRESH_TOKEN), any(), any());
		}

		@Test
		@DisplayName("access token과 refresh token의 memberId가 다르면 예외가 발생한다")
		void refresh_MemberIdMismatch_ThrowsException() {
			// given
			RefreshTokenUseCaseDto dto = new RefreshTokenUseCaseDto(OLD_ACCESS_TOKEN, OLD_REFRESH_TOKEN);

			given(jwtAdaptor.extractMemberIdIgnoreExpiration(dto.accessToken())).willReturn(MEMBER_ID);
			given(jwtAdaptor.extractMemberId(dto.refreshToken())).willReturn(2L);

			// when & then
			assertThatThrownBy(() -> refreshTokenUseCase.execute(dto))
				.isInstanceOf(CoreException.class)
				.satisfies(exception -> {
					CoreException coreException = (CoreException)exception;
					assertThat(coreException.getErrorType()).isEqualTo(ApiErrorType.INVALID_TOKEN);
				});

			then(tokenQueryRepository).shouldHaveNoInteractions();
		}

		@Test
		@DisplayName("DB에 토큰이 존재하지 않으면 예외가 발생한다")
		void refresh_TokenNotFound_ThrowsException() {
			// given
			RefreshTokenUseCaseDto dto = new RefreshTokenUseCaseDto(OLD_ACCESS_TOKEN, OLD_REFRESH_TOKEN);

			given(jwtAdaptor.extractMemberIdIgnoreExpiration(dto.accessToken())).willReturn(MEMBER_ID);
			given(jwtAdaptor.extractMemberId(dto.refreshToken())).willReturn(MEMBER_ID);
			given(tokenQueryRepository.findByMemberId(MEMBER_ID)).willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> refreshTokenUseCase.execute(dto))
				.isInstanceOf(CoreException.class)
				.satisfies(exception -> {
					CoreException coreException = (CoreException)exception;
					assertThat(coreException.getErrorType()).isEqualTo(ApiErrorType.TOKEN_NOT_FOUND);
				});
		}

		@Test
		@DisplayName("DB의 refresh token과 요청의 refresh token이 다르면 예외가 발생한다")
		void refresh_RefreshTokenMismatch_ThrowsException() {
			// given
			RefreshTokenUseCaseDto dto = new RefreshTokenUseCaseDto(OLD_ACCESS_TOKEN, OLD_REFRESH_TOKEN);
			TokenEntity token = mock(TokenEntity.class);

			given(jwtAdaptor.extractMemberIdIgnoreExpiration(dto.accessToken())).willReturn(MEMBER_ID);
			given(jwtAdaptor.extractMemberId(dto.refreshToken())).willReturn(MEMBER_ID);
			given(tokenQueryRepository.findByMemberId(MEMBER_ID)).willReturn(Optional.of(token));
			given(token.getRefreshToken()).willReturn("differentRefreshToken");

			// when & then
			assertThatThrownBy(() -> refreshTokenUseCase.execute(dto))
				.isInstanceOf(CoreException.class)
				.satisfies(exception -> {
					CoreException coreException = (CoreException)exception;
					assertThat(coreException.getErrorType()).isEqualTo(ApiErrorType.INVALID_TOKEN);
				});
		}

		@Test
		@DisplayName("refresh token이 만료되면 예외가 발생한다")
		void refresh_RefreshTokenExpired_ThrowsException() {
			// given
			RefreshTokenUseCaseDto dto = new RefreshTokenUseCaseDto(OLD_ACCESS_TOKEN, OLD_REFRESH_TOKEN);
			TokenEntity token = mock(TokenEntity.class);

			given(jwtAdaptor.extractMemberIdIgnoreExpiration(dto.accessToken())).willReturn(MEMBER_ID);
			given(jwtAdaptor.extractMemberId(dto.refreshToken())).willReturn(MEMBER_ID);
			given(tokenQueryRepository.findByMemberId(MEMBER_ID)).willReturn(Optional.of(token));
			given(token.getRefreshToken()).willReturn(OLD_REFRESH_TOKEN);
			given(token.isRefreshTokenExpired()).willReturn(true);

			// when & then
			assertThatThrownBy(() -> refreshTokenUseCase.execute(dto))
				.isInstanceOf(CoreException.class)
				.satisfies(exception -> {
					CoreException coreException = (CoreException)exception;
					assertThat(coreException.getErrorType()).isEqualTo(ApiErrorType.TOKEN_EXPIRED);
				});
		}
	}
}