package org.springframework.webflow.config;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class EmptySpringValidator implements Validator {

	public boolean supports(Class<?> clazz) {
		return false;
	}

	public void validate(Object target, Errors errors) {
	}

}
