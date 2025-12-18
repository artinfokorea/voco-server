package com.voco.voco.tov.domain.interfaces;

import java.util.List;
import java.util.UUID;

import com.voco.voco.tov.domain.model.VlWordGroupItemEntity;

public interface VlWordGroupItemQueryRepository {

	List<VlWordGroupItemEntity> findByWordGroupIdAndWordSeqBetween(UUID wordGroupId, Integer from, Integer to);
}
