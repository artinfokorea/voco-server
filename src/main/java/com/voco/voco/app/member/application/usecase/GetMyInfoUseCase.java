package com.voco.voco.app.member.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voco.voco.app.member.application.usecase.dto.out.MyInfo;
import com.voco.voco.app.member.domain.interfaces.MemberQueryRepository;
import com.voco.voco.app.member.domain.model.MemberEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetMyInfoUseCase {

	private final MemberQueryRepository memberQueryRepository;

	@Transactional(readOnly = true)
	public MyInfo execute(Long memberId) {
		MemberEntity member = memberQueryRepository.findByIdOrThrow(memberId);
		return MyInfo.from(member);
	}
}
