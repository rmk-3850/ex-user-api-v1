package com.rm.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	SUCCESS("S001","정상 처리됐습니다."),
	INVALID_IDPASSWORD("E101","아이디나 비밀번호가 틀렸습니다.");	
	
	private final String code;
	private final String msg;
}
