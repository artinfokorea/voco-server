package com.voco.voco.app.call.application.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Map;

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
import com.voco.voco.app.call.domain.interfaces.CallQueryRepository;
import com.voco.voco.app.call.domain.model.CallAnalysisEntity;
import com.voco.voco.app.call.domain.model.CallEntity;

@ExtendWith(MockitoExtension.class)
class CreateCallAnalysisUseCaseTest {

	@InjectMocks
	private CreateCallAnalysisUseCase createCallAnalysisUseCase;

	@Mock
	private CallQueryRepository callQueryRepository;

	@Mock
	private CallAnalysisCommandRepository callAnalysisCommandRepository;

	private static final Long CALL_ID = 1L;
	private static final Long ANALYSIS_ID = 100L;

	private CreateCallAnalysisUseCaseDto createValidDto() {
		return new CreateCallAnalysisUseCaseDto(
			Map.of("slot_analysis", Map.of("total_slots", 3, "filled_slots", 3)),
			4,
			3,
			List.of(
				Map.of("turn", 2, "original_utterance", "I wanting coffee.", "is_correct", false),
				Map.of("turn", 4, "original_utterance", "Medium, please.", "is_correct", true)
			),
			List.of(
				Map.of("turn", 2, "error_type", "grammar", "severity", "major")
			),
			Map.of("total_errors", 1, "by_type", Map.of("grammar", 1)),
			new CreateCallAnalysisUseCaseDto.ScoringDto(
				100, 5, 0, 5, 0, 10, 100, "excellent"
			),
			new CreateCallAnalysisUseCaseDto.FeedbackDto(
				List.of("대부분의 문장을 정확하게 표현함"),
				List.of("동사 형태 사용에 주의 필요"),
				List.of("현재형 vs 현재진행형 구분"),
				List.of("'want', 'like' 같은 상태 동사는 -ing 형태를 쓰지 않습니다")
			),
			"4개의 발화 중 1개의 문법 오류가 발견되었습니다."
		);
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

			// when
			Long result = createCallAnalysisUseCase.execute(CALL_ID, dto);

			// then
			assertThat(result).isEqualTo(ANALYSIS_ID);
			then(callQueryRepository).should().findByIdOrThrow(CALL_ID);
			then(callAnalysisCommandRepository).should().save(any(CallAnalysisEntity.class));
		}

		@Test
		@DisplayName("CallEntity의 analysisId가 업데이트된다")
		void createCallAnalysis_UpdatesCallEntityAnalysisId() {
			// given
			CreateCallAnalysisUseCaseDto dto = createValidDto();
			CallEntity call = createCall();
			given(callQueryRepository.findByIdOrThrow(CALL_ID)).willReturn(call);
			given(callAnalysisCommandRepository.save(any(CallAnalysisEntity.class))).willReturn(ANALYSIS_ID);

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

			// when
			createCallAnalysisUseCase.execute(CALL_ID, dto);

			// then
			CallAnalysisEntity savedEntity = captor.getValue();
			assertThat(savedEntity.getTotalUserUtterances()).isEqualTo(4);
			assertThat(savedEntity.getCorrectUtterances()).isEqualTo(3);
			assertThat(savedEntity.getBriefDescription()).isEqualTo("4개의 발화 중 1개의 문법 오류가 발견되었습니다.");
		}

		@Test
		@DisplayName("Scoring 정보가 올바르게 저장된다")
		void createCallAnalysis_ScoringCorrect() {
			// given
			CreateCallAnalysisUseCaseDto dto = createValidDto();
			CallEntity call = createCall();
			given(callQueryRepository.findByIdOrThrow(CALL_ID)).willReturn(call);
			ArgumentCaptor<CallAnalysisEntity> captor = ArgumentCaptor.forClass(CallAnalysisEntity.class);
			given(callAnalysisCommandRepository.save(captor.capture())).willReturn(ANALYSIS_ID);

			// when
			createCallAnalysisUseCase.execute(CALL_ID, dto);

			// then
			CallAnalysisEntity savedEntity = captor.getValue();
			assertThat(savedEntity.getScoring().getBaseScore()).isEqualTo(100);
			assertThat(savedEntity.getScoring().getFinalScore()).isEqualTo(100);
			assertThat(savedEntity.getScoring().getRating()).isEqualTo("excellent");
		}

		@Test
		@DisplayName("Feedback 정보가 올바르게 저장된다")
		void createCallAnalysis_FeedbackCorrect() {
			// given
			CreateCallAnalysisUseCaseDto dto = createValidDto();
			CallEntity call = createCall();
			given(callQueryRepository.findByIdOrThrow(CALL_ID)).willReturn(call);
			ArgumentCaptor<CallAnalysisEntity> captor = ArgumentCaptor.forClass(CallAnalysisEntity.class);
			given(callAnalysisCommandRepository.save(captor.capture())).willReturn(ANALYSIS_ID);

			// when
			createCallAnalysisUseCase.execute(CALL_ID, dto);

			// then
			CallAnalysisEntity savedEntity = captor.getValue();
			assertThat(savedEntity.getFeedback().getStrengths()).containsExactly("대부분의 문장을 정확하게 표현함");
			assertThat(savedEntity.getFeedback().getImprovements()).containsExactly("동사 형태 사용에 주의 필요");
		}
	}
}
