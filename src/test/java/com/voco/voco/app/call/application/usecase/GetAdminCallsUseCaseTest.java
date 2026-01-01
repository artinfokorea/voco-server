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

import com.voco.voco.app.call.application.usecase.dto.out.AdminCallHistoryInfo;
import com.voco.voco.app.call.domain.interfaces.CallQueryRepository;
import com.voco.voco.app.call.domain.interfaces.dto.out.AdminCallHistoryDomainDto;
import com.voco.voco.app.scenario.domain.model.Level;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetAdminCallsUseCase")
class GetAdminCallsUseCaseTest {

	@InjectMocks
	private GetAdminCallsUseCase getAdminCallsUseCase;

	@Mock
	private CallQueryRepository callQueryRepository;

	@Test
	@DisplayName("관리자 통화 내역 목록 조회에 성공한다")
	void getAdminCalls_Success() {
		// given
		int page = 0;
		int size = 10;

		List<AdminCallHistoryDomainDto> content = List.of(
			new AdminCallHistoryDomainDto(1L, "Cafe Order", "홍길동", 1L, Level.BEGINNER, LocalDateTime.now(), null),
			new AdminCallHistoryDomainDto(2L, "Hotel Booking", "김철수", 2L, Level.INTERMEDIATE, LocalDateTime.now(), null)
		);
		Page<AdminCallHistoryDomainDto> domainPage = new PageImpl<>(content);
		given(callQueryRepository.findAllCallHistory(any(Pageable.class)))
			.willReturn(domainPage);

		// when
		Page<AdminCallHistoryInfo> result = getAdminCallsUseCase.execute(page, size);

		// then
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getContent().get(0).scenarioName()).isEqualTo("Cafe Order");
		assertThat(result.getContent().get(0).memberName()).isEqualTo("홍길동");
		assertThat(result.getContent().get(1).scenarioName()).isEqualTo("Hotel Booking");
		assertThat(result.getContent().get(1).memberName()).isEqualTo("김철수");
	}

	@Test
	@DisplayName("통화 내역이 없으면 빈 페이지를 반환한다")
	void getAdminCalls_Empty() {
		// given
		int page = 0;
		int size = 10;

		Page<AdminCallHistoryDomainDto> emptyPage = new PageImpl<>(List.of());
		given(callQueryRepository.findAllCallHistory(any(Pageable.class)))
			.willReturn(emptyPage);

		// when
		Page<AdminCallHistoryInfo> result = getAdminCallsUseCase.execute(page, size);

		// then
		assertThat(result.getContent()).isEmpty();
		assertThat(result.getTotalElements()).isZero();
	}
}
