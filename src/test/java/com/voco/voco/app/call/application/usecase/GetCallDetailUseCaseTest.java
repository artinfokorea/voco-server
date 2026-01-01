package com.voco.voco.app.call.application.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.voco.voco.app.call.application.usecase.dto.out.CallDetailInfo;
import com.voco.voco.app.call.domain.interfaces.CallQueryRepository;
import com.voco.voco.app.call.domain.interfaces.dto.out.CallDetailDomainDto;
import com.voco.voco.app.scenario.domain.model.Level;
import com.voco.voco.common.exception.CoreException;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetCallDetailUseCase")
class GetCallDetailUseCaseTest {

	@InjectMocks
	private GetCallDetailUseCase getCallDetailUseCase;

	@Mock
	private CallQueryRepository callQueryRepository;

	@Test
	@DisplayName("통화 상세 조회에 성공한다")
	void getCallDetail_Success() {
		// given
		Long callId = 1L;
		Long memberId = 1L;

		CallDetailDomainDto domainDto = new CallDetailDomainDto(
			callId,
			LocalDateTime.now(),
			"Cafe Order",
			Level.BEGINNER,
			null
		);
		given(callQueryRepository.findCallDetailByIdAndMemberId(callId, memberId))
			.willReturn(Optional.of(domainDto));

		// when
		CallDetailInfo result = getCallDetailUseCase.execute(callId, memberId);

		// then
		assertThat(result.scenarioName()).isEqualTo("Cafe Order");
		assertThat(result.scenarioLevel()).isEqualTo(Level.BEGINNER);
	}

	@Test
	@DisplayName("통화가 존재하지 않으면 예외가 발생한다")
	void getCallDetail_NotFound() {
		// given
		Long callId = 999L;
		Long memberId = 1L;

		given(callQueryRepository.findCallDetailByIdAndMemberId(callId, memberId))
			.willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> getCallDetailUseCase.execute(callId, memberId))
			.isInstanceOf(CoreException.class);
	}

	@Test
	@DisplayName("다른 사용자의 통화는 조회할 수 없다")
	void getCallDetail_OtherUserCall_NotFound() {
		// given
		Long callId = 1L;
		Long memberId = 999L;

		given(callQueryRepository.findCallDetailByIdAndMemberId(callId, memberId))
			.willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> getCallDetailUseCase.execute(callId, memberId))
			.isInstanceOf(CoreException.class);
	}
}
