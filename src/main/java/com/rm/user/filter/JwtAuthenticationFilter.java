package com.rm.user.filter;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.rm.user.infra.JwtTokenProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
@RequiredArgsConstructor
@Log4j2
public class JwtAuthenticationFilter extends OncePerRequestFilter{
	private final JwtTokenProvider provider;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String token=provider.resolveToken(request);
		log.info("[doFilterInternal] token 추출 완료. token : {}",token);
		log.info("[doFilterInternal] token 유효성 체크 시작");
		if(token !=null && provider.isValidToken(token)) {
			Authentication authentication=provider.getAuthentication(token);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			log.info("[doFilterInternal] token 유효성 체크 완료");
		}
		filterChain.doFilter(request, response);
	}
}
