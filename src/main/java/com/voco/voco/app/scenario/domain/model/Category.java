package com.voco.voco.app.scenario.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
	DAILY("일상"),
	BUSINESS("비즈니스"),
	TRAVEL("여행"),
	SHOPPING("쇼핑");

	private final String description;
}