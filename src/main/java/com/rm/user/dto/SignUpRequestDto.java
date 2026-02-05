package com.rm.user.dto;

import java.util.List;

import com.rm.valid.Telephone;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SignUpRequestDto(
		@Valid SignRequestEssence e,
		@Schema(description = "공백불가")
		@NotBlank String name,
		@Telephone String phoneNumber,
		@Email@NotNull String email,
		@NotNull List<String> roles
	) {

}
