package com.voco.voco.app.call.application.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.voco.voco.app.call.application.usecase.dto.out.CallAnalysisRawInfo;
import com.voco.voco.app.call.domain.interfaces.CallAnalysisRawQueryRepository;
import com.voco.voco.app.call.domain.model.CallAnalysisRawEntity;
import com.voco.voco.common.exception.CoreException;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetAdminCallAnalysisRawUseCase")
class GetAdminCallAnalysisRawUseCaseTest {

	@InjectMocks
	private GetAdminCallAnalysisRawUseCase getAdminCallAnalysisRawUseCase;

	@Mock
	private CallAnalysisRawQueryRepository callAnalysisRawQueryRepository;

	@Test
	@DisplayName("통화 분석 Raw 데이터 조회에 성공한다")
	void getAdminCallAnalysisRaw_Success() {
		// given
		Long callId = 1L;

		CallAnalysisRawEntity entity = CallAnalysisRawEntity.create(
			100L,
			List.of(
				new CallAnalysisRawEntity.ConversationRaw("assistant", "Welcome!"),
				new CallAnalysisRawEntity.ConversationRaw("user", "Hello!")
			),
			Map.of("summary", Map.of("overall_completion_score", 100)),
			Map.of("scoring", Map.of("final_score", 95))
		);

		given(callAnalysisRawQueryRepository.findByCallId(callId))
			.willReturn(Optional.of(entity));

		// when
		CallAnalysisRawInfo result = getAdminCallAnalysisRawUseCase.execute(callId);

		// then
		assertThat(result.conversation()).hasSize(2);
		assertThat(result.conversation().get(0).role()).isEqualTo("assistant");
		assertThat(result.conversation().get(1).role()).isEqualTo("user");
		assertThat(result.taskCompletion()).isNotNull();
		assertThat(result.languageAccuracy()).isNotNull();
	}

	@Test
	@DisplayName("분석 데이터가 존재하지 않으면 예외가 발생한다")
	void getAdminCallAnalysisRaw_NotFound() {
		// given
		Long callId = 999L;

		given(callAnalysisRawQueryRepository.findByCallId(callId))
			.willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> getAdminCallAnalysisRawUseCase.execute(callId))
			.isInstanceOf(CoreException.class);
	}
}
