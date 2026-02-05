package com.rm.valid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String>{
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if(value==null) return false;
		return value.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*?])[A-Za-z\\d!@#$%^&*?]{8,}$");
	}
}
