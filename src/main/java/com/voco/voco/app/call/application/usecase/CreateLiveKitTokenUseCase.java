package com.voco.voco.app.call.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voco.voco.app.call.application.interfaces.LiveKitTokenAdaptor;
import com.voco.voco.app.call.application.usecase.dto.out.LiveKitTokenInfo;
import com.voco.voco.app.call.application.usecase.dto.out.ScenarioMetadata;
import com.voco.voco.app.call.domain.interfaces.CallCommandRepository;
import com.voco.voco.app.call.domain.model.CallEntity;
import com.voco.voco.app.member.domain.interfaces.MemberQueryRepository;
import com.voco.voco.app.member.domain.model.MemberEntity;
import com.voco.voco.app.scenario.domain.interfaces.ScenarioQueryRepository;
import com.voco.voco.app.scenario.domain.model.ConversationScenarioEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateLiveKitTokenUseCase {

	private final LiveKitTokenAdaptor liveKitTokenAdaptor;
	private final MemberQueryRepository memberQueryRepository;
	private final ScenarioQueryRepository scenarioQueryRepository;
	private final CallCommandRepository callCommandRepository;
	private final ObjectMapper objectMapper;

	@Transactional
	public LiveKitTokenInfo execute(Long memberId, Long scenarioId) {
		MemberEntity member = memberQueryRepository.findByIdOrThrow(memberId);
		ConversationScenarioEntity scenario = scenarioQueryRepository.findByIdOrThrow(scenarioId);

		String roomName = generateRoomName(member, scenario);
		String participantIdentity = String.valueOf(member.getId());
		String participantName = member.getKoreanName();

		CallEntity call = CallEntity.create(memberId, scenarioId, roomName);
		Long callId = callCommandRepository.save(call);

		String metadata = createMetadata(scenario, callId);
		String token = liveKitTokenAdaptor.createToken(roomName, participantIdentity, participantName, metadata);
		return new LiveKitTokenInfo(token, roomName, callId);
	}

	private String generateRoomName(MemberEntity member, ConversationScenarioEntity scenario) {
		String shortUuid = UUID.randomUUID().toString().substring(0, 8);
		return String.format("room-%d-%d-%s-%s",
			member.getId(),
			scenario.getId(),
			scenario.getLevel().name().toLowerCase(),
			shortUuid);
	}

	private String createMetadata(ConversationScenarioEntity scenario, Long callId) {
		ScenarioMetadata metadata = ScenarioMetadata.from(scenario, callId);
		try {
			return objectMapper.writeValueAsString(metadata);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Failed to serialize scenario metadata", e);
		}
	}
}
