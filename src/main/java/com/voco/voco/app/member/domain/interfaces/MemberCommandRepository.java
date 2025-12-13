package com.voco.voco.app.member.domain.interfaces;

import com.voco.voco.app.member.domain.model.MemberEntity;

public interface MemberCommandRepository {

	Long save(MemberEntity member);
}
