package com.voco.voco.app.member.domain.interfaces;

public interface MemberQueryRepository {

	boolean existsByEmail(String email);
}
