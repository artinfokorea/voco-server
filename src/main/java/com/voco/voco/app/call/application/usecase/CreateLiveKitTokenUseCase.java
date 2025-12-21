package com.voco.voco.app.call.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voco.voco.app.call.application.interfaces.LiveKitTokenAdaptor;
import com.voco.voco.app.call.application.usecase.dto.out.LiveKitTokenInfo;
import com.voco.voco.app.member.domain.interfaces.MemberQueryRepository;
import com.voco.voco.app.member.domain.model.MemberEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateLiveKitTokenUseCase {

	private final LiveKitTokenAdaptor liveKitTokenAdaptor;
	private final MemberQueryRepository memberQueryRepository;

	@Transactional(readOnly = true)
	public LiveKitTokenInfo execute(Long memberId) {
		MemberEntity member = memberQueryRepository.findByIdOrThrow(memberId);

		String roomName = generateRoomName(member);
		String participantIdentity = String.valueOf(member.getId());
		String participantName = member.getKoreanName();

		String token = liveKitTokenAdaptor.createToken(roomName, participantIdentity, participantName);
		return new LiveKitTokenInfo(token, roomName);
	}

	private String generateRoomName(MemberEntity member) {
		String shortUuid = UUID.randomUUID().toString().substring(0, 8);
		return String.format("room-%d-%s-%s", member.getId(), member.getLevel().name().toLowerCase(), shortUuid);
	}
}
