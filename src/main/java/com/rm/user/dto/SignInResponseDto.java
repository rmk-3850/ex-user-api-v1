package com.rm.user.dto;

public record SignInResponseDto(
		SignResponseEssence e,
		String token
	) {

}
