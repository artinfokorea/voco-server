package com.voco.voco.app.auth.infrastructure.adaptor;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.voco.voco.app.auth.application.interfaces.JwtProvider;
import com.voco.voco.common.annotation.MemberId;
import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class MemberIdArgumentResolver implements HandlerMethodArgumentResolver {

	private final JwtProvider jwtProvider;

	public MemberIdArgumentResolver(JwtProvider jwtProvider) {
		this.jwtProvider = jwtProvider;
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(MemberId.class) &&
			parameter.getParameterType().equals(Long.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter,
		ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest,
		WebDataBinderFactory binderFactory) {
		HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest();
		String authorizationHeader = request.getHeader("Authorization");

		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			throw new CoreException(ApiErrorType.TOKEN_NOT_FOUND);
		}

		String token = authorizationHeader.substring(7);
		return jwtProvider.extractMemberId(token);
	}
}