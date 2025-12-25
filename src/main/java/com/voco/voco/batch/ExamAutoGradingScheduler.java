package com.voco.voco.batch;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

import com.voco.voco.tov.domain.interfaces.VlExamQueryRepository;
import com.voco.voco.tov.domain.model.VlExamEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExamAutoGradingScheduler {

	private static final int EXAM_EXPIRED_MINUTES = 20;

	private final VlExamQueryRepository vlExamQueryRepository;
	private final ExamGradingExecutor examGradingExecutor;

	@Scheduled(fixedRate = 60000)
	@SchedulerLock(name = "examAutoGrading", lockAtLeastFor = "PT30S", lockAtMostFor = "PT5M")
	public void gradeExpiredExams() {
		List<VlExamEntity> expiredExams = vlExamQueryRepository.findExpiredInProgressExams(EXAM_EXPIRED_MINUTES);

		log.info("## Found {} expired exams to grade", expiredExams.size());

		if (expiredExams.isEmpty()) {
			return;
		}

		for (VlExamEntity exam : expiredExams) {
			try {
				examGradingExecutor.gradeExam(exam);
			} catch (Exception e) {
				log.error("## Failed to grade exam {}: {}", exam.getId(), e.getMessage(), e);
			}
		}

		log.info("## Completed grading {} expired exams", expiredExams.size());
	}
}
