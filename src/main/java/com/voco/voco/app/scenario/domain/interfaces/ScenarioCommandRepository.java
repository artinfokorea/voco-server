package com.voco.voco.app.scenario.domain.interfaces;

import com.voco.voco.app.scenario.domain.model.ConversationScenarioEntity;

public interface ScenarioCommandRepository {

	Long save(ConversationScenarioEntity scenario);
}
