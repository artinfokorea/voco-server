package com.voco.voco.common.exception;

import java.util.HashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import com.voco.voco.common.enums.ApiErrorType;

@Getter
public class ExceptionMessage {
	@Schema(description = "에러 번호", example = "SYSTEM_01")
	private String errorNo;

	@Schema(description = "에러 메세지", example = "MEMBER_NOT_FOUND")
	private String message;

	@Schema(description = "검증 오류 필드", example = "{\"field1\":\"error1\", \"field2\":\"error2\"}", implementation = Map.class)
	private final Map<String, String> validation = new HashMap<>();

	public ExceptionMessage(ApiErrorType errorType) {
		this.errorNo = errorType.getErrorNo();
		this.message = errorType.getMessage();
	}

	public void addValidation(String fieldName, String errorMessage) {
		this.validation.put(fieldName, errorMessage);
	}
}