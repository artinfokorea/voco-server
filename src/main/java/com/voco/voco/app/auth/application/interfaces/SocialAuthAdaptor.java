package com.voco.voco.app.auth.application.interfaces;

import com.voco.voco.app.auth.application.interfaces.dto.SocialUserInfo;
import com.voco.voco.app.member.domain.model.Provider;

public interface SocialAuthAdaptor {

	SocialUserInfo verifyToken(Provider provider, String idToken);
}
