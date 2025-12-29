package com.voco.voco.app.call.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voco.voco.app.call.application.usecase.dto.in.CreateCallAnalysisUseCaseDto;
import com.voco.voco.app.call.domain.interfaces.CallAnalysisCommandRepository;
import com.voco.voco.app.call.domain.interfaces.CallQueryRepository;
import com.voco.voco.app.call.domain.model.CallAnalysisEntity;
import com.voco.voco.app.call.domain.model.CallEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateCallAnalysisUseCase {

	private final CallQueryRepository callQueryRepository;
	private final CallAnalysisCommandRepository callAnalysisCommandRepository;

	@Transactional
	public Long execute(Long callId, CreateCallAnalysisUseCaseDto dto) {
		CallEntity call = callQueryRepository.findByIdOrThrow(callId);

		CallAnalysisEntity analysis = dto.toEntity();
		Long analysisId = callAnalysisCommandRepository.save(analysis);

		call.updateAnalysisId(analysisId);

		return analysisId;
	}
}
