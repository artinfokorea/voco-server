package com.voco.voco.app.scenario.application.usecase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voco.voco.app.scenario.application.usecase.dto.out.ScenarioSummaryInfo;
import com.voco.voco.app.scenario.domain.interfaces.ScenarioQueryRepository;
import com.voco.voco.app.scenario.domain.model.Level;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetScenariosUseCase {

	private final ScenarioQueryRepository scenarioQueryRepository;

	@Transactional(readOnly = true)
	public Page<ScenarioSummaryInfo> execute(Level level, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return scenarioQueryRepository.findAllByLevel(level, pageable)
			.map(ScenarioSummaryInfo::from);
	}
}
