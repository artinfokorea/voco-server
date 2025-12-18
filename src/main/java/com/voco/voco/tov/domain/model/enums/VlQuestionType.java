package com.voco.voco.tov.domain.model.enums;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VlQuestionType {
	MEANING_MULTIPLE_CHOICE("meaning_multiple_choice"),
	SPELLING_SUBJECTIVE("spelling_subjective"),
	SYNONYM_MULTIPLE_CHOICE("synonym_multiple_choice"),
	ANTONYM_MULTIPLE_CHOICE("antonym_multiple_choice"),
	CONTEXT_MULTIPLE_CHOICE("context_multiple_choice");

	private final String value;

	public static VlQuestionType fromValue(String value) {
		return Arrays.stream(values())
			.filter(type -> type.value.equals(value))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Unknown VlQuestionType: " + value));
	}
}