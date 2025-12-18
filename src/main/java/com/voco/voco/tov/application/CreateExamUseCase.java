package com.voco.voco.tov.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;
import com.voco.voco.tov.application.dto.in.CreateExamUseCaseDto;
import com.voco.voco.tov.domain.interfaces.VlExamCommandRepository;
import com.voco.voco.tov.domain.interfaces.VlExamQueryRepository;
import com.voco.voco.tov.domain.interfaces.VlExamQuestionCommandRepository;
import com.voco.voco.tov.domain.interfaces.VlUserQueryRepository;
import com.voco.voco.tov.domain.model.VlExamEntity;
import com.voco.voco.tov.domain.model.VlExamQuestionEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateExamUseCase {

	private final VlUserQueryRepository vlUserQueryRepository;
	private final VlExamQueryRepository vlExamQueryRepository;
	private final VlExamCommandRepository vlExamCommandRepository;
	private final VlExamQuestionCommandRepository vlExamQuestionCommandRepository;
	private final ExamWordsCacheService examWordsCacheService;

	@Transactional
	public UUID execute(CreateExamUseCaseDto dto) {
		vlUserQueryRepository.findByIdOrThrow(dto.memberId());

		if (vlExamQueryRepository.existsInProgressByUserId(dto.memberId())) {
			throw new CoreException(ApiErrorType.EXAM_IN_PROGRESS);
		}

		List<VlExamQuestionEntity> questionTemplates = examWordsCacheService.getQuestionsTemplate(
			dto.groupId(),
			dto.chapterFrom(),
			dto.chapterTo(),
			dto.stepFrom(),
			dto.stepTo(),
			dto.size()
		);

		log.info("[Cache HIT] 캐시에서 문제 템플릿 조회 완료 - {}개 문제", questionTemplates.size());

		int actualSize = questionTemplates.size();

		VlExamEntity exam = VlExamEntity.create(
			dto.memberId(),
			dto.groupId(),
			dto.chapterFrom(),
			dto.stepFrom(),
			dto.chapterTo(),
			dto.stepTo(),
			actualSize
		);
		VlExamEntity savedExam = vlExamCommandRepository.save(exam);

		List<VlExamQuestionEntity> shuffledQuestions = new ArrayList<>(questionTemplates);
		Collections.shuffle(shuffledQuestions);

		List<VlExamQuestionEntity> numberedQuestions = new ArrayList<>();
		for (int i = 0; i < shuffledQuestions.size(); i++) {
			VlExamQuestionEntity q = shuffledQuestions.get(i);
			numberedQuestions.add(VlExamQuestionEntity.create(
				savedExam.getId(),
				q.getMasterWordId(),
				q.getVlQuestionType(),
				i + 1,
				q.getQuestionText(),
				q.getCorrectAnswer(),
				q.getOption1(),
				q.getOption2(),
				q.getOption3(),
				q.getOption4(),
				q.getOption5()
			));
		}

		vlExamQuestionCommandRepository.saveAll(numberedQuestions);

		return savedExam.getId();
	}
}
