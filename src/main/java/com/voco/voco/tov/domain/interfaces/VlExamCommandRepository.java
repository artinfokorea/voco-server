package com.voco.voco.tov.domain.interfaces;

import com.voco.voco.tov.domain.model.VlExamEntity;

public interface VlExamCommandRepository {
	VlExamEntity save(VlExamEntity exam);
}