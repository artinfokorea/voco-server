package com.voco.voco.app.member.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voco.voco.app.auth.application.interfaces.SocialAuthAdaptor;
import com.voco.voco.app.auth.application.interfaces.dto.SocialUserInfo;
import com.voco.voco.app.member.application.usecase.dto.in.SocialSignUpUseCaseDto;
import com.voco.voco.app.member.domain.interfaces.MemberCommandRepository;
import com.voco.voco.app.member.domain.interfaces.MemberQueryRepository;
import com.voco.voco.app.member.domain.model.MemberEntity;
import com.voco.voco.app.member.domain.model.Provider;
import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SocialSignUpUseCase {

	private final MemberQueryRepository memberQueryRepository;
	private final MemberCommandRepository memberCommandRepository;
	private final SocialAuthAdaptor socialAuthAdaptor;

	public Long execute(SocialSignUpUseCaseDto dto) {
		validateProvider(dto.provider());

		SocialUserInfo socialUserInfo = socialAuthAdaptor.verifyToken(dto.provider(), dto.idToken());

		validateDuplicateSocialAccount(dto.provider(), socialUserInfo.providerId());

		MemberEntity member = MemberEntity.createSocial(
			dto.provider(),
			socialUserInfo.providerId(),
			dto.koreanName(),
			dto.englishName(),
			socialUserInfo.email(),
			dto.level()
		);

		return memberCommandRepository.save(member);
	}

	private void validateProvider(Provider provider) {
		if (provider == Provider.EMAIL) {
			throw new CoreException(ApiErrorType.UNSUPPORTED_PROVIDER);
		}
	}

	private void validateDuplicateSocialAccount(Provider provider, String providerId) {
		if (memberQueryRepository.existsByProviderAndProviderId(provider, providerId)) {
			throw new CoreException(ApiErrorType.DUPLICATED_SOCIAL_ACCOUNT);
		}
	}
}
