package com.voco.voco.common.interfaces;

public interface AdminChecker {

	boolean isAdmin(Long memberId);

	void validateAdmin(Long memberId);
}
