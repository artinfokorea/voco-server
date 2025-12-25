package com.voco.voco.tov.infrastructure.repository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.voco.voco.tov.domain.interfaces.VlExamQueryRepository;
import com.voco.voco.tov.domain.model.QVlExamEntity;
import com.voco.voco.tov.domain.model.VlExamEntity;
import com.voco.voco.tov.domain.model.enums.VlExamStatus;
import com.voco.voco.tov.infrastructure.persistence.VlExamJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class VlExamQueryRepositoryImpl implements VlExamQueryRepository {

	private final VlExamJpaRepository vlExamJpaRepository;
	private final JPAQueryFactory queryFactory;

	@Override
	public boolean existsInProgressByUserId(UUID userId) {
		return vlExamJpaRepository.existsByUserIdAndStatus(userId, VlExamStatus.IN_PROGRESS);
	}

	@Override
	public List<VlExamEntity> findExpiredInProgressExams(int expiredMinutes) {
		QVlExamEntity exam = QVlExamEntity.vlExamEntity;

		String expiredTime = Instant.now()
			.minus(expiredMinutes, ChronoUnit.MINUTES)
			.toString();

		return queryFactory
			.selectFrom(exam)
			.where(
				exam.status.eq(VlExamStatus.IN_PROGRESS),
				exam.startedAt.isNotNull(),
				exam.startedAt.lt(expiredTime)
			)
			.fetch();
	}
}
