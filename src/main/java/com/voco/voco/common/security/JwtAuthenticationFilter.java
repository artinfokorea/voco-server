package com.voco.voco.common.security;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.voco.voco.common.exception.CoreException;
import com.voco.voco.common.interfaces.JwtAdaptor;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtAdaptor jwtAdaptor;

	public JwtAuthenticationFilter(JwtAdaptor jwtAdaptor) {
		this.jwtAdaptor = jwtAdaptor;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		String token = resolveToken(request);

		try {
			if (token != null && jwtAdaptor.validateToken(token)) {
				Authentication authentication = jwtAdaptor.getAuthentication(token);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (CoreException e) {
			request.setAttribute("JWT_EXCEPTION", e);

			throw new AuthenticationServiceException(e.getMessage(), e);
		}

		filterChain.doFilter(request, response);
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}

		return null;
	}
}
