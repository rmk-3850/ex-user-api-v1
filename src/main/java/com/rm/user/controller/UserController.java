package com.rm.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.rm.user.dto.SignInResponseDto;
import com.rm.user.dto.SignRequestEssence;
import com.rm.user.dto.SignUpRequestDto;
import com.rm.user.dto.SignUpResponseDto;
import com.rm.user.dto.UpdateRequestDto;
import com.rm.user.dto.UserResponse;
import com.rm.user.service.SignService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {
	private final SignService signService;
	
	@Operation(description = "uid 중복 조회")
	@GetMapping("/auth/uid/{uid}")
	public ResponseEntity<Boolean> isDuplicationUid(
			@Parameter(description = "아이디",required = true)
			@PathVariable("uid") String uid
		){
		return ResponseEntity.ok(signService.existsByUid(uid));
	}
	
	@Operation(description = "email 중복 조회")
	@GetMapping("/auth/email/{email}")
	public ResponseEntity<Boolean> isDuplicationEmail(
			@Parameter(description = "아이디",required = true)
			@PathVariable("email") String email
		){
		return ResponseEntity.ok(signService.existsByEmail(email));
	}
	
	@Operation(description = "개인 정보 조회")
	@GetMapping("/id/{id}")
	public ResponseEntity<UserResponse<SignUpResponseDto>> select(
			@Parameter(description = "id",required = true)
			@PathVariable("id") Long id
		){
		UserResponse<SignUpResponseDto> data=signService.select(id);
		return ResponseEntity.status(data.status()).body(data);
	}
	
	@Operation(description = "로그인")
	@GetMapping("/auth")
	public ResponseEntity<UserResponse<SignInResponseDto>> signIn(
			@Valid@RequestBody SignRequestEssence dto
		){
		UserResponse<SignInResponseDto> data=signService.signIn(dto);
		return ResponseEntity.status(data.status()).body(data);
	}
	
	@Operation(description = "회원 가입")
	@PostMapping("/auth")
	public ResponseEntity<UserResponse<SignUpResponseDto>> signUp(
			@Valid@RequestBody SignUpRequestDto dto
		){
		UserResponse<SignUpResponseDto> data=signService.signUp(dto);
		return ResponseEntity.status(data.status()).body(data);
	}
	
	@Operation(description = "회원 정보 수정")
	@PutMapping("/id/{id}")
	public ResponseEntity<UserResponse<SignUpResponseDto>> update(
			@Parameter(description = "id",required = true)
			@PathVariable("id") Long id,
			@Valid@RequestBody UpdateRequestDto dto
		){
		UserResponse<SignUpResponseDto> data=signService.update(id,dto);
		return ResponseEntity.status(data.status()).body(data);
	}
	
	@Operation(description = "회원 탈퇴")
	@DeleteMapping("/id/{id}")
	public ResponseEntity<UserResponse<Void>> delete(
			@Parameter(description = "id",required = true)
			@PathVariable("id") Long id
		){
		UserResponse<Void> data=signService.delete(id);
		return ResponseEntity.status(data.status()).body(data);
	}
}
