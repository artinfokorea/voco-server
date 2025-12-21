package com.voco.voco.app.call.infrastructure.adaptor;

import org.springframework.stereotype.Component;

import com.voco.voco.app.call.application.interfaces.LiveKitTokenAdaptor;
import com.voco.voco.common.config.LiveKitProperties;

import io.livekit.server.AccessToken;
import io.livekit.server.RoomJoin;
import io.livekit.server.RoomName;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LiveKitTokenProvider implements LiveKitTokenAdaptor {

	private static final long TOKEN_TTL_SECONDS = 3600L;

	private final LiveKitProperties liveKitProperties;

	@Override
	public String createToken(String roomName, String participantIdentity, String participantName) {
		AccessToken token = new AccessToken(
			liveKitProperties.apiKey(),
			liveKitProperties.apiSecret()
		);

		token.setIdentity(participantIdentity);
		token.setName(participantName);
		token.setTtl(TOKEN_TTL_SECONDS);
		token.addGrants(new RoomJoin(true), new RoomName(roomName));

		return token.toJwt();
	}
}
