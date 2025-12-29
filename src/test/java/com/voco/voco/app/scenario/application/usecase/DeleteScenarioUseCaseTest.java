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

import com.voco.voco.app.scenario.domain.interfaces.ScenarioQueryRepository;
import com.voco.voco.app.scenario.domain.model.ConversationScenarioEntity;
import com.voco.voco.app.scenario.domain.model.Level;
import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;

@ExtendWith(MockitoExtension.class)
class DeleteScenarioUseCaseTest {

	@InjectMocks
	private DeleteScenarioUseCase deleteScenarioUseCase;

	@Mock
	private ScenarioQueryRepository scenarioQueryRepository;

	private static final Long SCENARIO_ID = 1L;

	@Nested
	@DisplayName("execute 메서드")
	class Execute {

		@Test
		@DisplayName("시나리오를 성공적으로 삭제한다")
		void deleteScenario_Success() {
			// given
			ConversationScenarioEntity scenario = createScenario();
			given(scenarioQueryRepository.findByIdOrThrow(SCENARIO_ID)).willReturn(scenario);

			// when
			deleteScenarioUseCase.execute(SCENARIO_ID);

			// then
			then(scenarioQueryRepository).should().findByIdOrThrow(SCENARIO_ID);
		}

		@Test
		@DisplayName("존재하지 않는 시나리오 삭제 시 예외가 발생한다")
		void deleteScenario_NotFound_ThrowsException() {
			// given
			given(scenarioQueryRepository.findByIdOrThrow(999L))
				.willThrow(new CoreException(ApiErrorType.SCENARIO_NOT_FOUND));

			// when & then
			assertThatThrownBy(() -> deleteScenarioUseCase.execute(999L))
				.isInstanceOf(CoreException.class)
				.hasFieldOrPropertyWithValue("errorType", ApiErrorType.SCENARIO_NOT_FOUND);
		}
	}

	private ConversationScenarioEntity createScenario() {
		ConversationScenarioEntity scenario = ConversationScenarioEntity.create(
			"Cafe Order",
			Level.BEGINNER,
			"a cafe staff member",
			"a customer",
			"When all required information is collected",
			List.of("Confirm the order")
		);
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
