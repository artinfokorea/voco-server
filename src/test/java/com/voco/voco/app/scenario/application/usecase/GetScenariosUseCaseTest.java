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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.voco.voco.app.scenario.application.usecase.dto.out.ScenarioSummaryInfo;
import com.voco.voco.app.scenario.domain.interfaces.ScenarioQueryRepository;
import com.voco.voco.app.scenario.domain.model.ConversationScenarioEntity;
import com.voco.voco.app.scenario.domain.model.Level;

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
		@DisplayName("레벨 필터 없이 전체 시나리오를 조회한다")
		void getScenarios_WithoutLevelFilter_Success() {
			// given
			int page = 0;
			int size = 10;
			Pageable pageable = PageRequest.of(page, size);

			List<ConversationScenarioEntity> scenarios = List.of(
				createScenario(1L, "Cafe Order", Level.BEGINNER),
				createScenario(2L, "Hotel Reservation", Level.INTERMEDIATE)
			);
			Page<ConversationScenarioEntity> scenarioPage = new PageImpl<>(scenarios, pageable, 2);

			given(scenarioQueryRepository.findAllByLevel(null, pageable)).willReturn(scenarioPage);

			// when
			Page<ScenarioSummaryInfo> result = getScenariosUseCase.execute(null, page, size);

			// then
			assertThat(result.getContent()).hasSize(2);
			assertThat(result.getTotalElements()).isEqualTo(2);
			then(scenarioQueryRepository).should().findAllByLevel(null, pageable);
		}

		@Test
		@DisplayName("레벨 필터로 시나리오를 조회한다")
		void getScenarios_WithLevelFilter_Success() {
			// given
			Level level = Level.BEGINNER;
			int page = 0;
			int size = 10;
			Pageable pageable = PageRequest.of(page, size);

			List<ConversationScenarioEntity> scenarios = List.of(
				createScenario(1L, "Cafe Order", Level.BEGINNER),
				createScenario(3L, "Restaurant Order", Level.BEGINNER)
			);
			Page<ConversationScenarioEntity> scenarioPage = new PageImpl<>(scenarios, pageable, 2);

			given(scenarioQueryRepository.findAllByLevel(level, pageable)).willReturn(scenarioPage);

			// when
			Page<ScenarioSummaryInfo> result = getScenariosUseCase.execute(level, page, size);

			// then
			assertThat(result.getContent()).hasSize(2);
			assertThat(result.getContent()).allMatch(info -> info.level().equals("BEGINNER"));
			then(scenarioQueryRepository).should().findAllByLevel(level, pageable);
		}

		@Test
		@DisplayName("빈 결과를 반환한다")
		void getScenarios_EmptyResult_Success() {
			// given
			Level level = Level.ADVANCED;
			int page = 0;
			int size = 10;
			Pageable pageable = PageRequest.of(page, size);

			Page<ConversationScenarioEntity> emptyPage = new PageImpl<>(List.of(), pageable, 0);

			given(scenarioQueryRepository.findAllByLevel(level, pageable)).willReturn(emptyPage);

			// when
			Page<ScenarioSummaryInfo> result = getScenariosUseCase.execute(level, page, size);

			// then
			assertThat(result.getContent()).isEmpty();
			assertThat(result.getTotalElements()).isZero();
		}

		@Test
		@DisplayName("ScenarioSummaryInfo로 올바르게 변환한다")
		void getScenarios_MapsToScenarioSummaryInfo() {
			// given
			int page = 0;
			int size = 10;
			Pageable pageable = PageRequest.of(page, size);

			ConversationScenarioEntity scenario = createScenario(1L, "Cafe Order", Level.BEGINNER);
			Page<ConversationScenarioEntity> scenarioPage = new PageImpl<>(List.of(scenario), pageable, 1);

			given(scenarioQueryRepository.findAllByLevel(null, pageable)).willReturn(scenarioPage);

			// when
			Page<ScenarioSummaryInfo> result = getScenariosUseCase.execute(null, page, size);

			// then
			assertThat(result.getContent()).hasSize(1);
			ScenarioSummaryInfo info = result.getContent().get(0);
			assertThat(info.scenarioId()).isEqualTo(1L);
			assertThat(info.name()).isEqualTo("Cafe Order");
			assertThat(info.level()).isEqualTo("BEGINNER");
		}

		@Test
		@DisplayName("페이징 정보가 올바르게 적용된다")
		void getScenarios_PaginationApplied() {
			// given
			int page = 1;
			int size = 5;
			Pageable pageable = PageRequest.of(page, size);

			List<ConversationScenarioEntity> scenarios = List.of(
				createScenario(6L, "Scenario 6", Level.BEGINNER)
			);
			Page<ConversationScenarioEntity> scenarioPage = new PageImpl<>(scenarios, pageable, 11);

			given(scenarioQueryRepository.findAllByLevel(null, pageable)).willReturn(scenarioPage);

			// when
			Page<ScenarioSummaryInfo> result = getScenariosUseCase.execute(null, page, size);

			// then
			assertThat(result.getNumber()).isEqualTo(1);
			assertThat(result.getSize()).isEqualTo(5);
			assertThat(result.getTotalElements()).isEqualTo(11);
			assertThat(result.getTotalPages()).isEqualTo(3);
		}
	}

	private ConversationScenarioEntity createScenario(Long id, String name, Level level) {
		ConversationScenarioEntity scenario = ConversationScenarioEntity.create(
			name,
			level,
			"ai role",
			"user role",
			"completion rule",
			List.of("detail")
		);
		setId(scenario, id);
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
