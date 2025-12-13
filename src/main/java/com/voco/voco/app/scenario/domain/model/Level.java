package com.voco.voco.app.scenario.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Level {
	BEGINNER("초급"),
	INTERMEDIATE("중급"),
	ADVANCED("고급");

	private final String description;
}
