package com.voco.voco.tov.domain.interfaces;

import java.util.List;

import com.voco.voco.tov.domain.model.VlExamQuestionEntity;

public interface VlExamQuestionCommandRepository {
	List<VlExamQuestionEntity> saveAll(List<VlExamQuestionEntity> questions);
}