package com.voco.voco.app.member.application.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.voco.voco.app.member.application.usecase.dto.in.UpdateMemberUseCaseDto;
import com.voco.voco.app.member.domain.interfaces.MemberQueryRepository;
import com.voco.voco.app.member.domain.model.Level;
import com.voco.voco.app.member.domain.model.MemberEntity;
import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;

@ExtendWith(MockitoExtension.class)
class UpdateMemberUseCaseTest {

	@InjectMocks
	private UpdateMemberUseCase updateMemberUseCase;

	@Mock
	private MemberQueryRepository memberQueryRepository;

	private static final Long MEMBER_ID = 1L;

	@Nested
	@DisplayName("execute 메서드")
	class Execute {

		@Test
		@DisplayName("유효한 입력으로 회원 정보 수정에 성공한다")
		void updateMember_Success() {
			// given
			UpdateMemberUseCaseDto dto = new UpdateMemberUseCaseDto(
				MEMBER_ID,
				"New English Name",
				Level.INTERMEDIATE
			);
			MemberEntity member = createMember();

			given(memberQueryRepository.findById(MEMBER_ID)).willReturn(Optional.of(member));

			// when
			updateMemberUseCase.execute(dto);

			// then
			assertThat(member.getEnglishName()).isEqualTo("New English Name");
			assertThat(member.getLevel()).isEqualTo(Level.INTERMEDIATE);
		}

		@Test
		@DisplayName("존재하지 않는 회원 ID로 수정 시 예외가 발생한다")
		void updateMember_MemberNotFound_ThrowsException() {
			// given
			UpdateMemberUseCaseDto dto = new UpdateMemberUseCaseDto(
				999L,
				"New English Name",
				Level.INTERMEDIATE
			);

			given(memberQueryRepository.findById(999L)).willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> updateMemberUseCase.execute(dto))
				.isInstanceOf(CoreException.class)
				.satisfies(exception -> {
					CoreException coreException = (CoreException) exception;
					assertThat(coreException.getErrorType()).isEqualTo(ApiErrorType.MEMBER_NOT_FOUND);
				});
		}
	}

	private MemberEntity createMember() {
		MemberEntity member = MemberEntity.create(
			"홍길동",
			"Hong Gildong",
			"test@example.com",
			"encodedPassword",
			Level.BEGINNER
		);
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
