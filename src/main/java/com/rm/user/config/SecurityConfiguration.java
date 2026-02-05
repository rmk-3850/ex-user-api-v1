package com.rm.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.rm.user.exception.CustomAccessDeniedHandler;
import com.rm.user.exception.CustomAuthenticationEntryPoint;
import com.rm.user.filter.JwtAuthenticationFilter;
import com.rm.user.infra.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfiguration {
	private final JwtTokenProvider tokenProvider;
	
	public SecurityFilterChain securityFilterChain(HttpSecurity http) {
		http.httpBasic(httpBasic->httpBasic.disable())
			.csrf(csrf->csrf.disable())
			.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth->auth
				.anyRequest().hasRole("USER")
			)
			.exceptionHandling(exception->exception
				.authenticationEntryPoint(new CustomAuthenticationEntryPoint())
				.accessDeniedHandler(new CustomAccessDeniedHandler())
			)
			.addFilterBefore(new JwtAuthenticationFilter(tokenProvider),
				UsernamePasswordAuthenticationFilter.class
			);
		return http.build();
	}
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(
            "/v2/api-docs",
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/swagger/**"
        );
    }
}
