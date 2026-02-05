package com.rm.user.dto;

import com.rm.user.entity.User;

public record SignUpResponseDto(
		SignResponseEssence e,
		String phoneNumber,
		String email
	) {
	public static SignUpResponseDto from(User entity) {
		return new SignUpResponseDto(
			new SignResponseEssence(entity.getId(), entity.getUid(), entity.getName()),
			entity.getPhoneNumber(),
			entity.getEmail()
		);
	}
}
