package com.rm.user.infra;

import java.security.Key;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class JwtTokenProvider {
	private final UserDetailsService userDetailsService;
	private final Clock clock;	
	private final long tokenValidMillisecond;
	
	public JwtTokenProvider(
			UserDetailsService userDetailsService,
			Clock clock,
			@Value("${jwt.token-valid-ms}") long tokenValidMillisecond) {
		this.userDetailsService=userDetailsService;
		this.clock=clock;
		this.tokenValidMillisecond=tokenValidMillisecond;
	}
	
	@Value("${jwt.secret}")
	private String base64Key;

	private Key key;	
	
	@PostConstruct
	private void init() {
		log.info("[init] JwtTokenProvider secretKey 초기화 시작");
		byte[] keyBytes=Base64.getDecoder().decode(base64Key);
		key=Keys.hmacShaKeyFor(keyBytes);
		log.info("[init] JwtTokenProvider secretKey 초기화 완료");
	}
	
	public String createToken(String uid,List<String> roles) {
		log.info("[createToken] 토큰 생성 시작");
		Instant now=Instant.now(clock);
		Date issuedAt=Date.from(now);
		Date expiration=Date.from(now.plusMillis(tokenValidMillisecond));
		String token=Jwts.builder()
				.setSubject(uid)
				.claim("roles", roles)
				.setIssuedAt(issuedAt)
				.setExpiration(expiration)
				.signWith(key)
				.compact();		
		log.info("[createToken] 토큰 생성 완료");
		return token;
	}
	
	public String getUsername(String token) {
		log.info("[getUsername] 토큰 기반 회원 uid 추출");
		String info=Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
		log.info("[getUsername] 토큰 기반 회원 uid 추출 완료, info : {}",info);
		return info;
	}
	
	public Authentication getAuthentication(String token) {
		log.info("[getAuthentication] 토큰 인증 정보 조회 시작");
		UserDetails user=userDetailsService.loadUserByUsername(getUsername(token));
		log.info("[getAuthentication] 토큰 인증 정보 조회 완료");
		
		return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
	}
	
	public String resolveToken(HttpServletRequest request) {
		log.info("[resolveToken] HTTP헤더에서 token 추출");
		String header=request.getHeader("Authorization");
		if(header==null || !header.regionMatches(true, 0, "Baerer ", 0, 7))
			return null;
		String token=header.substring(7).trim();
		return token.isBlank() ? null : token;
	}
	
	public boolean isValidToken(String token) {
		log.info("[isValidToken] 토큰 유효성 체크 시작");
		try {
			Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			log.info("[isValidToken] 토큰 유효하지 않음");
			return false;
		}
	}
}
