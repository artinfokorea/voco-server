package com.voco.voco.tov.application.dto.in;

import java.util.UUID;

public record CreateExamUseCaseDto(
	UUID memberId,
	UUID groupId,
	int chapterFrom,
	int chapterTo,
	Integer stepFrom,
	Integer stepTo,
	int size
) {
}
