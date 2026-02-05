package com.rm.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rm.user.dto.ErrorCode;
import com.rm.user.dto.SignInResponseDto;
import com.rm.user.dto.SignRequestEssence;
import com.rm.user.dto.SignResponseEssence;
import com.rm.user.dto.SignUpRequestDto;
import com.rm.user.dto.SignUpResponseDto;
import com.rm.user.dto.UpdateRequestDto;
import com.rm.user.dto.UserResponse;
import com.rm.user.entity.User;
import com.rm.user.infra.JwtTokenProvider;
import com.rm.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class SignService {
	private final UserRepository userRepository;
	private final JwtTokenProvider tokenProvider;
	private final PasswordEncoder passwordEncoder;
	
	public boolean existsByUid(String uid) {
		return userRepository.existsByUid(uid);
	}
	
	public boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}
	
	public User getUserOrThrow(Long id) {
		return userRepository.findById(id).orElseThrow(()->new RuntimeException());
	}
	
	public boolean passwordIsMatch(Long id,String password) {
		User user=getUserOrThrow(id);
		return passwordEncoder.matches(password, user.getPassword());
	}
	
	@Transactional
	public UserResponse<SignUpResponseDto> signUp(SignUpRequestDto dto) {
		User savedUser=userRepository.save(
			User.builder()
				.uid(dto.e().uid())
				.password(passwordEncoder.encode(dto.e().password()))
				.name(dto.name())
				.email(dto.email())
				.phoneNumber(dto.phoneNumber())
				.roles(dto.roles())
				.build()
		);		
		return UserResponse.success(SignUpResponseDto.from(savedUser));
	}
	
	@Transactional
	public UserResponse<SignInResponseDto> signIn(SignRequestEssence dto){
		User user=userRepository.getByUid(dto.uid());
		if(user==null) throw new RuntimeException();
		if(!passwordEncoder.matches(dto.password(), user.getPassword())) throw new RuntimeException();
		return UserResponse.success(new SignInResponseDto(
				new SignResponseEssence(user.getId(), user.getUid(), user.getName()),
				tokenProvider.createToken(user.getUid(), user.getRoles())
		));
	}
	
	@Transactional
	public UserResponse<SignUpResponseDto> update(Long id,UpdateRequestDto dto){
		User user=getUserOrThrow(id);
		user.update(
			dto.name(),
			passwordEncoder.encode(dto.e().password()),
			dto.phoneNumber()
		);
		return UserResponse.success(new SignUpResponseDto(
				new SignResponseEssence(user.getId(), user.getUid(), user.getName()),
				user.getPhoneNumber(),
				user.getEmail()
		));
	}
	
	@Transactional
	public UserResponse<Void> delete(Long id){
		User user=getUserOrThrow(id);
		userRepository.delete(user);
		return new UserResponse<>(true, ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMsg(), null);
	}
}
