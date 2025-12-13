package com.voco.voco.app.member.infrastructure.repository;

import org.springframework.stereotype.Repository;

import com.voco.voco.app.member.domain.interfaces.MemberQueryRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepositoryImpl implements MemberQueryRepository {

	private final MemberJpaRepository memberJpaRepository;

	@Override
	public boolean existsByEmail(String email) {
		return memberJpaRepository.existsByEmail(email);
	}
}
