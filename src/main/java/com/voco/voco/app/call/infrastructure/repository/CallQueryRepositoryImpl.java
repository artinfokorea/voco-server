package com.voco.voco.app.call.infrastructure.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.voco.voco.app.call.domain.interfaces.CallQueryRepository;
import com.voco.voco.app.call.domain.interfaces.dto.CallWithScenarioDto;
import com.voco.voco.app.call.domain.model.QCallEntity;
import com.voco.voco.app.scenario.domain.model.QScenarioEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CallQueryRepositoryImpl implements CallQueryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<CallWithScenarioDto> findAllByMemberIdWithScenario(Long memberId, Pageable pageable) {
		QCallEntity call = QCallEntity.callEntity;
		QScenarioEntity scenario = QScenarioEntity.scenarioEntity;

		List<CallWithScenarioDto> content = queryFactory
			.select(Projections.constructor(CallWithScenarioDto.class,
				call.id,
				scenario.id,
				scenario.title,
				scenario.description,
				scenario.level,
				scenario.category,
				call.analysisId,
				call.createdAt
			))
			.from(call)
			.join(scenario).on(call.scenarioId.eq(scenario.id))
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

		return new PageImpl<>(content, pageable, total != null ? total : 0);
	}

	@Override
	public boolean existsByAnalysisIdAndMemberId(Long analysisId, Long memberId) {
		QCallEntity call = QCallEntity.callEntity;

		Integer result = queryFactory
			.selectOne()
			.from(call)
			.where(
				call.analysisId.eq(analysisId),
				call.memberId.eq(memberId)
			)
			.fetchFirst();

		return result != null;
	}
}
