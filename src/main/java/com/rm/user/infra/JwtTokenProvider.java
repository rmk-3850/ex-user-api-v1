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
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class JwtTokenProvider {
	private final Key key;	
	private final Clock clock;	
	private final long tokenValidMillisecond;
	
	public JwtTokenProvider(
			Clock clock,
			@Value("${jwt.secret}") String base64Key,
			@Value("${jwt.token-valid-ms}") long tokenValidMillisecond) {
		byte[] keyBytes=Base64.getDecoder().decode(base64Key);
		this.key=Keys.hmacShaKeyFor(keyBytes);
		this.clock=clock;
		this.tokenValidMillisecond=tokenValidMillisecond;
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
}
