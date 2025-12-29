package com.voco.voco.app.call.infrastructure.repository;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.voco.voco.app.call.domain.interfaces.CallQueryRepository;
import com.voco.voco.app.call.domain.model.CallEntity;
import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CallQueryRepositoryImpl implements CallQueryRepository {

	private final JPAQueryFactory queryFactory;
	private final CallJpaRepository callJpaRepository;

	@Override
	public CallEntity findByIdOrThrow(Long id) {
		return callJpaRepository.findById(id)
			.orElseThrow(() -> new CoreException(ApiErrorType.CALL_NOT_FOUND));
	}
}
