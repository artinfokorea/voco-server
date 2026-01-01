package com.voco.voco.app.call.application.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.voco.voco.app.call.application.usecase.dto.in.CreateCallAnalysisUseCaseDto;
import com.voco.voco.app.call.domain.interfaces.CallAnalysisCommandRepository;
import com.voco.voco.app.call.domain.interfaces.CallAnalysisRawCommandRepository;
import com.voco.voco.app.call.domain.interfaces.CallQueryRepository;
import com.voco.voco.app.call.domain.model.CallAnalysisEntity;
import com.voco.voco.app.call.domain.model.CallAnalysisRawEntity;
import com.voco.voco.app.call.domain.model.CallEntity;

@ExtendWith(MockitoExtension.class)
class CreateCallAnalysisUseCaseTest {

	@InjectMocks
	private CreateCallAnalysisUseCase createCallAnalysisUseCase;

	@Mock
	private CallQueryRepository callQueryRepository;

	@Mock
	private CallAnalysisCommandRepository callAnalysisCommandRepository;

	@Mock
	private CallAnalysisRawCommandRepository callAnalysisRawCommandRepository;

	private static final Long CALL_ID = 1L;
	private static final Long ANALYSIS_ID = 100L;
	private static final Long RAW_ID = 200L;

	private CreateCallAnalysisUseCaseDto createValidDto() {
		List<CreateCallAnalysisUseCaseDto.ConversationDto> conversation = List.of(
			new CreateCallAnalysisUseCaseDto.ConversationDto("assistant", "Welcome! What would you like to order?"),
			new CreateCallAnalysisUseCaseDto.ConversationDto("user", "I wanting coffee."),
			new CreateCallAnalysisUseCaseDto.ConversationDto("assistant", "What size would you like?"),
			new CreateCallAnalysisUseCaseDto.ConversationDto("user", "Medium, please.")
		);

		CreateCallAnalysisUseCaseDto.TaskSummaryDto taskSummary = new CreateCallAnalysisUseCaseDto.TaskSummaryDto(
			100,
			"completed",
			"시나리오가 완벽하게 완료되었습니다."
		);

		CreateCallAnalysisUseCaseDto.TaskCompletionDto taskCompletion = new CreateCallAnalysisUseCaseDto.TaskCompletionDto(
			taskSummary,
			null,
			null,
			null,
			null
		);

		List<CreateCallAnalysisUseCaseDto.ErrorDto> errors = List.of(
			new CreateCallAnalysisUseCaseDto.ErrorDto(
				2,
				"grammar",
				"verb_form",
				"wanting",
				"want",
				"현재 진행형 대신 단순 현재형을 사용해야 합니다.",
				"major",
				"I wanting coffee."
			)
		);

		CreateCallAnalysisUseCaseDto.ScoringDto scoring = new CreateCallAnalysisUseCaseDto.ScoringDto(
			100,
			"excellent",
			100,
			5,
			10
		);

		CreateCallAnalysisUseCaseDto.FeedbackDto feedback = new CreateCallAnalysisUseCaseDto.FeedbackDto(
			List.of("대부분의 문장을 정확하게 표현함"),
			List.of("동사 형태 사용에 주의 필요"),
			List.of("현재형 vs 현재진행형 구분"),
			List.of("상태 동사는 -ing 형태를 쓰지 않습니다")
		);

		CreateCallAnalysisUseCaseDto.LanguageAccuracyDto languageAccuracy = new CreateCallAnalysisUseCaseDto.LanguageAccuracyDto(
			scoring,
			feedback,
			"4개의 발화 중 1개의 문법 오류가 발견되었습니다.",
			errors,
			4,
			3,
			null,
			null
		);

		return new CreateCallAnalysisUseCaseDto(conversation, taskCompletion, languageAccuracy);
	}

	private CallEntity createCall() {
		return CallEntity.create(1L, 1L, "room-1-1-beginner-abc12345");
	}

	@Nested
	@DisplayName("execute 메서드")
	class Execute {

		@Test
		@DisplayName("통화 분석 결과 저장에 성공한다")
		void createCallAnalysis_Success() {
			// given
			CreateCallAnalysisUseCaseDto dto = createValidDto();
			CallEntity call = createCall();
			given(callQueryRepository.findByIdOrThrow(CALL_ID)).willReturn(call);
			given(callAnalysisCommandRepository.save(any(CallAnalysisEntity.class))).willReturn(ANALYSIS_ID);
			given(callAnalysisRawCommandRepository.save(any(CallAnalysisRawEntity.class))).willReturn(RAW_ID);

			// when
			Long result = createCallAnalysisUseCase.execute(CALL_ID, dto);

			// then
			assertThat(result).isEqualTo(ANALYSIS_ID);
			then(callQueryRepository).should().findByIdOrThrow(CALL_ID);
			then(callAnalysisCommandRepository).should().save(any(CallAnalysisEntity.class));
			then(callAnalysisRawCommandRepository).should().save(any(CallAnalysisRawEntity.class));
		}

		@Test
		@DisplayName("CallEntity의 analysisId가 업데이트된다")
		void createCallAnalysis_UpdatesCallEntityAnalysisId() {
			// given
			CreateCallAnalysisUseCaseDto dto = createValidDto();
			CallEntity call = createCall();
			given(callQueryRepository.findByIdOrThrow(CALL_ID)).willReturn(call);
			given(callAnalysisCommandRepository.save(any(CallAnalysisEntity.class))).willReturn(ANALYSIS_ID);
			given(callAnalysisRawCommandRepository.save(any(CallAnalysisRawEntity.class))).willReturn(RAW_ID);

			// when
			createCallAnalysisUseCase.execute(CALL_ID, dto);

			// then
			assertThat(call.getAnalysisId()).isEqualTo(ANALYSIS_ID);
		}

		@Test
		@DisplayName("CallAnalysisEntity가 올바르게 생성된다")
		void createCallAnalysis_EntityCreatedCorrectly() {
			// given
			CreateCallAnalysisUseCaseDto dto = createValidDto();
			CallEntity call = createCall();
			given(callQueryRepository.findByIdOrThrow(CALL_ID)).willReturn(call);
			ArgumentCaptor<CallAnalysisEntity> captor = ArgumentCaptor.forClass(CallAnalysisEntity.class);
			given(callAnalysisCommandRepository.save(captor.capture())).willReturn(ANALYSIS_ID);
			given(callAnalysisRawCommandRepository.save(any(CallAnalysisRawEntity.class))).willReturn(RAW_ID);

			// when
			createCallAnalysisUseCase.execute(CALL_ID, dto);

			// then
			CallAnalysisEntity savedEntity = captor.getValue();
			assertThat(savedEntity.getConversation()).hasSize(4);
			assertThat(savedEntity.getTaskCompletionScore()).isNotNull();
			assertThat(savedEntity.getLanguageAccuracyScore()).isNotNull();
			assertThat(savedEntity.getFeedback()).isNotNull();
		}

		@Test
		@DisplayName("CallAnalysisRawEntity가 올바르게 생성된다")
		void createCallAnalysis_RawEntityCreatedCorrectly() {
			// given
			CreateCallAnalysisUseCaseDto dto = createValidDto();
			CallEntity call = createCall();
			given(callQueryRepository.findByIdOrThrow(CALL_ID)).willReturn(call);
			given(callAnalysisCommandRepository.save(any(CallAnalysisEntity.class))).willReturn(ANALYSIS_ID);
			ArgumentCaptor<CallAnalysisRawEntity> captor = ArgumentCaptor.forClass(CallAnalysisRawEntity.class);
			given(callAnalysisRawCommandRepository.save(captor.capture())).willReturn(RAW_ID);

			// when
			createCallAnalysisUseCase.execute(CALL_ID, dto);

			// then
			CallAnalysisRawEntity savedRawEntity = captor.getValue();
			assertThat(savedRawEntity.getAnalysisId()).isEqualTo(ANALYSIS_ID);
			assertThat(savedRawEntity.getConversation()).hasSize(4);
			assertThat(savedRawEntity.getTaskCompletion()).isNotNull();
			assertThat(savedRawEntity.getLanguageAccuracy()).isNotNull();
		}

		@Test
		@DisplayName("Conversation에 에러가 올바르게 매핑된다")
		void createCallAnalysis_ConversationErrorMappedCorrectly() {
			// given
			CreateCallAnalysisUseCaseDto dto = createValidDto();
			CallEntity call = createCall();
			given(callQueryRepository.findByIdOrThrow(CALL_ID)).willReturn(call);
			ArgumentCaptor<CallAnalysisEntity> captor = ArgumentCaptor.forClass(CallAnalysisEntity.class);
			given(callAnalysisCommandRepository.save(captor.capture())).willReturn(ANALYSIS_ID);
			given(callAnalysisRawCommandRepository.save(any(CallAnalysisRawEntity.class))).willReturn(RAW_ID);

			// when
			createCallAnalysisUseCase.execute(CALL_ID, dto);

			// then
			CallAnalysisEntity savedEntity = captor.getValue();
			CallAnalysisEntity.ConversationEntry secondEntry = savedEntity.getConversation().get(1);
			assertThat(secondEntry.role()).isEqualTo("user");
			assertThat(secondEntry.content()).isEqualTo("I wanting coffee.");
			assertThat(secondEntry.error()).isNotNull();
			assertThat(secondEntry.error().errorType()).isEqualTo("grammar");
			assertThat(secondEntry.error().correction()).isEqualTo("want");
		}

		@Test
		@DisplayName("점수가 올바르게 계산된다")
		void createCallAnalysis_ScoreCalculatedCorrectly() {
			// given
			CreateCallAnalysisUseCaseDto dto = createValidDto();
			CallEntity call = createCall();
			given(callQueryRepository.findByIdOrThrow(CALL_ID)).willReturn(call);
			ArgumentCaptor<CallAnalysisEntity> captor = ArgumentCaptor.forClass(CallAnalysisEntity.class);
			given(callAnalysisCommandRepository.save(captor.capture())).willReturn(ANALYSIS_ID);
			given(callAnalysisRawCommandRepository.save(any(CallAnalysisRawEntity.class))).willReturn(RAW_ID);

			// when
			createCallAnalysisUseCase.execute(CALL_ID, dto);

			// then
			CallAnalysisEntity savedEntity = captor.getValue();
			assertThat(savedEntity.getTaskCompletionScore()).isEqualTo(100);
			assertThat(savedEntity.getLanguageAccuracyScore()).isEqualTo(100);
			assertThat(savedEntity.getOverallScore()).isEqualTo(100);
		}
	}
}
