package com.voco.voco.tov.application;

import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.voco.voco.tov.domain.model.VlExamQuestionEntity;
import com.voco.voco.tov.domain.model.enums.VlQuestionType;

class CreateExamUseCaseTest {

	@Nested
	@DisplayName("moveSpellingAwayFromLast 메서드 테스트")
	class MoveSpellingAwayFromLastTest {

		@Test
		@DisplayName("마지막 문제가 스펠링이면 앞의 다른 유형과 교환한다")
		void shouldSwapWhenLastIsSpelling() throws Exception {
			// given
			List<VlExamQuestionEntity> questions = new ArrayList<>();
			questions.add(createQuestion(VlQuestionType.MEANING_MULTIPLE_CHOICE));
			questions.add(createQuestion(VlQuestionType.CONTEXT_MULTIPLE_CHOICE));
			questions.add(createQuestion(VlQuestionType.SPELLING_SUBJECTIVE));

			// when
			invokeMoveSpellingAwayFromLast(questions);

			// then
			assertThat(questions.get(2).getVlQuestionType())
				.isNotEqualTo(VlQuestionType.SPELLING_SUBJECTIVE);
			assertThat(questions.get(0).getVlQuestionType())
				.isEqualTo(VlQuestionType.SPELLING_SUBJECTIVE);
		}

		@Test
		@DisplayName("마지막 문제가 스펠링이 아니면 변경하지 않는다")
		void shouldNotSwapWhenLastIsNotSpelling() throws Exception {
			// given
			List<VlExamQuestionEntity> questions = new ArrayList<>();
			questions.add(createQuestion(VlQuestionType.SPELLING_SUBJECTIVE));
			questions.add(createQuestion(VlQuestionType.SPELLING_SUBJECTIVE));
			questions.add(createQuestion(VlQuestionType.MEANING_MULTIPLE_CHOICE));

			// when
			invokeMoveSpellingAwayFromLast(questions);

			// then
			assertThat(questions.get(2).getVlQuestionType())
				.isEqualTo(VlQuestionType.MEANING_MULTIPLE_CHOICE);
		}

		@Test
		@DisplayName("모든 문제가 스펠링이면 변경하지 않는다")
		void shouldNotSwapWhenAllAreSpelling() throws Exception {
			// given
			List<VlExamQuestionEntity> questions = new ArrayList<>();
			questions.add(createQuestion(VlQuestionType.SPELLING_SUBJECTIVE));
			questions.add(createQuestion(VlQuestionType.SPELLING_SUBJECTIVE));
			questions.add(createQuestion(VlQuestionType.SPELLING_SUBJECTIVE));

			// when
			invokeMoveSpellingAwayFromLast(questions);

			// then
			assertThat(questions).allMatch(q ->
				q.getVlQuestionType() == VlQuestionType.SPELLING_SUBJECTIVE);
		}

		@Test
		@DisplayName("문제가 1개이면 변경하지 않는다")
		void shouldNotSwapWhenOnlyOneQuestion() throws Exception {
			// given
			List<VlExamQuestionEntity> questions = new ArrayList<>();
			questions.add(createQuestion(VlQuestionType.SPELLING_SUBJECTIVE));

			// when
			invokeMoveSpellingAwayFromLast(questions);

			// then
			assertThat(questions).hasSize(1);
			assertThat(questions.get(0).getVlQuestionType())
				.isEqualTo(VlQuestionType.SPELLING_SUBJECTIVE);
		}

		@Test
		@DisplayName("빈 리스트면 예외가 발생하지 않는다")
		void shouldNotThrowWhenEmpty() throws Exception {
			// given
			List<VlExamQuestionEntity> questions = new ArrayList<>();

			// when & then
			assertThatCode(() -> invokeMoveSpellingAwayFromLast(questions))
				.doesNotThrowAnyException();
		}

		private VlExamQuestionEntity createQuestion(VlQuestionType type) {
			return VlExamQuestionEntity.create(
				UUID.randomUUID(),
				UUID.randomUUID(),
				type,
				1,
				"question",
				"answer",
				null, null, null, null, null
			);
		}

		private void invokeMoveSpellingAwayFromLast(List<VlExamQuestionEntity> questions) throws Exception {
			CreateExamUseCase useCase = new CreateExamUseCase(null, null, null, null, null);
			Method method = CreateExamUseCase.class.getDeclaredMethod(
				"moveSpellingAwayFromLast", List.class);
			method.setAccessible(true);
			method.invoke(useCase, questions);
		}
	}
}
