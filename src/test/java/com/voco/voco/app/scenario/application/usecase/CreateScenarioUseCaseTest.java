package com.voco.voco.app.scenario.application.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.voco.voco.app.scenario.application.usecase.dto.in.CreateScenarioUseCaseDto;
import com.voco.voco.app.scenario.domain.interfaces.ScenarioCommandRepository;
import com.voco.voco.app.scenario.domain.model.ConversationScenarioEntity;
import com.voco.voco.app.scenario.domain.model.Level;

@ExtendWith(MockitoExtension.class)
class CreateScenarioUseCaseTest {

	@InjectMocks
	private CreateScenarioUseCase createScenarioUseCase;

	@Mock
	private ScenarioCommandRepository scenarioCommandRepository;

	private static final Long SCENARIO_ID = 1L;

	private CreateScenarioUseCaseDto createValidDto() {
		return new CreateScenarioUseCaseDto(
			"Cafe Order",
			Level.BEGINNER,
			"a cafe staff member",
			"카페 직원",
			"a customer",
			"손님",
			"When all required information is collected",
			List.of("Confirm the order", "Politely end the conversation"),
			new CreateScenarioUseCaseDto.ScenarioContextDto(
				"The user is visiting a cafe for the first time.",
				List.of("Friendly and polite", "Calm and patient")
			),
			new CreateScenarioUseCaseDto.LanguageRulesDto(
				List.of("Use simple vocabulary only"),
				List.of("Use short sentences"),
				List.of("Use simple English only")
			),
			new CreateScenarioUseCaseDto.BehaviorRulesDto(
				List.of("NEVER explain grammar or vocabulary.", "NEVER break character.")
			),
			List.of(
				new CreateScenarioUseCaseDto.ConversationStateDto(1, "Greeting"),
				new CreateScenarioUseCaseDto.ConversationStateDto(2, "Ask for drink"),
				new CreateScenarioUseCaseDto.ConversationStateDto(3, "Confirm order")
			),
			List.of(
				new CreateScenarioUseCaseDto.ConversationSlotDto("drink_type", List.of("coffee", "latte")),
				new CreateScenarioUseCaseDto.ConversationSlotDto("size", List.of("small", "medium", "large"))
			)
		);
	}

	@Nested
	@DisplayName("execute 메서드")
	class Execute {

		@Test
		@DisplayName("시나리오 생성에 성공한다")
		void createScenario_Success() {
			// given
			CreateScenarioUseCaseDto dto = createValidDto();
			given(scenarioCommandRepository.save(any(ConversationScenarioEntity.class))).willReturn(SCENARIO_ID);

			// when
			Long result = createScenarioUseCase.execute(dto);

			// then
			assertThat(result).isEqualTo(SCENARIO_ID);
			then(scenarioCommandRepository).should().save(any(ConversationScenarioEntity.class));
		}

		@Test
		@DisplayName("시나리오 엔티티가 올바르게 생성된다")
		void createScenario_EntityCreatedCorrectly() {
			// given
			CreateScenarioUseCaseDto dto = createValidDto();
			ArgumentCaptor<ConversationScenarioEntity> captor = ArgumentCaptor.forClass(ConversationScenarioEntity.class);
			given(scenarioCommandRepository.save(captor.capture())).willReturn(SCENARIO_ID);

			// when
			createScenarioUseCase.execute(dto);

			// then
			ConversationScenarioEntity savedEntity = captor.getValue();
			assertThat(savedEntity.getName()).isEqualTo("Cafe Order");
			assertThat(savedEntity.getLevel()).isEqualTo(Level.BEGINNER);
			assertThat(savedEntity.getAiRoleEn()).isEqualTo("a cafe staff member");
			assertThat(savedEntity.getAiRoleKo()).isEqualTo("카페 직원");
			assertThat(savedEntity.getUserRoleEn()).isEqualTo("a customer");
			assertThat(savedEntity.getUserRoleKo()).isEqualTo("손님");
			assertThat(savedEntity.getCompletionRule()).isEqualTo("When all required information is collected");
			assertThat(savedEntity.getCompletionRuleDetail()).containsExactly("Confirm the order", "Politely end the conversation");
		}

		@Test
		@DisplayName("시나리오 컨텍스트가 올바르게 연결된다")
		void createScenario_ScenarioContextLinkedCorrectly() {
			// given
			CreateScenarioUseCaseDto dto = createValidDto();
			ArgumentCaptor<ConversationScenarioEntity> captor = ArgumentCaptor.forClass(ConversationScenarioEntity.class);
			given(scenarioCommandRepository.save(captor.capture())).willReturn(SCENARIO_ID);

			// when
			createScenarioUseCase.execute(dto);

			// then
			ConversationScenarioEntity savedEntity = captor.getValue();
			assertThat(savedEntity.getScenarioContext()).isNotNull();
			assertThat(savedEntity.getScenarioContext().getContext()).isEqualTo("The user is visiting a cafe for the first time.");
			assertThat(savedEntity.getScenarioContext().getPersonality()).containsExactly("Friendly and polite", "Calm and patient");
		}

		@Test
		@DisplayName("언어 규칙이 올바르게 연결된다")
		void createScenario_LanguageRulesLinkedCorrectly() {
			// given
			CreateScenarioUseCaseDto dto = createValidDto();
			ArgumentCaptor<ConversationScenarioEntity> captor = ArgumentCaptor.forClass(ConversationScenarioEntity.class);
			given(scenarioCommandRepository.save(captor.capture())).willReturn(SCENARIO_ID);

			// when
			createScenarioUseCase.execute(dto);

			// then
			ConversationScenarioEntity savedEntity = captor.getValue();
			assertThat(savedEntity.getLanguageRules()).isNotNull();
			assertThat(savedEntity.getLanguageRules().getVocabularyRules()).containsExactly("Use simple vocabulary only");
			assertThat(savedEntity.getLanguageRules().getSentenceRules()).containsExactly("Use short sentences");
			assertThat(savedEntity.getLanguageRules().getOutputConstraints()).containsExactly("Use simple English only");
		}

		@Test
		@DisplayName("행동 규칙이 올바르게 연결된다")
		void createScenario_BehaviorRulesLinkedCorrectly() {
			// given
			CreateScenarioUseCaseDto dto = createValidDto();
			ArgumentCaptor<ConversationScenarioEntity> captor = ArgumentCaptor.forClass(ConversationScenarioEntity.class);
			given(scenarioCommandRepository.save(captor.capture())).willReturn(SCENARIO_ID);

			// when
			createScenarioUseCase.execute(dto);

			// then
			ConversationScenarioEntity savedEntity = captor.getValue();
			assertThat(savedEntity.getBehaviorRules()).isNotNull();
			assertThat(savedEntity.getBehaviorRules().getRules()).containsExactly(
				"NEVER explain grammar or vocabulary.",
				"NEVER break character."
			);
		}

		@Test
		@DisplayName("대화 상태가 올바르게 연결된다")
		void createScenario_ConversationStatesLinkedCorrectly() {
			// given
			CreateScenarioUseCaseDto dto = createValidDto();
			ArgumentCaptor<ConversationScenarioEntity> captor = ArgumentCaptor.forClass(ConversationScenarioEntity.class);
			given(scenarioCommandRepository.save(captor.capture())).willReturn(SCENARIO_ID);

			// when
			createScenarioUseCase.execute(dto);

			// then
			ConversationScenarioEntity savedEntity = captor.getValue();
			assertThat(savedEntity.getConversationStates()).hasSize(3);
			assertThat(savedEntity.getConversationStates().get(0).getStateOrder()).isEqualTo(1);
			assertThat(savedEntity.getConversationStates().get(0).getStateName()).isEqualTo("Greeting");
			assertThat(savedEntity.getConversationStates().get(1).getStateOrder()).isEqualTo(2);
			assertThat(savedEntity.getConversationStates().get(1).getStateName()).isEqualTo("Ask for drink");
			assertThat(savedEntity.getConversationStates().get(2).getStateOrder()).isEqualTo(3);
			assertThat(savedEntity.getConversationStates().get(2).getStateName()).isEqualTo("Confirm order");
		}

		@Test
		@DisplayName("대화 슬롯이 올바르게 연결된다")
		void createScenario_ConversationSlotsLinkedCorrectly() {
			// given
			CreateScenarioUseCaseDto dto = createValidDto();
			ArgumentCaptor<ConversationScenarioEntity> captor = ArgumentCaptor.forClass(ConversationScenarioEntity.class);
			given(scenarioCommandRepository.save(captor.capture())).willReturn(SCENARIO_ID);

			// when
			createScenarioUseCase.execute(dto);

			// then
			ConversationScenarioEntity savedEntity = captor.getValue();
			assertThat(savedEntity.getConversationSlots()).hasSize(2);
			assertThat(savedEntity.getConversationSlots().get(0).getSlotKey()).isEqualTo("drink_type");
			assertThat(savedEntity.getConversationSlots().get(0).getAllowedValues()).containsExactly("coffee", "latte");
			assertThat(savedEntity.getConversationSlots().get(1).getSlotKey()).isEqualTo("size");
			assertThat(savedEntity.getConversationSlots().get(1).getAllowedValues()).containsExactly("small", "medium", "large");
		}

		@Test
		@DisplayName("conversationStates가 null이면 빈 리스트로 처리된다")
		void createScenario_NullConversationStates_Success() {
			// given
			CreateScenarioUseCaseDto dto = new CreateScenarioUseCaseDto(
				"Cafe Order",
				Level.BEGINNER,
				"a cafe staff member",
				"카페 직원",
				"a customer",
				"손님",
				"When all required information is collected",
				null,
				new CreateScenarioUseCaseDto.ScenarioContextDto("Context", List.of("Friendly")),
				new CreateScenarioUseCaseDto.LanguageRulesDto(
					List.of("Rule1"), List.of("Rule2"), List.of("Rule3")
				),
				new CreateScenarioUseCaseDto.BehaviorRulesDto(List.of("Rule")),
				null,
				null
			);
			ArgumentCaptor<ConversationScenarioEntity> captor = ArgumentCaptor.forClass(ConversationScenarioEntity.class);
			given(scenarioCommandRepository.save(captor.capture())).willReturn(SCENARIO_ID);

			// when
			Long result = createScenarioUseCase.execute(dto);

			// then
			assertThat(result).isEqualTo(SCENARIO_ID);
			ConversationScenarioEntity savedEntity = captor.getValue();
			assertThat(savedEntity.getConversationStates()).isEmpty();
			assertThat(savedEntity.getConversationSlots()).isEmpty();
		}

		@Test
		@DisplayName("completionRuleDetail이 null이어도 생성에 성공한다")
		void createScenario_NullCompletionRuleDetail_Success() {
			// given
			CreateScenarioUseCaseDto dto = new CreateScenarioUseCaseDto(
				"Cafe Order",
				Level.INTERMEDIATE,
				"a cafe staff member",
				"카페 직원",
				"a customer",
				"손님",
				"When all required information is collected",
				null,
				new CreateScenarioUseCaseDto.ScenarioContextDto("Context", List.of("Friendly")),
				new CreateScenarioUseCaseDto.LanguageRulesDto(
					List.of("Rule1"), List.of("Rule2"), List.of("Rule3")
				),
				new CreateScenarioUseCaseDto.BehaviorRulesDto(List.of("Rule")),
				null,
				null
			);
			given(scenarioCommandRepository.save(any(ConversationScenarioEntity.class))).willReturn(SCENARIO_ID);

			// when
			Long result = createScenarioUseCase.execute(dto);

			// then
			assertThat(result).isEqualTo(SCENARIO_ID);
		}
	}
}
