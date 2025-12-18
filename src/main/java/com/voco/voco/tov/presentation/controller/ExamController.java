package com.voco.voco.tov.presentation.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voco.voco.common.dto.response.ApiResponse;
import com.voco.voco.tov.application.CreateExamUseCase;
import com.voco.voco.tov.presentation.controller.dto.in.CreateExamRequest;
import com.voco.voco.tov.presentation.controller.dto.out.CreateExamResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "VL_Exam", description = "시험 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/exams")
public class ExamController {

	private final CreateExamUseCase createExamUseCase;

	@Operation(summary = "시험 생성", description = "지정한 범위의 단어로 시험을 생성합니다.")
	@PostMapping
	public ApiResponse<CreateExamResponse> createExam(
		@Valid @RequestBody CreateExamRequest request
	) {
		UUID examId = createExamUseCase.execute(request.toUseCaseDto());
		return ApiResponse.success(CreateExamResponse.from(examId));
	}
}
