package com.voco.voco.batch;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.voco.voco.tov.domain.interfaces.VlExamCommandRepository;
import com.voco.voco.tov.domain.interfaces.VlExamQuestionCommandRepository;
import com.voco.voco.tov.domain.interfaces.VlExamQuestionQueryRepository;
import com.voco.voco.tov.domain.interfaces.VlWordGroupQueryRepository;
import com.voco.voco.tov.domain.model.VlExamEntity;
import com.voco.voco.tov.domain.model.VlExamQuestionEntity;
import com.voco.voco.tov.domain.model.VlWordGroupEntity;
import com.voco.voco.tov.domain.model.enums.VlPassStatus;
import com.voco.voco.tov.domain.service.ExamGradingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamGradingExecutor {

	private final VlExamCommandRepository vlExamCommandRepository;
	private final VlExamQuestionQueryRepository vlExamQuestionQueryRepository;
	private final VlExamQuestionCommandRepository vlExamQuestionCommandRepository;
	private final VlWordGroupQueryRepository vlWordGroupQueryRepository;
	private final ExamGradingService examGradingService;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void gradeExam(VlExamEntity exam) {
		List<VlExamQuestionEntity> questions = vlExamQuestionQueryRepository.findByExamId(exam.getId());
		VlWordGroupEntity wordGroup = vlWordGroupQueryRepository.findByIdOrThrow(exam.getWordGroupId());

		examGradingService.gradeQuestions(questions);
		vlExamQuestionCommandRepository.saveAll(questions);

		int correctAnswers = examGradingService.countCorrectAnswers(questions);
		VlPassStatus passStatus = examGradingService.calculatePassStatus(questions, wordGroup);

		exam.complete(correctAnswers, passStatus);
		vlExamCommandRepository.save(exam);

		log.info("Graded exam {} - correctAnswers: {}, passStatus: {}",
			exam.getId(), correctAnswers, passStatus);
	}
}
