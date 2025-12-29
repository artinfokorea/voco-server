package com.voco.voco.app.call.infrastructure.repository;

import org.springframework.stereotype.Repository;

import com.voco.voco.app.call.domain.interfaces.CallCommandRepository;
import com.voco.voco.app.call.domain.model.CallEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CallCommandRepositoryImpl implements CallCommandRepository {

	private final CallJpaRepository callJpaRepository;

	@Override
	public Long save(CallEntity call) {
		return callJpaRepository.save(call).getId();
	}
}
