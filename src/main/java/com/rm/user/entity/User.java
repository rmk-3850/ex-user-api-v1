package com.rm.user.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
	name = "user",
	uniqueConstraints = {@UniqueConstraint(columnNames = "email")}
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails{
	
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false, unique = true)
	private String uid;
	
	@Column(nullable = false)
	private String name;
	
	@JsonProperty(access = Access.WRITE_ONLY)
	@Column(nullable = false)
	private String password;
	
	@Column(nullable = false,length = 20)
	private String phoneNumber;
	
	@Column(nullable = false,length = 255)
	private String email;
	
	@ElementCollection(fetch = FetchType.EAGER)
	@Builder.Default
	private List<String> roles=new ArrayList<String>();
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	}
	
	@JsonProperty(access = Access.WRITE_ONLY)
	@Override
	public String getUsername() {
		return uid;
	}
	@JsonProperty(access = Access.WRITE_ONLY)
	@Override
	public boolean isAccountNonExpired() {
		return UserDetails.super.isAccountNonExpired();
	}
	@JsonProperty(access = Access.WRITE_ONLY)
	@Override
	public boolean isAccountNonLocked() {
		return UserDetails.super.isAccountNonLocked();
	}
	@JsonProperty(access = Access.WRITE_ONLY)
	@Override
	public boolean isCredentialsNonExpired() {
		return UserDetails.super.isCredentialsNonExpired();
	}
	@JsonProperty(access = Access.WRITE_ONLY)
	@Override
	public boolean isEnabled() {
		return UserDetails.super.isEnabled();
	}
	
	public void update(String name,String password,String phoneNumber) {
		this.name=name;
		this.password=password;
		this.name=phoneNumber;
	}
}
