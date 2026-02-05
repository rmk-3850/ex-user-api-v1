package com.rm.exception;

import com.rm.user.dto.ErrorCode;

public abstract class BusinessException extends RuntimeException{
	private final ErrorCode errorCode;
	protected BusinessException(ErrorCode errorCode) {
		this.errorCode=errorCode;
	}
	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
