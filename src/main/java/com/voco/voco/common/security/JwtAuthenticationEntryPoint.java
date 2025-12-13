package com.voco.voco.common.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.voco.voco.common.dto.response.ApiResponse;
import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
		throws IOException, ServletException {
		
		Object exception = request.getAttribute("JWT_EXCEPTION");
		ApiResponse<?> apiResponse;
		ApiErrorType errorType;

		if (exception instanceof CoreException coreEx) {
			errorType = coreEx.getErrorType();
		} else {
			errorType = ApiErrorType.INTERNAL_SERVER_ERROR;
		}

		apiResponse = ApiResponse.fail(errorType);

		HttpStatus status;
		switch (errorType.getErrorCode()) {
			case DB_ERROR -> status = HttpStatus.INTERNAL_SERVER_ERROR;
			case UNAUTHORIZED -> status = HttpStatus.UNAUTHORIZED;
			case CLIENT_ERROR -> status = HttpStatus.BAD_REQUEST;
			case NOT_FOUND -> status = HttpStatus.NOT_FOUND;
			default -> status = HttpStatus.OK;
		}

		response.setStatus(status.value());
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
	}
}