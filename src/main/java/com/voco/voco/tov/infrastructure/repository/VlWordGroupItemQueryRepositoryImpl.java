package com.voco.voco.tov.infrastructure.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.voco.voco.tov.domain.interfaces.VlWordGroupItemQueryRepository;
import com.voco.voco.tov.domain.model.VlWordGroupItemEntity;
import com.voco.voco.tov.infrastructure.persistence.VlWordGroupItemJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class VlWordGroupItemQueryRepositoryImpl implements VlWordGroupItemQueryRepository {

	private final VlWordGroupItemJpaRepository vlWordGroupItemJpaRepository;

	@Override
	public List<VlWordGroupItemEntity> findByWordGroupIdAndWordSeqBetween(UUID wordGroupId, Integer from, Integer to) {
		return vlWordGroupItemJpaRepository.findByWordGroupIdAndWordSeqBetween(wordGroupId, from, to);
	}
}