package com.voco.voco.app.call.application.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voco.voco.app.call.application.interfaces.LiveKitTokenAdaptor;
import com.voco.voco.app.call.application.usecase.dto.out.LiveKitTokenInfo;
import com.voco.voco.app.call.domain.interfaces.CallCommandRepository;
import com.voco.voco.app.member.domain.interfaces.MemberQueryRepository;
import com.voco.voco.app.member.domain.model.Level;
import com.voco.voco.app.member.domain.model.MemberEntity;
import com.voco.voco.app.scenario.domain.interfaces.ScenarioQueryRepository;
import com.voco.voco.app.scenario.domain.model.BehaviorRulesEntity;
import com.voco.voco.app.scenario.domain.model.ConversationScenarioEntity;
import com.voco.voco.app.scenario.domain.model.LanguageRulesEntity;
import com.voco.voco.app.scenario.domain.model.ScenarioContextEntity;

@ExtendWith(MockitoExtension.class)
class CreateLiveKitTokenUseCaseTest {

	@InjectMocks
	private CreateLiveKitTokenUseCase createLiveKitTokenUseCase;

	@Mock
	private LiveKitTokenAdaptor liveKitTokenAdaptor;

	@Mock
	private MemberQueryRepository memberQueryRepository;

	@Mock
	private ScenarioQueryRepository scenarioQueryRepository;

	@Mock
	private ObjectMapper objectMapper;

	@Mock
	private CallCommandRepository callCommandRepository;

	private static final Long MEMBER_ID = 1L;
	private static final Long SCENARIO_ID = 10L;
	private static final String MOCK_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test";

	@Nested
	@DisplayName("execute 메서드")
	class Execute {

		@Test
		@DisplayName("유효한 회원 ID와 시나리오 ID로 토큰 생성에 성공한다")
		void createToken_Success() throws Exception {
			// given
			MemberEntity member = createMember();
			ConversationScenarioEntity scenario = createScenario();
			given(memberQueryRepository.findByIdOrThrow(MEMBER_ID)).willReturn(member);
			given(scenarioQueryRepository.findByIdOrThrow(SCENARIO_ID)).willReturn(scenario);
			given(objectMapper.writeValueAsString(any())).willReturn("{}");
			given(callCommandRepository.save(any())).willReturn(100L);
			given(liveKitTokenAdaptor.createToken(anyString(), eq(String.valueOf(MEMBER_ID)), eq("홍길동"), anyString()))
				.willReturn(MOCK_TOKEN);

			// when
			LiveKitTokenInfo result = createLiveKitTokenUseCase.execute(MEMBER_ID, SCENARIO_ID);

			// then
			assertThat(result.token()).isEqualTo(MOCK_TOKEN);
			assertThat(result.roomName()).startsWith("room-1-10-beginner-");
			assertThat(result.callId()).isEqualTo(100L);
		}

		@Test
		@DisplayName("룸 이름에 멤버 ID, 시나리오 ID, 레벨이 포함된다")
		void createToken_RoomNameContainsInfo() throws Exception {
			// given
			MemberEntity member = createMember();
			ConversationScenarioEntity scenario = createScenario();
			given(memberQueryRepository.findByIdOrThrow(MEMBER_ID)).willReturn(member);
			given(scenarioQueryRepository.findByIdOrThrow(SCENARIO_ID)).willReturn(scenario);
			given(objectMapper.writeValueAsString(any())).willReturn("{}");
			given(callCommandRepository.save(any())).willReturn(100L);
			given(liveKitTokenAdaptor.createToken(anyString(), anyString(), anyString(), anyString()))
				.willReturn(MOCK_TOKEN);

			// when
			LiveKitTokenInfo result = createLiveKitTokenUseCase.execute(MEMBER_ID, SCENARIO_ID);

			// then
			assertThat(result.roomName()).contains(String.valueOf(MEMBER_ID));
			assertThat(result.roomName()).contains(String.valueOf(SCENARIO_ID));
			assertThat(result.roomName()).contains("beginner");
		}

		@Test
		@DisplayName("참가자 정보와 메타데이터가 올바르게 전달된다")
		void createToken_ParticipantInfoAndMetadataCorrect() throws Exception {
			// given
			MemberEntity member = createMember();
			ConversationScenarioEntity scenario = createScenario();
			given(memberQueryRepository.findByIdOrThrow(MEMBER_ID)).willReturn(member);
			given(scenarioQueryRepository.findByIdOrThrow(SCENARIO_ID)).willReturn(scenario);
			given(objectMapper.writeValueAsString(any())).willReturn("{\"scenarioId\":10}");
			given(callCommandRepository.save(any())).willReturn(100L);
			given(liveKitTokenAdaptor.createToken(anyString(), anyString(), anyString(), anyString()))
				.willReturn(MOCK_TOKEN);

			// when
			createLiveKitTokenUseCase.execute(MEMBER_ID, SCENARIO_ID);

			// then
			then(liveKitTokenAdaptor).should().createToken(
				argThat(roomName -> roomName.startsWith("room-1-10-beginner-")),
				eq(String.valueOf(MEMBER_ID)),
				eq("홍길동"),
				eq("{\"scenarioId\":10}")
			);
		}

		@Test
		@DisplayName("INTERMEDIATE 레벨 시나리오의 룸 이름에 intermediate가 포함된다")
		void createToken_IntermediateLevel_RoomNameCorrect() throws Exception {
			// given
			MemberEntity member = createMember();
			ConversationScenarioEntity scenario = createScenarioWithLevel(
				com.voco.voco.app.scenario.domain.model.Level.INTERMEDIATE);
			given(memberQueryRepository.findByIdOrThrow(MEMBER_ID)).willReturn(member);
			given(scenarioQueryRepository.findByIdOrThrow(SCENARIO_ID)).willReturn(scenario);
			given(objectMapper.writeValueAsString(any())).willReturn("{}");
			given(callCommandRepository.save(any())).willReturn(100L);
			given(liveKitTokenAdaptor.createToken(anyString(), anyString(), anyString(), anyString()))
				.willReturn(MOCK_TOKEN);

			// when
			LiveKitTokenInfo result = createLiveKitTokenUseCase.execute(MEMBER_ID, SCENARIO_ID);

			// then
			assertThat(result.roomName()).contains("intermediate");
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
		setId(member, MEMBER_ID);
		return member;
	}

	private ConversationScenarioEntity createScenario() {
		return createScenarioWithLevel(com.voco.voco.app.scenario.domain.model.Level.BEGINNER);
	}

	private ConversationScenarioEntity createScenarioWithLevel(com.voco.voco.app.scenario.domain.model.Level level) {
		ConversationScenarioEntity scenario = ConversationScenarioEntity.create(
			"Cafe Order",
			level,
			"a cafe staff member",
			"a customer",
			"When all required information is collected",
			List.of("Confirm the order")
		);

		ScenarioContextEntity context = ScenarioContextEntity.create(
			"The user is visiting a cafe.",
			List.of("Friendly")
		);
		scenario.addScenarioContext(context);

		LanguageRulesEntity languageRules = LanguageRulesEntity.create(
			List.of("Use simple vocabulary"),
			List.of("Use short sentences"),
			List.of("Use simple English")
		);
		scenario.addLanguageRules(languageRules);

		BehaviorRulesEntity behaviorRules = BehaviorRulesEntity.create(
			List.of("NEVER break character.")
		);
		scenario.addBehaviorRules(behaviorRules);

		setId(scenario, SCENARIO_ID);
		return scenario;
	}

	private void setId(Object entity, Long id) {
		try {
			java.lang.reflect.Field idField = entity.getClass().getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(entity, id);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
