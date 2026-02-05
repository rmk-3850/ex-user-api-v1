package com.rm.user.dto;

import com.rm.valid.Password;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record SignRequestEssence(
		@Schema(description = "공백불가")
		@NotBlank String uid,
		@Schema(description = "공백불가")
		@Password String password
	) {

}
