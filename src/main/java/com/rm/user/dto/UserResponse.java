package com.rm.user.dto;

public record UserResponse<T>(
		boolean success,
		String code,
		String msg,
		T data
	) {
	public static <T> UserResponse<T> success(T data){
		return new UserResponse<>(
			true,
			ErrorCode.SUCCESS.getCode(),
			ErrorCode.SUCCESS.getMsg(),
			data
		);
	}
	public static <T> UserResponse<T> fail(ErrorCode errorCode){
		return new UserResponse<>(
			false,
			errorCode.getCode(),
			errorCode.getMsg(),
			null
		);
	}
}
