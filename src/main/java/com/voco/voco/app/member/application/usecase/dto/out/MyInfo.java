package com.voco.voco.app.member.application.usecase.dto.out;

import com.voco.voco.app.member.domain.model.Level;
import com.voco.voco.app.member.domain.model.MemberEntity;

public record MyInfo(
	String koreanName,
	String englishName,
	String email,
	Level level
) {
	public static MyInfo from(MemberEntity member) {
		return new MyInfo(
			member.getKoreanName(),
			member.getEnglishName(),
			member.getEmail(),
			member.getLevel()
		);
	}
}
