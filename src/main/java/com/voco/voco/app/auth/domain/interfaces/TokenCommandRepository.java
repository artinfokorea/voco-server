package com.voco.voco.app.auth.domain.interfaces;

import com.voco.voco.app.auth.domain.model.TokenEntity;

public interface TokenCommandRepository {

	TokenEntity save(TokenEntity token);
}
