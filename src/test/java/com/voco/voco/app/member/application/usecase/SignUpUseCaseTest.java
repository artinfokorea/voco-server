package com.voco.voco.app.member.application.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import com.voco.voco.app.member.application.usecase.dto.in.SignUpUseCaseDto;
import com.voco.voco.app.member.domain.interfaces.MemberCommandRepository;
import com.voco.voco.app.member.domain.interfaces.MemberQueryRepository;
import com.voco.voco.app.member.domain.model.Category;
import com.voco.voco.app.member.domain.model.Level;
import com.voco.voco.app.member.domain.model.MemberEntity;
import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;
import com.voco.voco.common.interfaces.PasswordAdaptor;

@ExtendWith(MockitoExtension.class)
class SignUpUseCaseTest {

	@InjectMocks
	private SignUpUseCase signUpUseCase;

	@Mock
	private MemberQueryRepository memberQueryRepository;

	@Mock
	private MemberCommandRepository memberCommandRepository;

	@Mock
	private PasswordAdaptor passwordAdaptor;

	private static final String VALID_PASSWORD = "Password1!";
	private static final String ENCODED_PASSWORD = "encodedPassword";

	@Nested
	@DisplayName("execute 메서드")
	class Execute {

		@Test
		@DisplayName("유효한 입력으로 회원가입에 성공한다")
		void signUp_Success() {
			// given
			SignUpUseCaseDto dto = new SignUpUseCaseDto(
				"홍길동",
				"Hong Gildong",
				"test@example.com",
				VALID_PASSWORD,
				Level.BEGINNER,
				Set.of(Category.DAILY, Category.BUSINESS)
			);
			Long expectedMemberId = 1L;

			given(memberQueryRepository.existsByEmail(dto.email())).willReturn(false);
			given(passwordAdaptor.encode(dto.password())).willReturn(ENCODED_PASSWORD);
			given(memberCommandRepository.save(any(MemberEntity.class))).willReturn(expectedMemberId);

			// when
			Long result = signUpUseCase.execute(dto);

			// then
			assertThat(result).isEqualTo(expectedMemberId);
			then(memberQueryRepository).should().existsByEmail(dto.email());
			then(passwordAdaptor).should().encode(dto.password());
			then(memberCommandRepository).should().save(any(MemberEntity.class));
		}

		@Test
		@DisplayName("이미 존재하는 이메일로 회원가입 시 예외가 발생한다")
		void signUp_DuplicateEmail_ThrowsException() {
			// given
			SignUpUseCaseDto dto = new SignUpUseCaseDto(
				"홍길동",
				"Hong Gildong",
				"duplicate@example.com",
				VALID_PASSWORD,
				Level.BEGINNER,
				Set.of(Category.DAILY)
			);

			given(memberQueryRepository.existsByEmail(dto.email())).willReturn(true);

			// when & then
			assertThatThrownBy(() -> signUpUseCase.execute(dto))
				.isInstanceOf(CoreException.class)
				.satisfies(exception -> {
					CoreException coreException = (CoreException) exception;
					assertThat(coreException.getErrorType()).isEqualTo(ApiErrorType.DUPLICATED_EMAIL);
				});

			then(memberCommandRepository).shouldHaveNoInteractions();
			then(passwordAdaptor).shouldHaveNoInteractions();
		}
	}

	@Nested
	@DisplayName("비밀번호 검증")
	class PasswordValidation {

		@ParameterizedTest
		@DisplayName("유효하지 않은 비밀번호로 회원가입 시 예외가 발생한다")
		@ValueSource(strings = {
			"short1!",           // 8자 미만
			"Password!",         // 숫자 없음
			"Password1",         // 특수문자 없음
			"12345678",          // 영문자, 특수문자 없음
			"!@#$%^&*",          // 영문자, 숫자 없음
			""                   // 빈 문자열
		})
		void signUp_InvalidPassword_ThrowsException(String invalidPassword) {
			// given
			SignUpUseCaseDto dto = new SignUpUseCaseDto(
				"홍길동",
				"Hong Gildong",
				"test@example.com",
				invalidPassword,
				Level.BEGINNER,
				Set.of(Category.DAILY)
			);

			given(memberQueryRepository.existsByEmail(dto.email())).willReturn(false);

			// when & then
			assertThatThrownBy(() -> signUpUseCase.execute(dto))
				.isInstanceOf(CoreException.class)
				.satisfies(exception -> {
					CoreException coreException = (CoreException) exception;
					assertThat(coreException.getErrorType()).isEqualTo(ApiErrorType.INVALID_PASSWORD);
				});

			then(memberCommandRepository).shouldHaveNoInteractions();
		}

		@ParameterizedTest
		@DisplayName("유효한 비밀번호로 회원가입에 성공한다")
		@ValueSource(strings = {
			"Password1!",
			"Abcdefg1@",
			"Test1234#",
			"MyP@ssw0rd",
			"Complex1!Password"
		})
		void signUp_ValidPassword_Success(String validPassword) {
			// given
			SignUpUseCaseDto dto = new SignUpUseCaseDto(
				"홍길동",
				"Hong Gildong",
				"test@example.com",
				validPassword,
				Level.BEGINNER,
				Set.of(Category.DAILY)
			);
			Long expectedMemberId = 1L;

			given(memberQueryRepository.existsByEmail(dto.email())).willReturn(false);
			given(passwordAdaptor.encode(dto.password())).willReturn(ENCODED_PASSWORD);
			given(memberCommandRepository.save(any(MemberEntity.class))).willReturn(expectedMemberId);

			// when
			Long result = signUpUseCase.execute(dto);

			// then
			assertThat(result).isEqualTo(expectedMemberId);
		}
	}
}