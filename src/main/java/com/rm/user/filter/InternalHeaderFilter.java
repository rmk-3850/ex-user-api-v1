package com.rm.user.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.rm.user.exception.UserNotFoundException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class InternalHeaderFilter extends OncePerRequestFilter{
	private final HandlerExceptionResolver resolver;
	
	public InternalHeaderFilter(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
		this.resolver=resolver;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String path=request.getRequestURI();
		
		if(path.startsWith("/v2/api-docs")||path.startsWith("/swagger")) {
			filterChain.doFilter(request, response);
			return;
		}
		
		String uid=request.getHeader("X-User-Uid");
		String roles=request.getHeader("X-User-Roles");
		try {
			if(uid==null || uid.isBlank()) {
				log.error("[doFilterInternal] 인증 헤더 누락");
				throw new UserNotFoundException();
			}			
		} catch (Exception e) {
			resolver.resolveException(request, response, null, e);
			return;
		}
		List<GrantedAuthority> authorities=AuthorityUtils.commaSeparatedStringToAuthorityList(roles);
		UsernamePasswordAuthenticationToken authentication=new UsernamePasswordAuthenticationToken(
				uid, null, authorities);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		filterChain.doFilter(request, response);
	}
}
