package com.voco.voco.tov.domain.interfaces;

import java.util.List;
import java.util.UUID;

import com.voco.voco.tov.domain.model.VlExamEntity;

public interface VlExamQueryRepository {
	boolean existsInProgressByUserId(UUID userId);

	List<VlExamEntity> findExpiredInProgressExams(int expiredMinutes);
}
