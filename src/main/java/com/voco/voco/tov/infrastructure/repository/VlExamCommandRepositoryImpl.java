package com.voco.voco.tov.infrastructure.repository;

import org.springframework.stereotype.Repository;

import com.voco.voco.tov.domain.interfaces.VlExamCommandRepository;
import com.voco.voco.tov.domain.model.VlExamEntity;
import com.voco.voco.tov.infrastructure.persistence.VlExamJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class VlExamCommandRepositoryImpl implements VlExamCommandRepository {

	private final VlExamJpaRepository vlExamJpaRepository;

	@Override
	public VlExamEntity save(VlExamEntity exam) {
		return vlExamJpaRepository.save(exam);
	}
}