package com.voco.voco.tov.domain.interfaces;

import java.util.List;
import java.util.UUID;

import com.voco.voco.tov.domain.model.VlExamQuestionEntity;

public interface VlExamQuestionQueryRepository {
	List<VlExamQuestionEntity> findByExamId(UUID examId);
}
