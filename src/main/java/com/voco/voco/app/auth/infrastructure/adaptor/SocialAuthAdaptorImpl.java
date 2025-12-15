package com.voco.voco.app.auth.infrastructure.adaptor;

import org.springframework.stereotype.Component;

import com.voco.voco.app.auth.application.interfaces.SocialAuthAdaptor;
import com.voco.voco.app.auth.application.interfaces.dto.SocialUserInfo;
import com.voco.voco.app.member.domain.model.Provider;
import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SocialAuthAdaptorImpl implements SocialAuthAdaptor {

	private final AppleAuthClient appleAuthClient;
	private final GoogleAuthClient googleAuthClient;
	private final KakaoAuthClient kakaoAuthClient;

	@Override
	public SocialUserInfo verifyToken(Provider provider, String idToken) {
		return switch (provider) {
			case APPLE -> appleAuthClient.verifyToken(idToken);
			case GOOGLE -> googleAuthClient.verifyToken(idToken);
			case KAKAO -> kakaoAuthClient.verifyToken(idToken);
			case EMAIL -> throw new CoreException(ApiErrorType.UNSUPPORTED_PROVIDER);
		};
	}
}
