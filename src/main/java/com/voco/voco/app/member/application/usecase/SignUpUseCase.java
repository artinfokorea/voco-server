package com.voco.voco.app.member.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voco.voco.app.member.application.usecase.dto.in.SignUpUseCaseDto;
import com.voco.voco.app.member.domain.interfaces.MemberCommandRepository;
import com.voco.voco.app.member.domain.interfaces.MemberQueryRepository;
import com.voco.voco.app.member.domain.model.MemberEntity;
import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;
import com.voco.voco.common.interfaces.PasswordAdaptor;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SignUpUseCase {

	private final MemberQueryRepository memberQueryRepository;
	private final MemberCommandRepository memberCommandRepository;
	private final PasswordAdaptor passwordAdaptor;

	public Long execute(SignUpUseCaseDto dto) {
		validateDuplicateEmail(dto.email());
		validatePassword(dto.password());

		String encodedPassword = passwordAdaptor.encode(dto.password());

		MemberEntity member = MemberEntity.create(
			dto.koreanName(),
			dto.englishName(),
			dto.email(),
			encodedPassword,
			dto.level()
		);

		return memberCommandRepository.save(member);
	}

	private void validateDuplicateEmail(String email) {
		if (memberQueryRepository.existsByEmail(email)) {
			throw new CoreException(ApiErrorType.DUPLICATED_EMAIL);
		}
	}

	private void validatePassword(String password) {
		String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$";
		if (!password.matches(passwordPattern)) {
			throw new CoreException(ApiErrorType.INVALID_PASSWORD);
		}
	}
}
