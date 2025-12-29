package com.voco.voco.app.member.infrastructure.adaptor;

import org.springframework.stereotype.Component;

import com.voco.voco.app.member.domain.interfaces.MemberQueryRepository;
import com.voco.voco.app.member.domain.model.MemberEntity;
import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;
import com.voco.voco.common.interfaces.AdminChecker;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdminCheckerImpl implements AdminChecker {

	private final MemberQueryRepository memberQueryRepository;

	@Override
	public boolean isAdmin(Long memberId) {
		MemberEntity member = memberQueryRepository.findByIdOrThrow(memberId);
		return member.isAdmin();
	}

	@Override
	public void validateAdmin(Long memberId) {
		if (!isAdmin(memberId)) {
			throw new CoreException(ApiErrorType.ADMIN_ONLY);
		}
	}
}
