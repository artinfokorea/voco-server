package com.voco.voco.tov.infrastructure.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.voco.voco.tov.domain.interfaces.VlExamQuestionQueryRepository;
import com.voco.voco.tov.domain.model.VlExamQuestionEntity;
import com.voco.voco.tov.infrastructure.persistence.VlExamQuestionJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class VlExamQuestionQueryRepositoryImpl implements VlExamQuestionQueryRepository {

	private final VlExamQuestionJpaRepository vlExamQuestionJpaRepository;

	@Override
	public List<VlExamQuestionEntity> findByExamId(UUID examId) {
		return vlExamQuestionJpaRepository.findByExamId(examId);
	}
}
