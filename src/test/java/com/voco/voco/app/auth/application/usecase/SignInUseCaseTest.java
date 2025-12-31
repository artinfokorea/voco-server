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

import com.voco.voco.app.auth.application.usecase.dto.in.SignInUseCaseDto;
import com.voco.voco.app.auth.application.usecase.dto.out.TokenInfo;
import com.voco.voco.app.auth.domain.interfaces.TokenCommandRepository;
import com.voco.voco.app.auth.domain.interfaces.TokenQueryRepository;
import com.voco.voco.app.auth.domain.model.TokenEntity;
import com.voco.voco.app.member.domain.interfaces.MemberQueryRepository;
import com.voco.voco.app.member.domain.model.Level;
import com.voco.voco.app.member.domain.model.MemberEntity;
import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;
import com.voco.voco.common.interfaces.JwtAdaptor;
import com.voco.voco.common.interfaces.PasswordAdaptor;

@ExtendWith(MockitoExtension.class)
class SignInUseCaseTest {

	@InjectMocks
	private SignInUseCase signInUseCase;

	@Mock
	private MemberQueryRepository memberQueryRepository;

	@Mock
	private TokenQueryRepository tokenQueryRepository;

	@Mock
	private TokenCommandRepository tokenCommandRepository;

	@Mock
	private JwtAdaptor jwtAdaptor;

	@Mock
	private PasswordAdaptor passwordAdaptor;

	private static final String TEST_EMAIL = "test@example.com";
	private static final String TEST_PASSWORD = "Password1!";
	private static final String ENCODED_PASSWORD = "encodedPassword";
	private static final String ACCESS_TOKEN = "accessToken";
	private static final String REFRESH_TOKEN = "refreshToken";
	private static final Long MEMBER_ID = 1L;

	@Nested
	@DisplayName("execute 메서드")
	class Execute {

		@Test
		@DisplayName("유효한 입력으로 로그인에 성공한다 - 기존 토큰이 없는 경우")
		void signIn_Success_NewToken() {
			// given
			SignInUseCaseDto dto = new SignInUseCaseDto(TEST_EMAIL, TEST_PASSWORD);
			MemberEntity member = createMember();

			given(memberQueryRepository.findByEmail(dto.email())).willReturn(Optional.of(member));
			given(passwordAdaptor.matches(dto.password(), member.getPassword())).willReturn(true);
			given(jwtAdaptor.createAccessToken(eq(member.getId()), anyLong())).willReturn(ACCESS_TOKEN);
			given(jwtAdaptor.createRefreshToken(eq(member.getId()), anyLong())).willReturn(REFRESH_TOKEN);
			given(tokenQueryRepository.findByMemberId(member.getId())).willReturn(Optional.empty());

			// when
			TokenInfo result = signInUseCase.execute(dto);

			// then
			assertThat(result.accessToken()).isEqualTo(ACCESS_TOKEN);
			assertThat(result.refreshToken()).isEqualTo(REFRESH_TOKEN);
			then(tokenCommandRepository).should().save(any(TokenEntity.class));
		}

		@Test
		@DisplayName("유효한 입력으로 로그인에 성공한다 - 기존 토큰이 있는 경우")
		void signIn_Success_UpdateToken() {
			// given
			SignInUseCaseDto dto = new SignInUseCaseDto(TEST_EMAIL, TEST_PASSWORD);
			MemberEntity member = createMember();
			TokenEntity existingToken = mock(TokenEntity.class);

			given(memberQueryRepository.findByEmail(dto.email())).willReturn(Optional.of(member));
			given(passwordAdaptor.matches(dto.password(), member.getPassword())).willReturn(true);
			given(jwtAdaptor.createAccessToken(eq(member.getId()), anyLong())).willReturn(ACCESS_TOKEN);
			given(jwtAdaptor.createRefreshToken(eq(member.getId()), anyLong())).willReturn(REFRESH_TOKEN);
			given(tokenQueryRepository.findByMemberId(member.getId())).willReturn(Optional.of(existingToken));

			// when
			TokenInfo result = signInUseCase.execute(dto);

			// then
			assertThat(result.accessToken()).isEqualTo(ACCESS_TOKEN);
			assertThat(result.refreshToken()).isEqualTo(REFRESH_TOKEN);
			then(existingToken).should().updateTokens(eq(ACCESS_TOKEN), eq(REFRESH_TOKEN), any(), any());
			then(tokenCommandRepository).shouldHaveNoInteractions();
		}

		@Test
		@DisplayName("존재하지 않는 이메일로 로그인 시 예외가 발생한다")
		void signIn_MemberNotFound_ThrowsException() {
			// given
			SignInUseCaseDto dto = new SignInUseCaseDto("notfound@example.com", TEST_PASSWORD);

			given(memberQueryRepository.findByEmail(dto.email())).willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> signInUseCase.execute(dto))
				.isInstanceOf(CoreException.class)
				.satisfies(exception -> {
					CoreException coreException = (CoreException)exception;
					assertThat(coreException.getErrorType()).isEqualTo(ApiErrorType.MEMBER_NOT_FOUND);
				});

			then(passwordAdaptor).shouldHaveNoInteractions();
			then(jwtAdaptor).shouldHaveNoInteractions();
			then(tokenQueryRepository).shouldHaveNoInteractions();
			then(tokenCommandRepository).shouldHaveNoInteractions();
		}

		@Test
		@DisplayName("비밀번호가 일치하지 않으면 예외가 발생한다")
		void signIn_InvalidPassword_ThrowsException() {
			// given
			SignInUseCaseDto dto = new SignInUseCaseDto(TEST_EMAIL, "wrongPassword");
			MemberEntity member = createMember();

			given(memberQueryRepository.findByEmail(dto.email())).willReturn(Optional.of(member));
			given(passwordAdaptor.matches(dto.password(), member.getPassword())).willReturn(false);

			// when & then
			assertThatThrownBy(() -> signInUseCase.execute(dto))
				.isInstanceOf(CoreException.class)
				.satisfies(exception -> {
					CoreException coreException = (CoreException)exception;
					assertThat(coreException.getErrorType()).isEqualTo(ApiErrorType.INVALID_PASSWORD_MISMATCH);
				});

			then(jwtAdaptor).shouldHaveNoInteractions();
			then(tokenQueryRepository).shouldHaveNoInteractions();
			then(tokenCommandRepository).shouldHaveNoInteractions();
		}
	}

	private MemberEntity createMember() {
		MemberEntity member = MemberEntity.create("홍길동", "Hong Gildong", TEST_EMAIL, ENCODED_PASSWORD,
			Level.BEGINNER);
		try {
			java.lang.reflect.Field idField = MemberEntity.class.getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(member, MEMBER_ID);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return member;
	}
}
