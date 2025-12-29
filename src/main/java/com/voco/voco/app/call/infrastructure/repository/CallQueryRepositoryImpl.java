package com.voco.voco.app.call.infrastructure.repository;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.voco.voco.app.call.domain.interfaces.CallQueryRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CallQueryRepositoryImpl implements CallQueryRepository {

	private final JPAQueryFactory queryFactory;

}
