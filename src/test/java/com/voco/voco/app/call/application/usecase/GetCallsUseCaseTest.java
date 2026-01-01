package com.voco.voco.app.call.application.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.voco.voco.app.call.application.usecase.dto.out.CallHistoryInfo;
import com.voco.voco.app.call.domain.enums.CallAnalysisGrade;
import com.voco.voco.app.call.domain.interfaces.CallQueryRepository;
import com.voco.voco.app.call.domain.interfaces.dto.out.CallHistoryDomainDto;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetCallsUseCase")
class GetCallsUseCaseTest {

	@InjectMocks
	private GetCallsUseCase getCallsUseCase;

	@Mock
	private CallQueryRepository callQueryRepository;

	@Test
	@DisplayName("통화 내역 목록 조회에 성공한다")
	void getCalls_Success() {
		// given
		Long memberId = 1L;
		int page = 0;
		int size = 10;

		List<CallHistoryDomainDto> content = List.of(
			new CallHistoryDomainDto(1L, LocalDateTime.now(), "Cafe Order", CallAnalysisGrade.EXCELLENT),
			new CallHistoryDomainDto(2L, LocalDateTime.now(), "Hotel Booking", CallAnalysisGrade.GOOD)
		);
		Page<CallHistoryDomainDto> domainPage = new PageImpl<>(content);
		given(callQueryRepository.findCallHistoryByMemberId(eq(memberId), any(Pageable.class)))
			.willReturn(domainPage);

		// when
		Page<CallHistoryInfo> result = getCallsUseCase.execute(memberId, page, size);

		// then
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getContent().get(0).scenarioName()).isEqualTo("Cafe Order");
		assertThat(result.getContent().get(1).scenarioName()).isEqualTo("Hotel Booking");
	}

	@Test
	@DisplayName("통화 내역이 없으면 빈 페이지를 반환한다")
	void getCalls_Empty() {
		// given
		Long memberId = 1L;
		int page = 0;
		int size = 10;

		Page<CallHistoryDomainDto> emptyPage = new PageImpl<>(List.of());
		given(callQueryRepository.findCallHistoryByMemberId(eq(memberId), any(Pageable.class)))
			.willReturn(emptyPage);

		// when
		Page<CallHistoryInfo> result = getCallsUseCase.execute(memberId, page, size);

		// then
		assertThat(result.getContent()).isEmpty();
		assertThat(result.getTotalElements()).isZero();
	}
}
