package com.voco.voco.app.call.application.usecase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.voco.voco.app.call.application.usecase.dto.out.CallInfo;
import com.voco.voco.app.call.domain.interfaces.CallQueryRepository;
import com.voco.voco.app.call.domain.interfaces.dto.CallWithScenarioDto;
import com.voco.voco.app.scenario.domain.model.Category;
import com.voco.voco.app.scenario.domain.model.Level;

@ExtendWith(MockitoExtension.class)
class GetCallsUseCaseTest {

	@InjectMocks
	private GetCallsUseCase getCallsUseCase;

	@Mock
	private CallQueryRepository callQueryRepository;

	private static final Long MEMBER_ID = 1L;

	@Nested
	@DisplayName("execute 메서드")
	class Execute {

		@Test
		@DisplayName("통화 내역 조회에 성공한다")
		void getCalls_Success() {
			// given
			Pageable pageable = PageRequest.of(0, 10);
			CallWithScenarioDto dto1 = new CallWithScenarioDto(
				1L, 1L, "카페 주문", "카페에서 음료 주문하기",
				Level.BEGINNER, Category.DAILY, null, LocalDateTime.now()
			);
			CallWithScenarioDto dto2 = new CallWithScenarioDto(
				2L, 2L, "비즈니스 미팅", "회의 일정 조율하기",
				Level.INTERMEDIATE, Category.BUSINESS, 100L, LocalDateTime.now()
			);

			Page<CallWithScenarioDto> page = new PageImpl<>(List.of(dto1, dto2), pageable, 2);
			given(callQueryRepository.findAllByMemberIdWithScenario(MEMBER_ID, pageable))
				.willReturn(page);

			// when
			Page<CallInfo> result = getCallsUseCase.execute(MEMBER_ID, pageable);

			// then
			assertThat(result.getContent()).hasSize(2);
			assertThat(result.getContent().get(0).callId()).isEqualTo(1L);
			assertThat(result.getContent().get(0).scenarioTitle()).isEqualTo("카페 주문");
			assertThat(result.getContent().get(0).scenarioLevel()).isEqualTo(Level.BEGINNER);
			assertThat(result.getContent().get(1).callId()).isEqualTo(2L);
			assertThat(result.getContent().get(1).analysisId()).isEqualTo(100L);
		}

		@Test
		@DisplayName("통화 내역이 없으면 빈 페이지를 반환한다")
		void getCalls_Empty_ReturnsEmptyPage() {
			// given
			Pageable pageable = PageRequest.of(0, 10);
			Page<CallWithScenarioDto> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

			given(callQueryRepository.findAllByMemberIdWithScenario(MEMBER_ID, pageable))
				.willReturn(emptyPage);

			// when
			Page<CallInfo> result = getCallsUseCase.execute(MEMBER_ID, pageable);

			// then
			assertThat(result.getContent()).isEmpty();
			assertThat(result.getTotalElements()).isZero();
		}

		@Test
		@DisplayName("페이징 정보가 올바르게 반영된다")
		void getCalls_Pagination_Success() {
			// given
			Pageable pageable = PageRequest.of(1, 5);
			CallWithScenarioDto dto = new CallWithScenarioDto(
				6L, 1L, "여행 예약", "호텔 예약하기",
				Level.ADVANCED, Category.TRAVEL, null, LocalDateTime.now()
			);

			Page<CallWithScenarioDto> page = new PageImpl<>(List.of(dto), pageable, 11);
			given(callQueryRepository.findAllByMemberIdWithScenario(MEMBER_ID, pageable))
				.willReturn(page);

			// when
			Page<CallInfo> result = getCallsUseCase.execute(MEMBER_ID, pageable);

			// then
			assertThat(result.getContent()).hasSize(1);
			assertThat(result.getTotalElements()).isEqualTo(11);
			assertThat(result.getTotalPages()).isEqualTo(3);
			assertThat(result.getNumber()).isEqualTo(1);
		}
	}
}
