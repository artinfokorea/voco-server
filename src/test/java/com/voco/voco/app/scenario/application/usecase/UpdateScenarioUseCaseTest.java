package com.voco.voco.app.scenario.application.usecase;

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

import com.voco.voco.app.scenario.application.usecase.dto.in.UpdateScenarioUseCaseDto;
import com.voco.voco.app.scenario.domain.interfaces.ScenarioQueryRepository;
import com.voco.voco.app.scenario.domain.model.BehaviorRulesEntity;
import com.voco.voco.app.scenario.domain.model.ConversationScenarioEntity;
import com.voco.voco.app.scenario.domain.model.ConversationSlotEntity;
import com.voco.voco.app.scenario.domain.model.ConversationStateEntity;
import com.voco.voco.app.scenario.domain.model.LanguageRulesEntity;
import com.voco.voco.app.scenario.domain.model.Level;
import com.voco.voco.app.scenario.domain.model.ScenarioContextEntity;
import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;

@ExtendWith(MockitoExtension.class)
class UpdateScenarioUseCaseTest {

	@InjectMocks
	private UpdateScenarioUseCase updateScenarioUseCase;

	@Mock
	private ScenarioQueryRepository scenarioQueryRepository;

	private static final Long SCENARIO_ID = 1L;

	@Nested
	@DisplayName("execute 메서드")
	class Execute {

		@Test
		@DisplayName("시나리오를 성공적으로 수정한다")
		void updateScenario_Success() {
			// given
			ConversationScenarioEntity existingScenario = createExistingScenario();
			given(scenarioQueryRepository.findByIdOrThrow(SCENARIO_ID)).willReturn(existingScenario);

			UpdateScenarioUseCaseDto dto = createUpdateDto();

			// when
			updateScenarioUseCase.execute(dto);

			// then
			assertThat(existingScenario.getName()).isEqualTo("Updated Cafe Order");
			assertThat(existingScenario.getLevel()).isEqualTo(Level.INTERMEDIATE);
			assertThat(existingScenario.getAiRoleEn()).isEqualTo("an updated cafe staff member");
			assertThat(existingScenario.getAiRoleKo()).isEqualTo("수정된 카페 직원");
			assertThat(existingScenario.getUserRoleEn()).isEqualTo("an updated customer");
			assertThat(existingScenario.getUserRoleKo()).isEqualTo("수정된 손님");
		}

		@Test
		@DisplayName("시나리오 컨텍스트를 수정한다")
		void updateScenario_UpdatesContext() {
			// given
			ConversationScenarioEntity existingScenario = createExistingScenario();
			given(scenarioQueryRepository.findByIdOrThrow(SCENARIO_ID)).willReturn(existingScenario);

			UpdateScenarioUseCaseDto dto = createUpdateDto();

			// when
			updateScenarioUseCase.execute(dto);

			// then
			assertThat(existingScenario.getScenarioContext()).isNotNull();
			assertThat(existingScenario.getScenarioContext().getContext()).isEqualTo("Updated context");
		}

		@Test
		@DisplayName("언어 규칙을 수정한다")
		void updateScenario_UpdatesLanguageRules() {
			// given
			ConversationScenarioEntity existingScenario = createExistingScenario();
			given(scenarioQueryRepository.findByIdOrThrow(SCENARIO_ID)).willReturn(existingScenario);

			UpdateScenarioUseCaseDto dto = createUpdateDto();

			// when
			updateScenarioUseCase.execute(dto);

			// then
			assertThat(existingScenario.getLanguageRules()).isNotNull();
			assertThat(existingScenario.getLanguageRules().getVocabularyRules()).contains("Updated vocabulary rule");
		}

		@Test
		@DisplayName("행동 규칙을 수정한다")
		void updateScenario_UpdatesBehaviorRules() {
			// given
			ConversationScenarioEntity existingScenario = createExistingScenario();
			given(scenarioQueryRepository.findByIdOrThrow(SCENARIO_ID)).willReturn(existingScenario);

			UpdateScenarioUseCaseDto dto = createUpdateDto();

			// when
			updateScenarioUseCase.execute(dto);

			// then
			assertThat(existingScenario.getBehaviorRules()).isNotNull();
			assertThat(existingScenario.getBehaviorRules().getRules()).contains("Updated behavior rule");
		}

		@Test
		@DisplayName("대화 상태 목록을 수정한다")
		void updateScenario_UpdatesConversationStates() {
			// given
			ConversationScenarioEntity existingScenario = createExistingScenario();
			given(scenarioQueryRepository.findByIdOrThrow(SCENARIO_ID)).willReturn(existingScenario);

			UpdateScenarioUseCaseDto dto = createUpdateDto();

			// when
			updateScenarioUseCase.execute(dto);

			// then
			assertThat(existingScenario.getConversationStates()).hasSize(3);
		}

		@Test
		@DisplayName("대화 슬롯 목록을 수정한다")
		void updateScenario_UpdatesConversationSlots() {
			// given
			ConversationScenarioEntity existingScenario = createExistingScenario();
			given(scenarioQueryRepository.findByIdOrThrow(SCENARIO_ID)).willReturn(existingScenario);

			UpdateScenarioUseCaseDto dto = createUpdateDto();

			// when
			updateScenarioUseCase.execute(dto);

			// then
			assertThat(existingScenario.getConversationSlots()).hasSize(2);
		}

		@Test
		@DisplayName("존재하지 않는 시나리오 수정 시 예외가 발생한다")
		void updateScenario_NotFound_ThrowsException() {
			// given
			given(scenarioQueryRepository.findByIdOrThrow(999L))
				.willThrow(new CoreException(ApiErrorType.SCENARIO_NOT_FOUND));

			UpdateScenarioUseCaseDto dto = new UpdateScenarioUseCaseDto(
				999L,
				"Updated Name",
				Level.BEGINNER,
				"ai role",
				"AI 역할",
				"user role",
				"사용자 역할",
				"completion rule",
				List.of(),
				new UpdateScenarioUseCaseDto.ScenarioContextDto("context", List.of()),
				new UpdateScenarioUseCaseDto.LanguageRulesDto(List.of(), List.of(), List.of()),
				new UpdateScenarioUseCaseDto.BehaviorRulesDto(List.of()),
				null,
				null
			);

			// when & then
			assertThatThrownBy(() -> updateScenarioUseCase.execute(dto))
				.isInstanceOf(CoreException.class)
				.hasFieldOrPropertyWithValue("errorType", ApiErrorType.SCENARIO_NOT_FOUND);
		}
	}

	private ConversationScenarioEntity createExistingScenario() {
		ConversationScenarioEntity scenario = ConversationScenarioEntity.create(
			"Cafe Order",
			Level.BEGINNER,
			"a cafe staff member",
			"카페 직원",
			"a customer",
			"손님",
			"When all required information is collected",
			List.of("Confirm the order")
		);

		ScenarioContextEntity context = ScenarioContextEntity.create(
			"The user is visiting a cafe.",
			List.of("Friendly", "Patient")
		);
		scenario.addScenarioContext(context);

		LanguageRulesEntity languageRules = LanguageRulesEntity.create(
			List.of("Use simple vocabulary"),
			List.of("Use short sentences"),
			List.of("Use simple English")
		);
		scenario.addLanguageRules(languageRules);

		BehaviorRulesEntity behaviorRules = BehaviorRulesEntity.create(
			List.of("NEVER break character")
		);
		scenario.addBehaviorRules(behaviorRules);

		ConversationStateEntity state1 = ConversationStateEntity.create(1, "Greeting");
		ConversationStateEntity state2 = ConversationStateEntity.create(2, "Ask for drink");
		scenario.addConversationState(state1);
		scenario.addConversationState(state2);

		ConversationSlotEntity slot = ConversationSlotEntity.create("drink_type", List.of("coffee", "latte"));
		scenario.addConversationSlot(slot);

		setId(scenario, SCENARIO_ID);
		return scenario;
	}

	private UpdateScenarioUseCaseDto createUpdateDto() {
		return new UpdateScenarioUseCaseDto(
			SCENARIO_ID,
			"Updated Cafe Order",
			Level.INTERMEDIATE,
			"an updated cafe staff member",
			"수정된 카페 직원",
			"an updated customer",
			"수정된 손님",
			"Updated completion rule",
			List.of("Updated completion detail"),
			new UpdateScenarioUseCaseDto.ScenarioContextDto(
				"Updated context",
				List.of("Updated personality")
			),
			new UpdateScenarioUseCaseDto.LanguageRulesDto(
				List.of("Updated vocabulary rule"),
				List.of("Updated sentence rule"),
				List.of("Updated output constraint")
			),
			new UpdateScenarioUseCaseDto.BehaviorRulesDto(
				List.of("Updated behavior rule")
			),
			List.of(
				new UpdateScenarioUseCaseDto.ConversationStateDto(1, "Updated Greeting"),
				new UpdateScenarioUseCaseDto.ConversationStateDto(2, "Updated Ask for drink"),
				new UpdateScenarioUseCaseDto.ConversationStateDto(3, "New State")
			),
			List.of(
				new UpdateScenarioUseCaseDto.ConversationSlotDto("drink_type", List.of("espresso", "cappuccino")),
				new UpdateScenarioUseCaseDto.ConversationSlotDto("size", List.of("small", "medium", "large"))
			)
		);
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
