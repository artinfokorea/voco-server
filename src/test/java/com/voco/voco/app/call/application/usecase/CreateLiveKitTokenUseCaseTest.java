package com.voco.voco.app.call.application.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.voco.voco.app.call.application.interfaces.LiveKitTokenAdaptor;
import com.voco.voco.app.call.application.usecase.dto.out.LiveKitTokenInfo;
import com.voco.voco.app.member.domain.interfaces.MemberQueryRepository;
import com.voco.voco.app.member.domain.model.Level;
import com.voco.voco.app.member.domain.model.MemberEntity;

@ExtendWith(MockitoExtension.class)
class CreateLiveKitTokenUseCaseTest {

	@InjectMocks
	private CreateLiveKitTokenUseCase createLiveKitTokenUseCase;

	@Mock
	private LiveKitTokenAdaptor liveKitTokenAdaptor;

	@Mock
	private MemberQueryRepository memberQueryRepository;

	private static final Long MEMBER_ID = 1L;
	private static final String MOCK_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test";

	@Nested
	@DisplayName("execute 메서드")
	class Execute {

		@Test
		@DisplayName("유효한 회원 ID로 토큰 생성에 성공한다")
		void createToken_Success() {
			// given
			MemberEntity member = createMember();
			given(memberQueryRepository.findByIdOrThrow(MEMBER_ID)).willReturn(member);
			given(liveKitTokenAdaptor.createToken(anyString(), eq(String.valueOf(MEMBER_ID)), eq("홍길동")))
				.willReturn(MOCK_TOKEN);

			// when
			LiveKitTokenInfo result = createLiveKitTokenUseCase.execute(MEMBER_ID);

			// then
			assertThat(result.token()).isEqualTo(MOCK_TOKEN);
			assertThat(result.roomName()).startsWith("room-1-beginner-");
			assertThat(result.roomName()).hasSize("room-1-beginner-".length() + 8);
		}

		@Test
		@DisplayName("룸 이름에 멤버 ID와 레벨이 포함된다")
		void createToken_RoomNameContainsMemberInfo() {
			// given
			MemberEntity member = createMember();
			given(memberQueryRepository.findByIdOrThrow(MEMBER_ID)).willReturn(member);
			given(liveKitTokenAdaptor.createToken(anyString(), anyString(), anyString()))
				.willReturn(MOCK_TOKEN);

			// when
			LiveKitTokenInfo result = createLiveKitTokenUseCase.execute(MEMBER_ID);

			// then
			assertThat(result.roomName()).contains(String.valueOf(MEMBER_ID));
			assertThat(result.roomName()).contains("beginner");
		}

		@Test
		@DisplayName("참가자 정보가 올바르게 전달된다")
		void createToken_ParticipantInfoCorrect() {
			// given
			MemberEntity member = createMember();
			given(memberQueryRepository.findByIdOrThrow(MEMBER_ID)).willReturn(member);
			given(liveKitTokenAdaptor.createToken(anyString(), anyString(), anyString()))
				.willReturn(MOCK_TOKEN);

			// when
			createLiveKitTokenUseCase.execute(MEMBER_ID);

			// then
			then(liveKitTokenAdaptor).should().createToken(
				argThat(roomName -> roomName.startsWith("room-1-beginner-")),
				eq(String.valueOf(MEMBER_ID)),
				eq("홍길동")
			);
		}

		@Test
		@DisplayName("INTERMEDIATE 레벨 회원의 룸 이름에 intermediate가 포함된다")
		void createToken_IntermediateLevel_RoomNameCorrect() {
			// given
			MemberEntity member = createMemberWithLevel(Level.INTERMEDIATE);
			given(memberQueryRepository.findByIdOrThrow(MEMBER_ID)).willReturn(member);
			given(liveKitTokenAdaptor.createToken(anyString(), anyString(), anyString()))
				.willReturn(MOCK_TOKEN);

			// when
			LiveKitTokenInfo result = createLiveKitTokenUseCase.execute(MEMBER_ID);

			// then
			assertThat(result.roomName()).contains("intermediate");
		}

		@Test
		@DisplayName("ADVANCED 레벨 회원의 룸 이름에 advanced가 포함된다")
		void createToken_AdvancedLevel_RoomNameCorrect() {
			// given
			MemberEntity member = createMemberWithLevel(Level.ADVANCED);
			given(memberQueryRepository.findByIdOrThrow(MEMBER_ID)).willReturn(member);
			given(liveKitTokenAdaptor.createToken(anyString(), anyString(), anyString()))
				.willReturn(MOCK_TOKEN);

			// when
			LiveKitTokenInfo result = createLiveKitTokenUseCase.execute(MEMBER_ID);

			// then
			assertThat(result.roomName()).contains("advanced");
		}
	}

	private MemberEntity createMember() {
		return createMemberWithLevel(Level.BEGINNER);
	}

	private MemberEntity createMemberWithLevel(Level level) {
		MemberEntity member = MemberEntity.create(
			"홍길동",
			"Hong Gildong",
			"test@example.com",
			"encodedPassword",
			level
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
