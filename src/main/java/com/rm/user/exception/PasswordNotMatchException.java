package com.rm.user.exception;

import com.rm.exception.BusinessException;
import com.rm.user.dto.ErrorCode;

public class PasswordNotMatchException extends BusinessException{
	public PasswordNotMatchException() {
		super(ErrorCode.INVALID_IDPASSWORD);
	}
}
