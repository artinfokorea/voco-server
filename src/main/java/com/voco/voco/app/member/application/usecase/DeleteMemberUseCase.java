package com.voco.voco.app.member.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voco.voco.app.member.domain.interfaces.MemberQueryRepository;
import com.voco.voco.app.member.domain.model.MemberEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeleteMemberUseCase {

	private final MemberQueryRepository memberQueryRepository;

	@Transactional
	public void execute(Long memberId) {
		MemberEntity member = memberQueryRepository.findByIdOrThrow(memberId);
		member.delete();
	}
}
