package com.voco.voco.common.annotation;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;
import com.voco.voco.common.interfaces.AdminChecker;
import com.voco.voco.common.interfaces.JwtAdaptor;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class AdminIdArgumentResolver implements HandlerMethodArgumentResolver {

	private final JwtAdaptor jwtAdaptor;
	private final AdminChecker adminChecker;

	public AdminIdArgumentResolver(JwtAdaptor jwtAdaptor, AdminChecker adminChecker) {
		this.jwtAdaptor = jwtAdaptor;
		this.adminChecker = adminChecker;
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(AdminId.class) &&
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
		Long memberId = jwtAdaptor.extractMemberId(token);

		adminChecker.validateAdmin(memberId);

		return memberId;
	}
}
