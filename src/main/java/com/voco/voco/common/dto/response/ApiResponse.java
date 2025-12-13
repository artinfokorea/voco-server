package com.voco.voco.common.dto.response;

import com.voco.voco.common.enums.ApiResultType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.ExceptionMessage;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiResponse<T> {

	@Schema(description = "결과 타입", example = "FAIL")
	private ApiResultType type;

	private ExceptionMessage exception;

	private T item;

	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(ApiResultType.SUCCESS, null, data);
	}

	public static <T> ApiResponse<T> success() {
		return new ApiResponse<>(ApiResultType.SUCCESS, null, null);
	}

	public static <T> ApiResponse<T> fail(ApiErrorType errorType) {
		return new ApiResponse<>(ApiResultType.FAIL, new ExceptionMessage(errorType), null);
	}

	public ApiResponse(ApiResultType resultType, ExceptionMessage exception, T data) {
		this.type = resultType;
		this.exception = exception;
		this.item = data;
	}
}
