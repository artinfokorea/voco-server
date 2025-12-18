package com.voco.voco.tov.domain.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.voco.voco.tov.domain.interfaces.dto.WordWithDetailsDto;
import com.voco.voco.tov.domain.model.VlExamQuestionEntity;
import com.voco.voco.tov.domain.model.VlWordGroupEntity;
import com.voco.voco.tov.domain.model.enums.VlQuestionType;

@Service
public class ExamQuestionGenerator {

	private final ObjectMapper objectMapper = new ObjectMapper();

	public List<VlExamQuestionEntity> generate(UUID examId, VlWordGroupEntity group,
		List<WordWithDetailsDto> words, int requestedSize) {

		List<VlQuestionType> enabledTypes = getEnabledTypes(group);
		if (enabledTypes.isEmpty() || words.isEmpty()) {
			return Collections.emptyList();
		}

		int totalQuestions = Math.min(requestedSize, words.size());
		Map<VlQuestionType, Integer> typeAllocation = allocateQuestionTypes(enabledTypes, totalQuestions, words);

		return generateQuestions(examId, words, typeAllocation);
	}

	private List<VlQuestionType> getEnabledTypes(VlWordGroupEntity group) {
		List<VlQuestionType> types = new ArrayList<>();

		if (Boolean.TRUE.equals(group.getEnableMeaning())) {
			types.add(VlQuestionType.MEANING_MULTIPLE_CHOICE);
		}
		if (Boolean.TRUE.equals(group.getEnableSpelling())) {
			types.add(VlQuestionType.SPELLING_SUBJECTIVE);
		}
		if (Boolean.TRUE.equals(group.getEnableSynonymAntonym())) {
			types.add(VlQuestionType.SYNONYM_MULTIPLE_CHOICE);
			types.add(VlQuestionType.ANTONYM_MULTIPLE_CHOICE);
		}
		if (Boolean.TRUE.equals(group.getEnableContext())) {
			types.add(VlQuestionType.CONTEXT_MULTIPLE_CHOICE);
		}

		return types;
	}

	private Map<VlQuestionType, Integer> allocateQuestionTypes(List<VlQuestionType> enabledTypes,
		int totalQuestions, List<WordWithDetailsDto> words) {

		Map<VlQuestionType, Integer> allocation = enabledTypes.stream()
			.collect(Collectors.toMap(type -> type, type -> 0));

		long wordsWithSynonym = words.stream().filter(WordWithDetailsDto::hasSynonym).count();
		long wordsWithAntonym = words.stream().filter(WordWithDetailsDto::hasAntonym).count();
		long wordsWithContext = words.stream().filter(WordWithDetailsDto::hasExampleSentence).count();

		boolean hasSynonymAntonym = enabledTypes.contains(VlQuestionType.SYNONYM_MULTIPLE_CHOICE)
			|| enabledTypes.contains(VlQuestionType.ANTONYM_MULTIPLE_CHOICE);

		int categoryCount = (int)enabledTypes.stream()
			.filter(t -> t != VlQuestionType.ANTONYM_MULTIPLE_CHOICE)
			.count();

		int baseAllocation = totalQuestions / categoryCount;
		int remainder = totalQuestions % categoryCount;

		List<VlQuestionType> fallbackTypes = enabledTypes.stream()
			.filter(t -> t == VlQuestionType.MEANING_MULTIPLE_CHOICE
				|| t == VlQuestionType.SPELLING_SUBJECTIVE)
			.toList();

		int shortfall = 0;
		int categoryIndex = 0;

		for (VlQuestionType type : enabledTypes) {
			if (type == VlQuestionType.ANTONYM_MULTIPLE_CHOICE) {
				continue;
			}

			int count = baseAllocation + (categoryIndex < remainder ? 1 : 0);
			categoryIndex++;

			if (type == VlQuestionType.SYNONYM_MULTIPLE_CHOICE && hasSynonymAntonym) {
				int synonymCount = count / 2;
				int antonymCount = count - synonymCount;

				int actualSynonym = Math.min(synonymCount, (int)wordsWithSynonym);
				int actualAntonym = Math.min(antonymCount, (int)wordsWithAntonym);

				allocation.put(VlQuestionType.SYNONYM_MULTIPLE_CHOICE, actualSynonym);
				allocation.put(VlQuestionType.ANTONYM_MULTIPLE_CHOICE, actualAntonym);
				shortfall += (synonymCount - actualSynonym) + (antonymCount - actualAntonym);
			} else {
				int maxAvailable = switch (type) {
					case CONTEXT_MULTIPLE_CHOICE -> (int)wordsWithContext;
					default -> words.size();
				};

				int actualCount = Math.min(count, maxAvailable);
				allocation.put(type, actualCount);
				shortfall += (count - actualCount);
			}
		}

		if (shortfall > 0 && !fallbackTypes.isEmpty()) {
			int perFallback = shortfall / fallbackTypes.size();
			int fallbackRemainder = shortfall % fallbackTypes.size();

			for (int i = 0; i < fallbackTypes.size(); i++) {
				VlQuestionType fallbackType = fallbackTypes.get(i);
				int additional = perFallback + (i < fallbackRemainder ? 1 : 0);
				allocation.merge(fallbackType, additional, Integer::sum);
			}
		}

		return allocation;
	}

	private List<VlExamQuestionEntity> generateQuestions(UUID examId, List<WordWithDetailsDto> words,
		Map<VlQuestionType, Integer> typeAllocation) {

		List<VlExamQuestionEntity> questions = new ArrayList<>();
		Set<String> usedAnswers = new HashSet<>();

		List<WordWithDetailsDto> shuffledWords = new ArrayList<>(words);
		Collections.shuffle(shuffledWords);

		Map<VlQuestionType, List<WordWithDetailsDto>> wordPoolByType = createWordPoolByType(shuffledWords);

		for (Map.Entry<VlQuestionType, Integer> entry : typeAllocation.entrySet()) {
			VlQuestionType type = entry.getKey();
			int count = entry.getValue();

			List<WordWithDetailsDto> pool = wordPoolByType.getOrDefault(type, shuffledWords);

			for (WordWithDetailsDto word : pool) {
				if (count <= 0)
					break;

				String answer = getAnswerForType(word, type);
				if (answer == null || usedAnswers.contains(answer)) {
					continue;
				}

				VlExamQuestionEntity question = createQuestion(examId, word, type, 0, shuffledWords);
				if (question != null) {
					questions.add(question);
					usedAnswers.add(answer);
					count--;
				}
			}
		}

		return questions;
	}

	private Map<VlQuestionType, List<WordWithDetailsDto>> createWordPoolByType(List<WordWithDetailsDto> words) {
		return Map.of(
			VlQuestionType.SYNONYM_MULTIPLE_CHOICE,
			words.stream().filter(WordWithDetailsDto::hasSynonym).toList(),
			VlQuestionType.ANTONYM_MULTIPLE_CHOICE,
			words.stream().filter(WordWithDetailsDto::hasAntonym).toList(),
			VlQuestionType.CONTEXT_MULTIPLE_CHOICE,
			words.stream().filter(WordWithDetailsDto::hasExampleSentence).toList()
		);
	}

	private String getAnswerForType(WordWithDetailsDto word, VlQuestionType type) {
		return switch (type) {
			case MEANING_MULTIPLE_CHOICE -> word.getMeanings().isEmpty() ? null : word.getMeanings().get(0);
			case SPELLING_SUBJECTIVE -> word.getEnglishWord();
			case SYNONYM_MULTIPLE_CHOICE -> word.getSynonyms().isEmpty() ? null : word.getSynonyms().get(0);
			case ANTONYM_MULTIPLE_CHOICE -> word.getAntonyms().isEmpty() ? null : word.getAntonyms().get(0);
			case CONTEXT_MULTIPLE_CHOICE ->
				word.getExampleSentences().isEmpty() ? null : word.getExampleSentences().get(0).getBlankAnswer();
		};
	}

	private VlExamQuestionEntity createQuestion(UUID examId, WordWithDetailsDto word,
		VlQuestionType type, int questionNumber, List<WordWithDetailsDto> allWords) {

		String questionText;
		String correctAnswer;
		List<String> options = new ArrayList<>();

		switch (type) {
			case MEANING_MULTIPLE_CHOICE -> {
				questionText = word.getEnglishWord();
				correctAnswer = toJsonArray(word.getMeanings());
				options = generateMeaningOptions(word, allWords, word.getMeanings().getFirst());
			}
			case SPELLING_SUBJECTIVE -> {
				questionText = word.getMeanings().isEmpty() ? "" : word.getMeanings().getFirst();
				List<String> answers = Stream.concat(
					Stream.of(word.getEnglishWord()),
					word.getSynonyms().stream()
				).toList();
				correctAnswer = toJsonArray(answers);
			}
			case SYNONYM_MULTIPLE_CHOICE -> {
				questionText = word.getEnglishWord();
				correctAnswer = word.getSynonyms().getFirst();
				options = generateSynonymOptions(word, allWords, correctAnswer);
			}
			case ANTONYM_MULTIPLE_CHOICE -> {
				questionText = word.getEnglishWord();
				correctAnswer = word.getAntonyms().getFirst();
				options = generateAntonymOptions(word, allWords, correctAnswer);
			}
			case CONTEXT_MULTIPLE_CHOICE -> {
				WordWithDetailsDto.ExampleSentenceDto sentence = word.getExampleSentences().getFirst();
				questionText = sentence.getSentenceWithBlank();
				correctAnswer = sentence.getBlankAnswer();
				options = generateContextOptions(word, allWords, correctAnswer);
			}
			default -> {
				return null;
			}
		}

		return VlExamQuestionEntity.create(
			examId,
			word.getMasterWordId(),
			type,
			questionNumber,
			questionText,
			correctAnswer,
			options.size() > 0 ? options.get(0) : null,
			options.size() > 1 ? options.get(1) : null,
			options.size() > 2 ? options.get(2) : null,
			options.size() > 3 ? options.get(3) : null,
			options.size() > 4 ? options.get(4) : null
		);
	}

	private List<String> generateMeaningOptions(WordWithDetailsDto word, List<WordWithDetailsDto> allWords,
		String correctAnswer) {
		List<String> options = new ArrayList<>();
		options.add(correctAnswer);

		List<String> otherMeanings = allWords.stream()
			.filter(w -> !w.getMasterWordId().equals(word.getMasterWordId()))
			.flatMap(w -> w.getMeanings().stream())
			.filter(m -> !m.equals(correctAnswer))
			.distinct()
			.toList();

		List<String> shuffled = new ArrayList<>(otherMeanings);
		Collections.shuffle(shuffled);

		for (String meaning : shuffled) {
			if (options.size() >= 5)
				break;
			options.add(meaning);
		}

		Collections.shuffle(options);
		return options;
	}

	private List<String> generateSynonymOptions(WordWithDetailsDto word, List<WordWithDetailsDto> allWords,
		String correctAnswer) {
		List<String> options = new ArrayList<>();
		options.add(correctAnswer);

		List<String> otherWords = allWords.stream()
			.filter(w -> !w.getMasterWordId().equals(word.getMasterWordId()))
			.map(WordWithDetailsDto::getEnglishWord)
			.filter(w -> !w.equals(correctAnswer))
			.distinct()
			.toList();

		List<String> shuffled = new ArrayList<>(otherWords);
		Collections.shuffle(shuffled);

		for (String w : shuffled) {
			if (options.size() >= 5)
				break;
			options.add(w);
		}

		Collections.shuffle(options);
		return options;
	}

	private List<String> generateAntonymOptions(WordWithDetailsDto word, List<WordWithDetailsDto> allWords,
		String correctAnswer) {
		return generateSynonymOptions(word, allWords, correctAnswer);
	}

	private List<String> generateContextOptions(WordWithDetailsDto word, List<WordWithDetailsDto> allWords,
		String correctAnswer) {
		List<String> options = new ArrayList<>();
		options.add(correctAnswer);

		List<String> otherWords = allWords.stream()
			.filter(w -> !w.getMasterWordId().equals(word.getMasterWordId()))
			.map(WordWithDetailsDto::getEnglishWord)
			.filter(w -> !w.equals(correctAnswer))
			.distinct()
			.toList();

		List<String> shuffled = new ArrayList<>(otherWords);
		Collections.shuffle(shuffled);

		for (String w : shuffled) {
			if (options.size() >= 5)
				break;
			options.add(w);
		}

		Collections.shuffle(options);
		return options;
	}

	private String toJsonArray(List<String> values) {
		try {
			return objectMapper.writeValueAsString(values);
		} catch (JsonProcessingException e) {
			return "[\"" + String.join("\",\"", values) + "\"]";
		}
	}
}
