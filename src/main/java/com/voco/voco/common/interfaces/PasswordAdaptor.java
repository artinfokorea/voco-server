package com.voco.voco.common.interfaces;

public interface PasswordAdaptor {

	String encode(String rawPassword);

	boolean matches(String rawPassword, String encodedPassword);
}