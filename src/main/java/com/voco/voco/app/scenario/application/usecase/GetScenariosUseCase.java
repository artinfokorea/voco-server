package com.voco.voco.app.scenario.application.usecase;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voco.voco.app.scenario.application.usecase.dto.out.ScenarioInfo;
import com.voco.voco.app.scenario.domain.interfaces.ScenarioQueryRepository;
import com.voco.voco.app.scenario.domain.model.Level;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetScenariosUseCase {

	private final ScenarioQueryRepository scenarioQueryRepository;

	public List<ScenarioInfo> execute(Level level) {
		return Optional.ofNullable(level)
			.map(scenarioQueryRepository::findAllByLevel)
			.orElseGet(scenarioQueryRepository::findAll)
			.stream()
			.map(ScenarioInfo::from)
			.toList();
	}
}
