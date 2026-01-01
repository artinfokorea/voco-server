package com.voco.voco.app.member.application.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.voco.voco.app.member.domain.interfaces.MemberQueryRepository;
import com.voco.voco.app.member.domain.model.Level;
import com.voco.voco.app.member.domain.model.MemberEntity;
import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;

@ExtendWith(MockitoExtension.class)
class DeleteMemberUseCaseTest {

	@InjectMocks
	private DeleteMemberUseCase deleteMemberUseCase;

	@Mock
	private MemberQueryRepository memberQueryRepository;

	private static final Long MEMBER_ID = 1L;

	@Test
	@DisplayName("회원 탈퇴에 성공한다")
	void deleteMember_Success() {
		// given
		MemberEntity member = MemberEntity.create(
			"홍길동",
			"Gildong",
			"test@example.com",
			"password",
			Level.BEGINNER
		);
		given(memberQueryRepository.findByIdOrThrow(MEMBER_ID)).willReturn(member);

		// when
		deleteMemberUseCase.execute(MEMBER_ID);

		// then
		then(memberQueryRepository).should().findByIdOrThrow(MEMBER_ID);
	}

	@Test
	@DisplayName("존재하지 않는 회원 탈퇴 시 예외가 발생한다")
	void deleteMember_NotFound_ThrowsException() {
		// given
		given(memberQueryRepository.findByIdOrThrow(999L))
			.willThrow(new CoreException(ApiErrorType.MEMBER_NOT_FOUND));

		// when & then
		assertThatThrownBy(() -> deleteMemberUseCase.execute(999L))
			.isInstanceOf(CoreException.class)
			.hasFieldOrPropertyWithValue("errorType", ApiErrorType.MEMBER_NOT_FOUND);
	}
}
