package com.voco.voco.app.call.infrastructure.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.voco.voco.app.call.domain.interfaces.CallQueryRepository;
import com.voco.voco.app.call.domain.interfaces.dto.out.AdminCallHistoryDomainDto;
import com.voco.voco.app.call.domain.interfaces.dto.out.CallDetailDomainDto;
import com.voco.voco.app.call.domain.interfaces.dto.out.CallHistoryDomainDto;
import com.voco.voco.app.call.domain.model.CallEntity;
import com.voco.voco.app.call.domain.model.QCallAnalysisEntity;
import com.voco.voco.app.call.domain.model.QCallEntity;
import com.voco.voco.app.member.domain.model.QMemberEntity;
import com.voco.voco.app.scenario.domain.model.QConversationScenarioEntity;
import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CallQueryRepositoryImpl implements CallQueryRepository {

	private final JPAQueryFactory queryFactory;
	private final CallJpaRepository callJpaRepository;

	private static final QCallEntity call = QCallEntity.callEntity;
	private static final QConversationScenarioEntity scenario = QConversationScenarioEntity.conversationScenarioEntity;
	private static final QCallAnalysisEntity analysis = QCallAnalysisEntity.callAnalysisEntity;
	private static final QMemberEntity member = QMemberEntity.memberEntity;

	@Override
	public CallEntity findByIdOrThrow(Long id) {
		return callJpaRepository.findById(id)
			.orElseThrow(() -> new CoreException(ApiErrorType.CALL_NOT_FOUND));
	}

	@Override
	public Page<CallHistoryDomainDto> findCallHistoryByMemberId(Long memberId, Pageable pageable) {
		List<CallHistoryDomainDto> content = queryFactory
			.select(Projections.constructor(
				CallHistoryDomainDto.class,
				call.id,
				call.createdAt,
				scenario.name,
				analysis.grade
			))
			.from(call)
			.leftJoin(scenario).on(call.scenarioId.eq(scenario.id))
			.leftJoin(analysis).on(call.analysisId.eq(analysis.id))
			.where(call.memberId.eq(memberId))
			.orderBy(call.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = queryFactory
			.select(call.count())
			.from(call)
			.where(call.memberId.eq(memberId))
			.fetchOne();

		return new PageImpl<>(content, pageable, total != null ? total : 0L);
	}

	@Override
	public Optional<CallDetailDomainDto> findCallDetailByIdAndMemberId(Long callId, Long memberId) {
		Tuple result = queryFactory
			.select(
				call.id,
				call.createdAt,
				scenario.name,
				scenario.level,
				analysis
			)
			.from(call)
			.leftJoin(scenario).on(call.scenarioId.eq(scenario.id))
			.leftJoin(analysis).on(call.analysisId.eq(analysis.id))
			.where(
				call.id.eq(callId),
				call.memberId.eq(memberId)
			)
			.fetchOne();

		if (result == null) {
			return Optional.empty();
		}

		return Optional.of(new CallDetailDomainDto(
			result.get(call.id),
			result.get(call.createdAt),
			result.get(scenario.name),
			result.get(scenario.level),
			result.get(analysis)
		));
	}

	@Override
	public Page<AdminCallHistoryDomainDto> findAllCallHistory(Pageable pageable) {
		List<Tuple> results = queryFactory
			.select(
				call.id,
				scenario.name,
				member.koreanName,
				call.memberId,
				scenario.level,
				analysis.createdAt,
				analysis
			)
			.from(call)
			.leftJoin(scenario).on(call.scenarioId.eq(scenario.id))
			.leftJoin(member).on(call.memberId.eq(member.id))
			.leftJoin(analysis).on(call.analysisId.eq(analysis.id))
			.orderBy(call.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		List<AdminCallHistoryDomainDto> content = results.stream()
			.map(tuple -> new AdminCallHistoryDomainDto(
				tuple.get(call.id),
				tuple.get(scenario.name),
				tuple.get(member.koreanName),
				tuple.get(call.memberId),
				tuple.get(scenario.level),
				tuple.get(analysis.createdAt),
				tuple.get(analysis)
			))
			.toList();

		Long total = queryFactory
			.select(call.count())
			.from(call)
			.fetchOne();

		return new PageImpl<>(content, pageable, total != null ? total : 0L);
	}
}
