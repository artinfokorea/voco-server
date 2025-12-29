package com.voco.voco.app.scenario.presentation.controller.dto.in;

import com.voco.voco.app.scenario.domain.model.Level;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "시나리오 목록 조회 요청")
public record GetScenariosRequest(
	@Schema(description = "레벨 필터 (null이면 전체 조회)", example = "BEGINNER")
	Level level,

	@Schema(description = "페이지 번호 (1부터 시작)", example = "1")
	Integer page,

	@Schema(description = "페이지 크기", example = "10")
	Integer size
) {
	public GetScenariosRequest {
		if (page == null || page < 1) {
			page = 1;
		}
		if (size == null) {
			size = 10;
		}
	}

	@Schema(hidden = true)
	public int getPageIndex() {
		return page - 1;
	}
}
