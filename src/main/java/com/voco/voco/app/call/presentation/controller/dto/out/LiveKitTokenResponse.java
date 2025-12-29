package com.voco.voco.app.call.presentation.controller.dto.out;

import com.voco.voco.app.call.application.usecase.dto.out.LiveKitTokenInfo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "LiveKit 토큰 응답")
public record LiveKitTokenResponse(
	@Schema(description = "LiveKit 접근 토큰")
	String token,

	@Schema(description = "방 이름")
	String roomName,

	@Schema(description = "통화 ID")
	Long callId
) {
	public static LiveKitTokenResponse from(LiveKitTokenInfo tokenInfo) {
		return new LiveKitTokenResponse(tokenInfo.token(), tokenInfo.roomName(), tokenInfo.callId());
	}
}
