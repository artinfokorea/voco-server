package com.voco.voco.tov.infrastructure.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.voco.voco.tov.domain.interfaces.VlExamQueryRepository;
import com.voco.voco.tov.domain.model.enums.VlExamStatus;
import com.voco.voco.tov.infrastructure.persistence.VlExamJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class VlExamQueryRepositoryImpl implements VlExamQueryRepository {

	private final VlExamJpaRepository vlExamJpaRepository;

	@Override
	public boolean existsInProgressByUserId(UUID userId) {
		return vlExamJpaRepository.existsByUserIdAndStatus(userId, VlExamStatus.IN_PROGRESS);
	}
}
