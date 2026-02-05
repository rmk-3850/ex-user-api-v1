package com.rm.user.exception;

import com.rm.exception.BusinessException;
import com.rm.user.dto.ErrorCode;

public class UserNotFoundException extends BusinessException {
	public UserNotFoundException() {
		super(ErrorCode.USER_NOT_FOUND);
	}
}
