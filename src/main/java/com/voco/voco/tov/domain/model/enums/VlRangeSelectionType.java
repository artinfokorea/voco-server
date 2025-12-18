package com.voco.voco.tov.domain.model.enums;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VlRangeSelectionType {
	CHAPTER("chapter"),
	CHAPTER_STEP("chapter-step"),
	SINGLE("single");

	private final String value;

	public static VlRangeSelectionType fromValue(String value) {
		return Arrays.stream(values())
			.filter(type -> type.value.equals(value))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Unknown VlRangeSelectionType: " + value));
	}
}
