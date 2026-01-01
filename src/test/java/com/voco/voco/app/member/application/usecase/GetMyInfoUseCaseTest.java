package com.voco.voco.app.member.application.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.voco.voco.app.member.application.usecase.dto.out.MyInfo;
import com.voco.voco.app.member.domain.interfaces.MemberQueryRepository;
import com.voco.voco.app.member.domain.model.Level;
import com.voco.voco.app.member.domain.model.MemberEntity;
import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;

@ExtendWith(MockitoExtension.class)
class GetMyInfoUseCaseTest {

	@InjectMocks
	private GetMyInfoUseCase getMyInfoUseCase;

	@Mock
	private MemberQueryRepository memberQueryRepository;

	private static final Long MEMBER_ID = 1L;

	@Test
	@DisplayName("내 정보 조회에 성공한다")
	void getMyInfo_Success() {
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
		MyInfo result = getMyInfoUseCase.execute(MEMBER_ID);

		// then
		assertThat(result.koreanName()).isEqualTo("홍길동");
		assertThat(result.englishName()).isEqualTo("Gildong");
		assertThat(result.email()).isEqualTo("test@example.com");
		assertThat(result.level()).isEqualTo(Level.BEGINNER);
	}

	@Test
	@DisplayName("존재하지 않는 회원 조회 시 예외가 발생한다")
	void getMyInfo_NotFound_ThrowsException() {
		// given
		given(memberQueryRepository.findByIdOrThrow(999L))
			.willThrow(new CoreException(ApiErrorType.MEMBER_NOT_FOUND));

		// when & then
		assertThatThrownBy(() -> getMyInfoUseCase.execute(999L))
			.isInstanceOf(CoreException.class)
			.hasFieldOrPropertyWithValue("errorType", ApiErrorType.MEMBER_NOT_FOUND);
	}
}
