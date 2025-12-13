package com.voco.voco.common.adaptor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.voco.voco.common.interfaces.PasswordAdaptor;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PasswordAdaptorImpl implements PasswordAdaptor {

	private final PasswordEncoder passwordEncoder;

	@Override
	public String encode(String rawPassword) {
		return passwordEncoder.encode(rawPassword);
	}

	@Override
	public boolean matches(String rawPassword, String encodedPassword) {
		return passwordEncoder.matches(rawPassword, encodedPassword);
	}
}