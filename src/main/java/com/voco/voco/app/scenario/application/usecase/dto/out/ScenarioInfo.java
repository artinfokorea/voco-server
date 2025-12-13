package com.voco.voco.app.scenario.application.usecase.dto.out;

import com.voco.voco.app.scenario.domain.model.Category;
import com.voco.voco.app.scenario.domain.model.Level;
import com.voco.voco.app.scenario.domain.model.ScenarioEntity;

public record ScenarioInfo(
	Long id,
	String title,
	String description,
	Level level,
	Category category,
	String content
) {
	public static ScenarioInfo from(ScenarioEntity entity) {
		return new ScenarioInfo(
			entity.getId(),
			entity.getTitle(),
			entity.getDescription(),
			entity.getLevel(),
			entity.getCategory(),
			entity.getContent()
		);
	}
}
