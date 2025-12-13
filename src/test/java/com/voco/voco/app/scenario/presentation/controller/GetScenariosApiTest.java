package com.voco.voco.app.scenario.presentation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.voco.voco.app.scenario.domain.model.Category;
import com.voco.voco.app.scenario.domain.model.Level;
import com.voco.voco.app.scenario.domain.model.ScenarioEntity;
import com.voco.voco.app.scenario.infrastructure.repository.ScenarioJpaRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("GET /api/v1/scenarios")
class GetScenariosApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ScenarioJpaRepository scenarioJpaRepository;

	private static final String SCENARIO_URL = "/api/v1/scenarios";

	@Test
	@DisplayName("시나리오 전체 조회에 성공한다")
	void getScenarios_Success() throws Exception {
		// given
		scenarioJpaRepository.save(ScenarioEntity.create("카페에서 주문하기", "카페 상황 연습", Level.BEGINNER, Category.DAILY, "내용1"));
		scenarioJpaRepository.save(ScenarioEntity.create("비즈니스 미팅", "미팅 상황 연습", Level.ADVANCED, Category.BUSINESS, "내용2"));

		// when
		ResultActions result = mockMvc.perform(get(SCENARIO_URL));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("SUCCESS"))
			.andExpect(jsonPath("$.item").isArray())
			.andExpect(jsonPath("$.item.length()").value(2));
	}

	@Test
	@DisplayName("레벨로 필터링하여 시나리오를 조회한다")
	void getScenarios_WithLevelFilter_Success() throws Exception {
		// given
		scenarioJpaRepository.save(ScenarioEntity.create("카페에서 주문하기", "카페 상황 연습", Level.BEGINNER, Category.DAILY, "내용1"));
		scenarioJpaRepository.save(ScenarioEntity.create("택시 타기", "택시 상황 연습", Level.BEGINNER, Category.TRAVEL, "내용2"));
		scenarioJpaRepository.save(ScenarioEntity.create("비즈니스 미팅", "미팅 상황 연습", Level.ADVANCED, Category.BUSINESS, "내용3"));

		// when
		ResultActions result = mockMvc.perform(get(SCENARIO_URL)
			.param("level", "BEGINNER"));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("SUCCESS"))
			.andExpect(jsonPath("$.item").isArray())
			.andExpect(jsonPath("$.item.length()").value(2))
			.andExpect(jsonPath("$.item[0].level").value("BEGINNER"))
			.andExpect(jsonPath("$.item[1].level").value("BEGINNER"));
	}

	@Test
	@DisplayName("시나리오가 없으면 빈 리스트를 반환한다")
	void getScenarios_Empty_ReturnsEmptyList() throws Exception {
		// when
		ResultActions result = mockMvc.perform(get(SCENARIO_URL));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("SUCCESS"))
			.andExpect(jsonPath("$.item").isArray())
			.andExpect(jsonPath("$.item.length()").value(0));
	}

	@Test
	@DisplayName("해당 레벨의 시나리오가 없으면 빈 리스트를 반환한다")
	void getScenarios_WithLevelFilter_Empty_ReturnsEmptyList() throws Exception {
		// given
		scenarioJpaRepository.save(ScenarioEntity.create("카페에서 주문하기", "카페 상황 연습", Level.BEGINNER, Category.DAILY, "내용1"));

		// when
		ResultActions result = mockMvc.perform(get(SCENARIO_URL)
			.param("level", "ADVANCED"));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("SUCCESS"))
			.andExpect(jsonPath("$.item").isArray())
			.andExpect(jsonPath("$.item.length()").value(0));
	}

	@Test
	@DisplayName("INTERMEDIATE 레벨로 필터링하여 조회한다")
	void getScenarios_WithIntermediateLevel_Success() throws Exception {
		// given
		scenarioJpaRepository.save(ScenarioEntity.create("카페에서 주문하기", "카페 상황 연습", Level.BEGINNER, Category.DAILY, "내용1"));
		scenarioJpaRepository.save(ScenarioEntity.create("호텔 체크인", "호텔 상황 연습", Level.INTERMEDIATE, Category.TRAVEL, "내용2"));
		scenarioJpaRepository.save(ScenarioEntity.create("비즈니스 미팅", "미팅 상황 연습", Level.ADVANCED, Category.BUSINESS, "내용3"));

		// when
		ResultActions result = mockMvc.perform(get(SCENARIO_URL)
			.param("level", "INTERMEDIATE"));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("SUCCESS"))
			.andExpect(jsonPath("$.item").isArray())
			.andExpect(jsonPath("$.item.length()").value(1))
			.andExpect(jsonPath("$.item[0].level").value("INTERMEDIATE"));
	}

	@Test
	@DisplayName("시나리오 응답에 모든 필드가 포함된다")
	void getScenarios_ResponseContainsAllFields() throws Exception {
		// given
		scenarioJpaRepository.save(ScenarioEntity.create("카페에서 주문하기", "카페 상황 연습", Level.BEGINNER, Category.DAILY, "시나리오 내용입니다."));

		// when
		ResultActions result = mockMvc.perform(get(SCENARIO_URL));

		// then
		result
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("SUCCESS"))
			.andExpect(jsonPath("$.item[0].id").isNumber())
			.andExpect(jsonPath("$.item[0].title").value("카페에서 주문하기"))
			.andExpect(jsonPath("$.item[0].description").value("카페 상황 연습"))
			.andExpect(jsonPath("$.item[0].level").value("BEGINNER"))
			.andExpect(jsonPath("$.item[0].content").value("시나리오 내용입니다."));
	}
}
