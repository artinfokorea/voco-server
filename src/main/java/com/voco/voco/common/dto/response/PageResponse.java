package com.voco.voco.common.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "페이징 응답")
public record PageResponse<T>(
	@Schema(description = "컨텐츠 목록")
	List<T> content,

	@Schema(description = "페이지 정보")
	PageInfo page
) {
	public static <T> PageResponse<T> from(Page<T> page) {
		return new PageResponse<>(
			page.getContent(),
			new PageInfo(
				page.getSize(),
				page.getNumber() + 1,
				page.getTotalElements(),
				page.getTotalPages()
			)
		);
	}

	@Schema(description = "페이지 정보")
	public record PageInfo(
		@Schema(description = "페이지 크기", example = "10")
		int size,

		@Schema(description = "현재 페이지 번호 (1부터 시작)", example = "1")
		int number,

		@Schema(description = "전체 요소 수", example = "100")
		long totalElements,

		@Schema(description = "전체 페이지 수", example = "10")
		int totalPages
	) {
	}
}
