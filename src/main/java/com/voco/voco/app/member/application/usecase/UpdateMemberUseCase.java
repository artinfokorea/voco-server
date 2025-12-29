package com.voco.voco.app.member.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voco.voco.app.member.application.usecase.dto.in.UpdateMemberUseCaseDto;
import com.voco.voco.app.member.domain.interfaces.MemberQueryRepository;
import com.voco.voco.app.member.domain.model.MemberEntity;
import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateMemberUseCase {

	private final MemberQueryRepository memberQueryRepository;

	public void execute(UpdateMemberUseCaseDto dto) {
		MemberEntity member = memberQueryRepository.findById(dto.memberId())
			.orElseThrow(() -> new CoreException(ApiErrorType.MEMBER_NOT_FOUND));

		member.update(dto.englishName(), dto.level());
	}
}
