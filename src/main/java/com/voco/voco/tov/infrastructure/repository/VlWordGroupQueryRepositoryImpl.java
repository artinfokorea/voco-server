package com.voco.voco.tov.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.voco.voco.tov.domain.interfaces.VlWordGroupQueryRepository;
import com.voco.voco.tov.domain.model.VlWordGroupEntity;
import com.voco.voco.tov.infrastructure.persistence.VlWordGroupJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class VlWordGroupQueryRepositoryImpl implements VlWordGroupQueryRepository {

	private final VlWordGroupJpaRepository vlWordGroupJpaRepository;

	@Override
	public Optional<VlWordGroupEntity> findById(UUID id) {
		return vlWordGroupJpaRepository.findById(id);
	}
}