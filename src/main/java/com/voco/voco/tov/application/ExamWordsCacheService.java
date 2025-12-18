package com.voco.voco.tov.application;

import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;
import com.voco.voco.tov.domain.interfaces.VlWordGroupItemQueryRepository;
import com.voco.voco.tov.domain.interfaces.VlWordGroupQueryRepository;
import com.voco.voco.tov.domain.interfaces.VlWordQueryRepository;
import com.voco.voco.tov.domain.interfaces.dto.WordWithDetailsDto;
import com.voco.voco.tov.domain.model.VlExamQuestionEntity;
import com.voco.voco.tov.domain.model.VlWordGroupEntity;
import com.voco.voco.tov.domain.model.VlWordGroupItemEntity;
import com.voco.voco.tov.domain.service.ExamQuestionGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamWordsCacheService {

	private final VlWordGroupItemQueryRepository vlWordGroupItemQueryRepository;
	private final VlWordQueryRepository vlWordQueryRepository;
	private final VlWordGroupQueryRepository vlWordGroupQueryRepository;
	private final ExamQuestionGenerator examQuestionGenerator;

	@Cacheable(value = "examQuestions", key = "#groupId + '_' + #chapterFrom + '_' + #chapterTo + '_' + #stepFrom + '_' + #stepTo + '_' + #size")
	public List<VlExamQuestionEntity> getQuestionsTemplate(UUID groupId, Integer chapterFrom, Integer chapterTo,
		Integer stepFrom, Integer stepTo, Integer size) {

		log.info("[Cache MISS] 문제 템플릿 새로 생성 - groupId: {}, chapter: {}-{}, step: {}-{}, size: {}",
			groupId, chapterFrom, chapterTo, stepFrom, stepTo, size);

		VlWordGroupEntity group = vlWordGroupQueryRepository.findByIdOrThrow(groupId);

		int from = chapterFrom * 1000 + (stepFrom != null ? stepFrom : 0);
		int to = chapterTo * 1000 + (stepTo != null ? stepTo : 999);

		List<VlWordGroupItemEntity> items = vlWordGroupItemQueryRepository.findByWordGroupIdAndWordSeqBetween(
			groupId, from, to);

		if (items.isEmpty()) {
			throw new CoreException(ApiErrorType.WORD_NOT_FOUND_IN_RANGE);
		}

		List<UUID> masterWordIds = items.stream()
			.map(VlWordGroupItemEntity::getMasterWordId)
			.toList();

		List<WordWithDetailsDto> words = vlWordQueryRepository.findWordsWithDetailsByMasterWordIds(masterWordIds);

		int actualSize = Math.min(size, words.size());

		return examQuestionGenerator.generate(null, group, words, actualSize);
	}
}
