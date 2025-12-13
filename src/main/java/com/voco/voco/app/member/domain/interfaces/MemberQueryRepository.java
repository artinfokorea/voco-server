package com.voco.voco.app.member.domain.interfaces;

import java.util.Optional;

import com.voco.voco.app.member.domain.model.MemberEntity;

public interface MemberQueryRepository {

	boolean existsByEmail(String email);

	Optional<MemberEntity> findByEmail(String email);
}
