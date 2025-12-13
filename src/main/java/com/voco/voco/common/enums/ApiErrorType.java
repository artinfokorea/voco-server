package com.voco.voco.common.enums;

import org.springframework.boot.logging.LogLevel;

import lombok.Getter;

@Getter
public enum ApiErrorType {
	// system
	INTERNAL_SERVER_ERROR(ApiErrorCode.SERVER_ERROR, "SYSTEM-1", "서버에서 오류가 발생했습니다. 요청을 처리할 수 없습니다.", LogLevel.ERROR),
	BAD_REQUEST(ApiErrorCode.CLIENT_ERROR, "SYSTEM-2", "유효하지 않은 요청입니다.", LogLevel.WARN),

	// Auth
	TOKEN_NOT_FOUND(ApiErrorCode.UNAUTHORIZED, "AUTH-1", "토큰이 존재하지 않습니다.", LogLevel.WARN),
	TOKEN_EXPIRED(ApiErrorCode.UNAUTHORIZED, "AUTH-2", "토큰이 만료되었습니다.", LogLevel.WARN),
	INVALID_SIGNATURE(ApiErrorCode.UNAUTHORIZED, "AUTH-3", "토큰 서명이 유효하지 않습니다.", LogLevel.WARN),
	MALFORMED_TOKEN(ApiErrorCode.CLIENT_ERROR, "AUTH-4", "잘못된 형식의 토큰입니다.", LogLevel.WARN),
	UNSUPPORTED_TOKEN(ApiErrorCode.CLIENT_ERROR, "AUTH-5", "지원하지 않는 토큰입니다.", LogLevel.WARN),
	INVALID_TOKEN(ApiErrorCode.CLIENT_ERROR, "AUTH-6", "유효하지 않은 토큰입니다.", LogLevel.WARN),

	// MEMBER
	MEMBER_NOT_FOUND(ApiErrorCode.NOT_FOUND, "MEMBER-1", "해당 유저가 존재하지 않습니다.",
		LogLevel.WARN),
	INVALID_PASSWORD(ApiErrorCode.CLIENT_ERROR, "MEMBER-2", "비밀번호는 최소 8자 이상이며, 영문자, 숫자, 특수문자를 각각 하나 이상 포함해야 합니다.",
		LogLevel.WARN),
	INVALID_PASSWORD_MISMATCH(ApiErrorCode.CLIENT_ERROR, "MEMBER-3", "비밀번호가 일치하지 않습니다.", LogLevel.WARN),
	DUPLICATED_EMAIL(ApiErrorCode.CLIENT_ERROR, "MEMBER-4", "이미 사용 중인 이메일입니다.", LogLevel.WARN),

	// NOTIFICATION
	DUPLICATED_NOTIFICATION_SCHEDULE(ApiErrorCode.CLIENT_ERROR, "NOTIFICATION-1", "해당 요일에 이미 알림 스케줄이 존재합니다.", LogLevel.WARN);

	private final ApiErrorCode errorCode;
	private final String errorNo;
	private final String message;
	private final LogLevel logLevel;

	ApiErrorType(ApiErrorCode errorCode, String errorNo, String message, LogLevel logLevel) {
		this.errorCode = errorCode;
		this.errorNo = errorNo;
		this.message = message;
		this.logLevel = logLevel;
	}
}
