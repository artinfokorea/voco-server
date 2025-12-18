package com.voco.voco.tov.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.voco.voco.tov.application.dto.in.CreateExamUseCaseDto;
import com.voco.voco.tov.domain.interfaces.VlExamCommandRepository;
import com.voco.voco.tov.domain.interfaces.VlExamQuestionCommandRepository;
import com.voco.voco.tov.domain.interfaces.VlUserQueryRepository;
import com.voco.voco.tov.domain.interfaces.VlWordGroupItemQueryRepository;
import com.voco.voco.tov.domain.interfaces.VlWordGroupQueryRepository;
import com.voco.voco.tov.domain.interfaces.VlWordQueryRepository;
import com.voco.voco.tov.domain.interfaces.dto.WordWithDetailsDto;
import com.voco.voco.tov.domain.model.VlExamEntity;
import com.voco.voco.tov.domain.model.VlExamQuestionEntity;
import com.voco.voco.tov.domain.model.VlWordGroupEntity;
import com.voco.voco.tov.domain.model.VlWordGroupItemEntity;
import com.voco.voco.tov.domain.service.ExamQuestionGenerator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CreateExamUseCase {

	private final VlUserQueryRepository vlUserQueryRepository;
	private final VlWordGroupQueryRepository vlWordGroupQueryRepository;
	private final VlWordGroupItemQueryRepository vlWordGroupItemQueryRepository;
	private final VlWordQueryRepository vlWordQueryRepository;
	private final VlExamCommandRepository vlExamCommandRepository;
	private final VlExamQuestionCommandRepository vlExamQuestionCommandRepository;
	private final ExamQuestionGenerator examQuestionGenerator;

	@Transactional
	public UUID execute(CreateExamUseCaseDto dto) {
		vlUserQueryRepository.findByIdOrThrow(dto.memberId());
		VlWordGroupEntity group = vlWordGroupQueryRepository.findByIdOrThrow(dto.groupId());

		int from = dto.chapterFrom() * 1000 + (dto.stepFrom() != null ? dto.stepFrom() : 0);
		int to = dto.chapterTo() * 1000 + (dto.stepTo() != null ? dto.stepTo() : 999);

		List<VlWordGroupItemEntity> items = vlWordGroupItemQueryRepository.findByWordGroupIdAndWordSeqBetween(
			dto.groupId(), from, to);

		if (items.isEmpty()) {
			throw new IllegalArgumentException("범위 내 단어가 없습니다.");
		}

		List<UUID> masterWordIds = items.stream()
			.map(VlWordGroupItemEntity::getMasterWordId)
			.toList();

		List<WordWithDetailsDto> words = vlWordQueryRepository.findWordsWithDetailsByMasterWordIds(masterWordIds);

		int actualSize = Math.min(dto.size(), items.size());

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

		List<VlExamQuestionEntity> questions = examQuestionGenerator.generate(
			savedExam.getId(),
			group,
			words,
			actualSize
		);

		List<VlExamQuestionEntity> shuffledQuestions = new ArrayList<>(questions);
		Collections.shuffle(shuffledQuestions);

		List<VlExamQuestionEntity> numberedQuestions = new ArrayList<>();
		for (int i = 0; i < shuffledQuestions.size(); i++) {
			VlExamQuestionEntity q = shuffledQuestions.get(i);
			numberedQuestions.add(VlExamQuestionEntity.create(
				q.getExamId(),
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
