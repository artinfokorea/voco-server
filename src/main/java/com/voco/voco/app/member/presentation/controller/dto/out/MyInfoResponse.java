package com.voco.voco.app.member.presentation.controller.dto.out;

import com.voco.voco.app.member.application.usecase.dto.out.MyInfo;
import com.voco.voco.app.member.domain.model.Level;

public record MyInfoResponse(
	String koreanName,
	String englishName,
	String email,
	Level level
) {
	public static MyInfoResponse from(MyInfo info) {
		return new MyInfoResponse(
			info.koreanName(),
			info.englishName(),
			info.email(),
			info.level()
		);
	}
}
