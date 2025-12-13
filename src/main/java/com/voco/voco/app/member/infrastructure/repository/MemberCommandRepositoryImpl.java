package com.voco.voco.app.member.infrastructure.repository;

import org.springframework.stereotype.Repository;

import com.voco.voco.app.member.domain.interfaces.MemberCommandRepository;
import com.voco.voco.app.member.domain.model.MemberEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MemberCommandRepositoryImpl implements MemberCommandRepository {

	private final MemberJpaRepository memberJpaRepository;

	@Override
	public Long save(MemberEntity member) {
		return memberJpaRepository.save(member).getId();
	}
}
