package com.voco.voco.app.call.domain.interfaces;

import com.voco.voco.app.call.domain.model.CallEntity;

public interface CallQueryRepository {

	CallEntity findByIdOrThrow(Long id);
}
