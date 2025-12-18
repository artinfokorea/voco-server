package com.voco.voco.tov.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.voco.voco.tov.domain.interfaces.VlUserQueryRepository;
import com.voco.voco.tov.domain.model.VlUserEntity;
import com.voco.voco.tov.infrastructure.persistence.VlUserJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class VlUserQueryRepositoryImpl implements VlUserQueryRepository {

	private final VlUserJpaRepository vlUserJpaRepository;

	@Override
	public Optional<VlUserEntity> findById(UUID id) {
		return vlUserJpaRepository.findById(id);
	}
}