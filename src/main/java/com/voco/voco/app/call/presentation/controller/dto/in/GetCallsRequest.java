package com.voco.voco.app.call.presentation.controller.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "통화 내역 조회 요청")
public record GetCallsRequest(
	@Schema(description = "페이지 번호 (1부터 시작)", example = "1", defaultValue = "1")
	Integer page,

	@Schema(description = "페이지 크기", example = "10", defaultValue = "10")
	Integer size
) {
	public GetCallsRequest {
		if (page == null || page < 1) {
			page = 1;
		}
		if (size == null || size < 1) {
			size = 10;
		}
	}

	public int getPageIndex() {
		return page - 1;
	}
}
