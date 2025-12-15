package com.voco.voco.app.member.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Provider {
	EMAIL("이메일"),
	APPLE("애플"),
	GOOGLE("구글"),
	KAKAO("카카오");

	private final String description;
}
