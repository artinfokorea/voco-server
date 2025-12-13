package com.voco.voco.app.scenario.application.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.voco.voco.app.scenario.application.usecase.dto.out.ScenarioInfo;
import com.voco.voco.app.scenario.domain.interfaces.ScenarioQueryRepository;
import com.voco.voco.app.scenario.domain.model.Level;
import com.voco.voco.app.scenario.domain.model.ScenarioEntity;

@ExtendWith(MockitoExtension.class)
class GetScenariosUseCaseTest {

	@InjectMocks
	private GetScenariosUseCase getScenariosUseCase;

	@Mock
	private ScenarioQueryRepository scenarioQueryRepository;

	@Nested
	@DisplayName("execute 메서드")
	class Execute {

		@Test
		@DisplayName("level이 null이면 전체 시나리오를 조회한다")
		void getScenarios_WithNullLevel_ReturnsAll() {
			// given
			ScenarioEntity scenario1 = createScenarioWithId(1L, "카페에서 주문하기", "카페 상황 연습", Level.BEGINNER, "내용1");
			ScenarioEntity scenario2 = createScenarioWithId(2L, "비즈니스 미팅", "미팅 상황 연습", Level.ADVANCED, "내용2");

			given(scenarioQueryRepository.findAll())
				.willReturn(List.of(scenario1, scenario2));

			// when
			List<ScenarioInfo> result = getScenariosUseCase.execute(null);

			// then
			assertThat(result).hasSize(2);
			assertThat(result.get(0).id()).isEqualTo(1L);
			assertThat(result.get(0).title()).isEqualTo("카페에서 주문하기");
			assertThat(result.get(0).level()).isEqualTo(Level.BEGINNER);
			assertThat(result.get(1).id()).isEqualTo(2L);
			assertThat(result.get(1).title()).isEqualTo("비즈니스 미팅");
			assertThat(result.get(1).level()).isEqualTo(Level.ADVANCED);
			verify(scenarioQueryRepository).findAll();
			verify(scenarioQueryRepository, never()).findAllByLevel(any());
		}

		@Test
		@DisplayName("level이 지정되면 해당 레벨의 시나리오만 조회한다")
		void getScenarios_WithLevel_ReturnsFiltered() {
			// given
			ScenarioEntity beginnerScenario = createScenarioWithId(1L, "카페에서 주문하기", "카페 상황 연습", Level.BEGINNER, "내용1");

			given(scenarioQueryRepository.findAllByLevel(Level.BEGINNER))
				.willReturn(List.of(beginnerScenario));

			// when
			List<ScenarioInfo> result = getScenariosUseCase.execute(Level.BEGINNER);

			// then
			assertThat(result).hasSize(1);
			assertThat(result.get(0).level()).isEqualTo(Level.BEGINNER);
			verify(scenarioQueryRepository).findAllByLevel(Level.BEGINNER);
			verify(scenarioQueryRepository, never()).findAll();
		}

		@Test
		@DisplayName("시나리오가 없으면 빈 리스트를 반환한다")
		void getScenarios_Empty_ReturnsEmptyList() {
			// given
			given(scenarioQueryRepository.findAll())
				.willReturn(Collections.emptyList());

			// when
			List<ScenarioInfo> result = getScenariosUseCase.execute(null);

			// then
			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("해당 레벨의 시나리오가 없으면 빈 리스트를 반환한다")
		void getScenarios_WithLevel_Empty_ReturnsEmptyList() {
			// given
			given(scenarioQueryRepository.findAllByLevel(Level.ADVANCED))
				.willReturn(Collections.emptyList());

			// when
			List<ScenarioInfo> result = getScenariosUseCase.execute(Level.ADVANCED);

			// then
			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("하나의 시나리오만 있어도 조회에 성공한다")
		void getScenarios_SingleScenario_Success() {
			// given
			ScenarioEntity scenario = createScenarioWithId(1L, "카페에서 주문하기", "카페 상황 연습", Level.BEGINNER, "내용");

			given(scenarioQueryRepository.findAll())
				.willReturn(List.of(scenario));

			// when
			List<ScenarioInfo> result = getScenariosUseCase.execute(null);

			// then
			assertThat(result).hasSize(1);
			assertThat(result.get(0).title()).isEqualTo("카페에서 주문하기");
		}
	}

	private ScenarioEntity createScenarioWithId(Long id, String title, String description, Level level, String content) {
		ScenarioEntity scenario = ScenarioEntity.create(title, description, level, content);
		try {
			java.lang.reflect.Field idField = ScenarioEntity.class.getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(scenario, id);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return scenario;
	}
}
