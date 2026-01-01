package com.voco.voco.app.call.infrastructure.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.voco.voco.app.call.domain.interfaces.CallAnalysisRawQueryRepository;
import com.voco.voco.app.call.domain.model.CallAnalysisRawEntity;
import com.voco.voco.app.call.domain.model.QCallAnalysisRawEntity;
import com.voco.voco.app.call.domain.model.QCallEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CallAnalysisRawQueryRepositoryImpl implements CallAnalysisRawQueryRepository {

	private final JPAQueryFactory queryFactory;

	private static final QCallEntity call = QCallEntity.callEntity;
	private static final QCallAnalysisRawEntity analysisRaw = QCallAnalysisRawEntity.callAnalysisRawEntity;

	@Override
	public Optional<CallAnalysisRawEntity> findByCallId(Long callId) {
		CallAnalysisRawEntity result = queryFactory
			.selectFrom(analysisRaw)
			.join(call).on(call.analysisId.eq(analysisRaw.analysisId))
			.where(call.id.eq(callId))
			.fetchOne();

		return Optional.ofNullable(result);
	}
}
