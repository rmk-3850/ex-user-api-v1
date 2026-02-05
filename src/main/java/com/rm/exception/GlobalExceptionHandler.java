package com.rm.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.rm.user.dto.ErrorCode;
import com.rm.user.dto.UserResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<UserResponse<Void>> handleBusiness(BusinessException e) {
	    return ResponseEntity.status(e.getErrorCode().getStatus()).body(UserResponse.fail(e.getErrorCode()));
	}
	
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<UserResponse<Void>> handleRuntime(RuntimeException e) {
	    return ResponseEntity.status(ErrorCode.SERVER_ERROR.getStatus()).body(UserResponse.fail(ErrorCode.SERVER_ERROR));
	}
	@ExceptionHandler(Exception.class)
	public ResponseEntity<UserResponse<Void>> handleAll(Exception e) {
	    return ResponseEntity.status(ErrorCode.SERVER_ERROR.getStatus()).body(UserResponse.fail(ErrorCode.SERVER_ERROR));
	}
}
