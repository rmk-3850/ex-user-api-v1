package com.rm.user.dto;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	SUCCESS(HttpStatus.ACCEPTED,"S001","정상 처리됐습니다."),
	INVALID_AUTHENTICATION(HttpStatus.UNAUTHORIZED,"AUTH-001","로그인이 필요합니다."),
	ACCESS_DENIED(HttpStatus.FORBIDDEN,"AUTH-002","접근 권한이 없습니다."),
	INVALID_IDPASSWORD(HttpStatus.FORBIDDEN,"E101","아이디나 비밀번호가 틀렸습니다."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND,"E102","사용자를 찾을 수 없습니다."),	
	SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"E500","서버 내부에서 오류가 발생했습니다.");
	private final HttpStatus status;
	private final String code;
	private final String msg;
}
