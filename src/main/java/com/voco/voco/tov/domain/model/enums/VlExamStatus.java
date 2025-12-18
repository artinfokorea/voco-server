package com.voco.voco.tov.domain.model.enums;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VlExamStatus {
	IN_PROGRESS("in_progress"),
	COMPLETED("completed");

	private final String value;

	public static VlExamStatus fromValue(String value) {
		return Arrays.stream(values())
			.filter(status -> status.value.equals(value))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Unknown VlExamStatus: " + value));
	}
}