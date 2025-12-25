package com.voco.voco.tov.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voco.voco.tov.domain.model.VlExamQuestionEntity;

public interface VlExamQuestionJpaRepository extends JpaRepository<VlExamQuestionEntity, UUID> {
	List<VlExamQuestionEntity> findByExamId(UUID examId);
}