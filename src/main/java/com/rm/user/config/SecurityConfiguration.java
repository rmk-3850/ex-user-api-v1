package com.rm.user.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.rm.user.exception.CustomAccessDeniedHandler;
import com.rm.user.exception.CustomAuthenticationEntryPoint;
import com.rm.user.filter.InternalHeaderFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
	private final HandlerExceptionResolver resolver;
	
	public SecurityConfiguration(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
		this.resolver=resolver;
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http,
			CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
			CustomAccessDeniedHandler customAccessDeniedHandler
			) {
		http.httpBasic(httpBasic->httpBasic.disable())
			.csrf(csrf->csrf.disable())
			.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth->auth
				.requestMatchers(
		            "/v2/api-docs",
		            "/swagger-resources/**",
		            "/swagger-ui.html",
		            "/swagger/**").permitAll()
				.anyRequest().hasRole("USER")
			)
			.exceptionHandling(exception->exception
				.authenticationEntryPoint(customAuthenticationEntryPoint)
				.accessDeniedHandler(customAccessDeniedHandler)
			)
			.addFilterBefore(new InternalHeaderFilter(resolver),
				UsernamePasswordAuthenticationFilter.class
			);
		return http.build();
	}
}
