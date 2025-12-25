package com.voco.voco.tov.domain.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.voco.voco.tov.domain.model.VlExamQuestionEntity;
import com.voco.voco.tov.domain.model.VlWordGroupEntity;
import com.voco.voco.tov.domain.model.enums.VlPassStatus;
import com.voco.voco.tov.domain.model.enums.VlQuestionType;

@Service
public class ExamGradingService {

	public VlPassStatus calculatePassStatus(List<VlExamQuestionEntity> questions, VlWordGroupEntity wordGroup) {
		if (questions.isEmpty()) {
			return VlPassStatus.FAIL;
		}

		int totalCorrect = countCorrectAnswers(questions);
		int totalPercentage = calculatePercentage(totalCorrect, questions.size());

		if (totalPercentage < wordGroup.getPassPercentageTotal()) {
			return VlPassStatus.FAIL;
		}

		Map<VlQuestionType, List<VlExamQuestionEntity>> questionsByType = questions.stream()
			.collect(Collectors.groupingBy(VlExamQuestionEntity::getVlQuestionType));

		if (wordGroup.getEnableMeaning() && !checkTypePercentage(
			questionsByType.get(VlQuestionType.MEANING_MULTIPLE_CHOICE),
			wordGroup.getPassPercentageMeaning())) {
			return VlPassStatus.FAIL;
		}

		if (wordGroup.getEnableSpelling() && !checkTypePercentage(
			questionsByType.get(VlQuestionType.SPELLING_SUBJECTIVE),
			wordGroup.getPassPercentageSpelling())) {
			return VlPassStatus.FAIL;
		}

		if (wordGroup.getEnableSynonymAntonym()) {
			List<VlExamQuestionEntity> synonymAntonymQuestions = getSynonymAntonymQuestions(questionsByType);
			if (!checkTypePercentage(synonymAntonymQuestions, wordGroup.getPassPercentageSynonymAntonym())) {
				return VlPassStatus.FAIL;
			}
		}

		if (wordGroup.getEnableContext() && !checkTypePercentage(
			questionsByType.get(VlQuestionType.CONTEXT_MULTIPLE_CHOICE),
			wordGroup.getPassPercentageContext())) {
			return VlPassStatus.FAIL;
		}

		return VlPassStatus.PASS;
	}

	public int countCorrectAnswers(List<VlExamQuestionEntity> questions) {
		return (int) questions.stream()
			.filter(this::isCorrectAnswer)
			.count();
	}

	public void gradeQuestions(List<VlExamQuestionEntity> questions) {
		for (VlExamQuestionEntity question : questions) {
			if (question.getIsCorrect() == null) {
				boolean isCorrect = isCorrectAnswer(question);
				question.markCorrect(isCorrect);
			}
		}
	}

	private boolean isCorrectAnswer(VlExamQuestionEntity question) {
		if (question.getIsCorrect() != null) {
			return question.getIsCorrect();
		}

		String userAnswer = question.getUserAnswer();
		String correctAnswer = question.getCorrectAnswer();

		if (userAnswer == null || correctAnswer == null) {
			return false;
		}

		if (isArrayFormat(correctAnswer)) {
			return matchesAnyInArray(userAnswer, correctAnswer, question.getVlQuestionType());
		}

		return matchesAnswer(userAnswer, correctAnswer, question.getVlQuestionType());
	}

	private boolean matchesAnswer(String userAnswer, String correctAnswer, VlQuestionType questionType) {
		String trimmedUserAnswer = userAnswer.trim();

		if (correctAnswer.equalsIgnoreCase(trimmedUserAnswer)) {
			return true;
		}

		if (questionType == VlQuestionType.SPELLING_SUBJECTIVE) {
			String withoutParentheses = removeParenthesesContent(correctAnswer);
			if (withoutParentheses.equalsIgnoreCase(trimmedUserAnswer)) {
				return true;
			}
		}

		return false;
	}

	private String removeParenthesesContent(String text) {
		return text.replaceAll("\\([^)]*\\)", "");
	}

	private boolean isArrayFormat(String value) {
		return value.startsWith("[") && value.endsWith("]");
	}

	private boolean matchesAnyInArray(String userAnswer, String correctAnswerArray, VlQuestionType questionType) {
		String content = correctAnswerArray.substring(1, correctAnswerArray.length() - 1);

		return Arrays.stream(content.split(","))
			.map(s -> s.trim().replaceAll("^\"|\"$", ""))
			.anyMatch(answer -> matchesAnswer(userAnswer, answer, questionType));
	}

	private boolean checkTypePercentage(List<VlExamQuestionEntity> questions, int requiredPercentage) {
		if (questions == null || questions.isEmpty()) {
			return true;
		}
		int correct = countCorrectAnswers(questions);
		int percentage = calculatePercentage(correct, questions.size());
		return percentage >= requiredPercentage;
	}

	private int calculatePercentage(int correct, int total) {
		if (total == 0) {
			return 0;
		}
		return (correct * 100) / total;
	}

	private List<VlExamQuestionEntity> getSynonymAntonymQuestions(
		Map<VlQuestionType, List<VlExamQuestionEntity>> questionsByType) {
		List<VlExamQuestionEntity> synonymQuestions = questionsByType.get(VlQuestionType.SYNONYM_MULTIPLE_CHOICE);
		List<VlExamQuestionEntity> antonymQuestions = questionsByType.get(VlQuestionType.ANTONYM_MULTIPLE_CHOICE);

		if (synonymQuestions == null && antonymQuestions == null) {
			return List.of();
		}
		if (synonymQuestions == null) {
			return antonymQuestions;
		}
		if (antonymQuestions == null) {
			return synonymQuestions;
		}

		return java.util.stream.Stream.concat(synonymQuestions.stream(), antonymQuestions.stream())
			.toList();
	}
}
