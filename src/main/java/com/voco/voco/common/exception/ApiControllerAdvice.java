package com.voco.voco.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import lombok.extern.slf4j.Slf4j;
import com.voco.voco.common.dto.response.ApiResponse;
import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.enums.ApiResultType;

@Slf4j
@RestControllerAdvice
class ApiControllerAdvice {
	@ExceptionHandler(CoreException.class)
	public ResponseEntity<Object> handleCoreException(CoreException e) {
		switch (e.getErrorType().getLogLevel()) {
			case ERROR -> log.error("{}", e.getMessage());
			case WARN -> log.warn("{}", e.getMessage());
			default -> log.info("{}", e.getMessage());
		}

		HttpStatus status;
		switch (e.getErrorType().getErrorCode()) {
			case DB_ERROR -> status = HttpStatus.INTERNAL_SERVER_ERROR;
			case UNAUTHORIZED -> status = HttpStatus.UNAUTHORIZED;
			case FORBIDDEN -> status = HttpStatus.FORBIDDEN;
			case CLIENT_ERROR -> status = HttpStatus.BAD_REQUEST;
			case NOT_FOUND -> status = HttpStatus.NOT_FOUND;
			default -> status = HttpStatus.OK;
		}

		ApiResponse<Object> response = ApiResponse.fail(e.getErrorType());
		return new ResponseEntity<>(response, status);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> invalidRequestHandler(MethodArgumentNotValidException e) {
		ExceptionMessage exceptionMessage = new ExceptionMessage(ApiErrorType.BAD_REQUEST);
		ApiResponse<Object> response = new ApiResponse<>(ApiResultType.FAIL, exceptionMessage, null);

		for (FieldError fieldError : e.getFieldErrors()) {
			exceptionMessage.addValidation(fieldError.getField(), fieldError.getDefaultMessage());
		}
		return new ResponseEntity<>(response, e.getStatusCode());
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
		Throwable cause = e.getCause();
		ExceptionMessage exceptionMessage = new ExceptionMessage(ApiErrorType.BAD_REQUEST);

		if (cause instanceof InvalidFormatException) {
			InvalidFormatException ife = (InvalidFormatException)cause;
			String fieldName = ife.getPath().get(1).getFieldName();
			String invalidValue = ife.getValue().toString();
			String errorMessage = String.format("%s의 필드 값 '%s'가 올바르지 않습니다", fieldName, invalidValue);

			exceptionMessage.addValidation(fieldName, errorMessage);
		}

		ApiResponse<Object> response = new ApiResponse<>(ApiResultType.FAIL, exceptionMessage, null);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<Object> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
		String fieldName = e.getName();
		String value = e.getValue() != null ? e.getValue().toString() : "null";
		String errorMessage = String.format("'%s' 값 '%s'는 유효하지 않습니다.", fieldName, value);

		ExceptionMessage exceptionMessage = new ExceptionMessage(ApiErrorType.BAD_REQUEST);
		exceptionMessage.addValidation(fieldName, errorMessage);

		ApiResponse<Object> response = new ApiResponse<>(ApiResultType.FAIL, exceptionMessage, null);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleException(Exception e) {
		log.error("Exception class: {}", e.getClass().getName());
		log.error("Exception message: {}", e.getMessage());

		ExceptionMessage exceptionMessage = new ExceptionMessage(ApiErrorType.INTERNAL_SERVER_ERROR);
		ApiResponse<Object> response = new ApiResponse<>(ApiResultType.FAIL, exceptionMessage, null);

		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
