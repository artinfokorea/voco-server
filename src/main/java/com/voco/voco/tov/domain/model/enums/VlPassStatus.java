package com.voco.voco.tov.domain.model.enums;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VlPassStatus {
	PASS("PASS"),
	FAIL("FAIL");

	private final String value;

	public static VlPassStatus fromValue(String value) {
		return Arrays.stream(values())
			.filter(status -> status.value.equals(value))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Unknown VlPassStatus: " + value));
	}
}