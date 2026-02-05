package com.rm.user.dto;

import com.rm.valid.Telephone;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record UpdateRequestDto(
		@Valid SignRequestEssence e,
		@Schema(description = "공백불가")
		@NotBlank String name,
		@Telephone String phoneNumber
	) {

}
