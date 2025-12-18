package com.voco.voco.tov.domain.interfaces.dto;

import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class WordWithDetailsDto {
	private final UUID masterWordId;
	private final String englishWord;
	private final List<String> meanings;
	private final List<String> synonyms;
	private final List<String> antonyms;
	private final List<ExampleSentenceDto> exampleSentences;

	public boolean hasSynonym() {
		return synonyms != null && !synonyms.isEmpty();
	}

	public boolean hasAntonym() {
		return antonyms != null && !antonyms.isEmpty();
	}

	public boolean hasExampleSentence() {
		return exampleSentences != null && !exampleSentences.isEmpty();
	}

	@Getter
	@RequiredArgsConstructor
	public static class ExampleSentenceDto {
		private final String sentenceWithBlank;
		private final String blankAnswer;
	}
}