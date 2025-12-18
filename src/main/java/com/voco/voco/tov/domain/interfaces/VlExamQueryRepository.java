package com.voco.voco.tov.domain.interfaces;

import java.util.UUID;

public interface VlExamQueryRepository {
	boolean existsInProgressByUserId(UUID userId);
}
