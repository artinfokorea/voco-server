package com.voco.voco.common.exception;

import lombok.Getter;
import com.voco.voco.common.enums.ApiErrorType;

@Getter
public class CoreException extends RuntimeException {
	private final ApiErrorType errorType;

	public CoreException(ApiErrorType errorType) {
		super(errorType.getMessage());
		this.errorType = errorType;
	}

	public ApiErrorType getErrorType() {
		return errorType;
	}

}