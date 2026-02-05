package com.rm.user.dto;

import org.springframework.http.HttpStatus;

public record UserResponse<T>(
		boolean success,
		HttpStatus status,
		String code,
		String msg,
		T data
	) {
	public static <T> UserResponse<T> success(T data){
		return new UserResponse<>(
			true,
			ErrorCode.SUCCESS.getStatus(),
			ErrorCode.SUCCESS.getCode(),
			ErrorCode.SUCCESS.getMsg(),
			data
		);
	}
	public static <T> UserResponse<T> fail(ErrorCode errorCode){
		return new UserResponse<>(
			false,
			errorCode.getStatus(),
			errorCode.getCode(),
			errorCode.getMsg(),
			null
		);
	}
}
