package com.voco.voco.app.call.application.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.voco.voco.app.call.application.usecase.dto.out.CallAnalysisInfo;
import com.voco.voco.app.call.domain.interfaces.CallAnalysisQueryRepository;
import com.voco.voco.app.call.domain.interfaces.CallQueryRepository;
import com.voco.voco.app.call.domain.model.CallAnalysisEntity;
import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;

@ExtendWith(MockitoExtension.class)
class GetCallAnalysisUseCaseTest {

	@InjectMocks
	private GetCallAnalysisUseCase getCallAnalysisUseCase;

	@Mock
	private CallAnalysisQueryRepository callAnalysisQueryRepository;

	@Mock
	private CallQueryRepository callQueryRepository;

	private static final Long MEMBER_ID = 1L;

	@Nested
	@DisplayName("execute 메서드")
	class Execute {

		@Test
		@DisplayName("분석 결과 조회에 성공한다")
		void getCallAnalysis_Success() {
			// given
			Long analysisId = 1L;
			CallAnalysisEntity entity = createAnalysisEntity(analysisId);

			given(callQueryRepository.existsByAnalysisIdAndMemberId(analysisId, MEMBER_ID))
				.willReturn(true);
			given(callAnalysisQueryRepository.findById(analysisId))
				.willReturn(Optional.of(entity));

			// when
			CallAnalysisInfo result = getCallAnalysisUseCase.execute(analysisId, MEMBER_ID);

			// then
			assertThat(result.id()).isEqualTo(analysisId);
			assertThat(result.content()).isEqualTo("{\"test\": \"content\"}");
			assertThat(result.score()).isEqualTo(85);
			assertThat(result.summary()).isEqualTo("테스트 요약입니다.");
		}

		@Test
		@DisplayName("다른 회원의 분석 결과 조회 시 예외가 발생한다")
		void getCallAnalysis_Forbidden_ThrowsException() {
			// given
			Long analysisId = 1L;
			Long otherMemberId = 2L;

			given(callQueryRepository.existsByAnalysisIdAndMemberId(analysisId, otherMemberId))
				.willReturn(false);

			// when & then
			assertThatThrownBy(() -> getCallAnalysisUseCase.execute(analysisId, otherMemberId))
				.isInstanceOf(CoreException.class)
				.hasFieldOrPropertyWithValue("errorType", ApiErrorType.CALL_ANALYSIS_FORBIDDEN);
		}

		@Test
		@DisplayName("분석 결과가 없으면 예외가 발생한다")
		void getCallAnalysis_NotFound_ThrowsException() {
			// given
			Long analysisId = 999L;

			given(callQueryRepository.existsByAnalysisIdAndMemberId(analysisId, MEMBER_ID))
				.willReturn(true);
			given(callAnalysisQueryRepository.findById(analysisId))
				.willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> getCallAnalysisUseCase.execute(analysisId, MEMBER_ID))
				.isInstanceOf(CoreException.class)
				.hasFieldOrPropertyWithValue("errorType", ApiErrorType.CALL_ANALYSIS_NOT_FOUND);
		}
	}

	private CallAnalysisEntity createAnalysisEntity(Long id) {
		CallAnalysisEntity entity = CallAnalysisEntity.create("{\"test\": \"content\"}", 85, "테스트 요약입니다.");
		try {
			java.lang.reflect.Field idField = CallAnalysisEntity.class.getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(entity, id);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return entity;
	}
}
