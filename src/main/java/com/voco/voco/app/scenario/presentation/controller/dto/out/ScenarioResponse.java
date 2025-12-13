package com.voco.voco.app.scenario.presentation.controller.dto.out;

import com.voco.voco.app.scenario.application.usecase.dto.out.ScenarioInfo;
import com.voco.voco.app.scenario.domain.model.Level;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "시나리오 응답")
public record ScenarioResponse(
	@Schema(description = "시나리오 ID", example = "1")
	Long id,

	@Schema(description = "시나리오 제목", example = "카페에서 주문하기")
	String title,

	@Schema(description = "시나리오 설명", example = "카페에서 음료를 주문하는 상황을 연습합니다.")
	String description,

	@Schema(description = "난이도", example = "BEGINNER")
	Level level,

	@Schema(description = "시나리오 내용")
	String content
) {
	public static ScenarioResponse from(ScenarioInfo info) {
		return new ScenarioResponse(
			info.id(),
			info.title(),
			info.description(),
			info.level(),
			info.content()
		);
	}
}
