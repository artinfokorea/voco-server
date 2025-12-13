package com.voco.voco.app.call.presentation.controller.dto.in;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "통화 내역 조회 요청")
public record GetCallsRequest(
	@Schema(description = "페이지 번호 (0부터 시작)", example = "0", defaultValue = "0")
	Integer page,

	@Schema(description = "페이지 크기", example = "10", defaultValue = "10")
	Integer size
) {
	public GetCallsRequest {
		if (page == null) {
			page = 0;
		}
		if (size == null) {
			size = 10;
		}
	}

	public Pageable toPageable() {
		return PageRequest.of(page, size);
	}
}
