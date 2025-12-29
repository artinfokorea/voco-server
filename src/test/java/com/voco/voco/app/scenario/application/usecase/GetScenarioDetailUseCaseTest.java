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

import com.voco.voco.app.scenario.application.usecase.dto.out.ScenarioDetailInfo;
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
class GetScenarioDetailUseCaseTest {

	@InjectMocks
	private GetScenarioDetailUseCase getScenarioDetailUseCase;

	@Mock
	private ScenarioQueryRepository scenarioQueryRepository;

	private static final Long SCENARIO_ID = 1L;

	@Nested
	@DisplayName("execute 메서드")
	class Execute {

		@Test
		@DisplayName("시나리오 상세 정보를 조회한다")
		void getScenarioDetail_Success() {
			// given
			ConversationScenarioEntity scenario = createFullScenario();
			given(scenarioQueryRepository.findByIdOrThrow(SCENARIO_ID)).willReturn(scenario);

			// when
			ScenarioDetailInfo result = getScenarioDetailUseCase.execute(SCENARIO_ID);

			// then
			assertThat(result.scenarioId()).isEqualTo(SCENARIO_ID);
			assertThat(result.name()).isEqualTo("Cafe Order");
			assertThat(result.level()).isEqualTo("BEGINNER");
			assertThat(result.aiRole()).isEqualTo("a cafe staff member");
			assertThat(result.userRole()).isEqualTo("a customer");
		}

		@Test
		@DisplayName("시나리오 컨텍스트 정보를 포함한다")
		void getScenarioDetail_ContainsContext() {
			// given
			ConversationScenarioEntity scenario = createFullScenario();
			given(scenarioQueryRepository.findByIdOrThrow(SCENARIO_ID)).willReturn(scenario);

			// when
			ScenarioDetailInfo result = getScenarioDetailUseCase.execute(SCENARIO_ID);

			// then
			assertThat(result.scenarioContext()).isNotNull();
			assertThat(result.scenarioContext().context()).isEqualTo("The user is visiting a cafe.");
			assertThat(result.scenarioContext().personality()).hasSize(2);
		}

		@Test
		@DisplayName("언어 규칙 정보를 포함한다")
		void getScenarioDetail_ContainsLanguageRules() {
			// given
			ConversationScenarioEntity scenario = createFullScenario();
			given(scenarioQueryRepository.findByIdOrThrow(SCENARIO_ID)).willReturn(scenario);

			// when
			ScenarioDetailInfo result = getScenarioDetailUseCase.execute(SCENARIO_ID);

			// then
			assertThat(result.languageRules()).isNotNull();
			assertThat(result.languageRules().vocabularyRules()).isNotEmpty();
			assertThat(result.languageRules().sentenceRules()).isNotEmpty();
			assertThat(result.languageRules().outputConstraints()).isNotEmpty();
		}

		@Test
		@DisplayName("행동 규칙 정보를 포함한다")
		void getScenarioDetail_ContainsBehaviorRules() {
			// given
			ConversationScenarioEntity scenario = createFullScenario();
			given(scenarioQueryRepository.findByIdOrThrow(SCENARIO_ID)).willReturn(scenario);

			// when
			ScenarioDetailInfo result = getScenarioDetailUseCase.execute(SCENARIO_ID);

			// then
			assertThat(result.behaviorRules()).isNotNull();
			assertThat(result.behaviorRules().rules()).isNotEmpty();
		}

		@Test
		@DisplayName("대화 상태 목록을 포함한다")
		void getScenarioDetail_ContainsConversationStates() {
			// given
			ConversationScenarioEntity scenario = createFullScenario();
			given(scenarioQueryRepository.findByIdOrThrow(SCENARIO_ID)).willReturn(scenario);

			// when
			ScenarioDetailInfo result = getScenarioDetailUseCase.execute(SCENARIO_ID);

			// then
			assertThat(result.conversationStates()).hasSize(2);
			assertThat(result.conversationStates().get(0).stateOrder()).isEqualTo(1);
			assertThat(result.conversationStates().get(0).stateName()).isEqualTo("Greeting");
		}

		@Test
		@DisplayName("대화 슬롯 목록을 포함한다")
		void getScenarioDetail_ContainsConversationSlots() {
			// given
			ConversationScenarioEntity scenario = createFullScenario();
			given(scenarioQueryRepository.findByIdOrThrow(SCENARIO_ID)).willReturn(scenario);

			// when
			ScenarioDetailInfo result = getScenarioDetailUseCase.execute(SCENARIO_ID);

			// then
			assertThat(result.conversationSlots()).hasSize(1);
			assertThat(result.conversationSlots().get(0).slotKey()).isEqualTo("drink_type");
			assertThat(result.conversationSlots().get(0).allowedValues()).contains("coffee", "latte");
		}

		@Test
		@DisplayName("완료 규칙 정보를 포함한다")
		void getScenarioDetail_ContainsCompletionRule() {
			// given
			ConversationScenarioEntity scenario = createFullScenario();
			given(scenarioQueryRepository.findByIdOrThrow(SCENARIO_ID)).willReturn(scenario);

			// when
			ScenarioDetailInfo result = getScenarioDetailUseCase.execute(SCENARIO_ID);

			// then
			assertThat(result.completionRule()).isEqualTo("When all required information is collected");
			assertThat(result.completionRuleDetail()).hasSize(1);
		}

		@Test
		@DisplayName("존재하지 않는 시나리오 조회 시 예외가 발생한다")
		void getScenarioDetail_NotFound_ThrowsException() {
			// given
			given(scenarioQueryRepository.findByIdOrThrow(999L))
				.willThrow(new CoreException(ApiErrorType.SCENARIO_NOT_FOUND));

			// when & then
			assertThatThrownBy(() -> getScenarioDetailUseCase.execute(999L))
				.isInstanceOf(CoreException.class)
				.hasFieldOrPropertyWithValue("errorType", ApiErrorType.SCENARIO_NOT_FOUND);
		}
	}

	private ConversationScenarioEntity createFullScenario() {
		ConversationScenarioEntity scenario = ConversationScenarioEntity.create(
			"Cafe Order",
			Level.BEGINNER,
			"a cafe staff member",
			"a customer",
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
