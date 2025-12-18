package com.voco.voco.tov.infrastructure.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.voco.voco.tov.domain.interfaces.VlExamQuestionCommandRepository;
import com.voco.voco.tov.domain.model.VlExamQuestionEntity;
import com.voco.voco.tov.infrastructure.persistence.VlExamQuestionJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class VlExamQuestionCommandRepositoryImpl implements VlExamQuestionCommandRepository {

	private final VlExamQuestionJpaRepository vlExamQuestionJpaRepository;

	@Override
	public List<VlExamQuestionEntity> saveAll(List<VlExamQuestionEntity> questions) {
		return vlExamQuestionJpaRepository.saveAll(questions);
	}
}