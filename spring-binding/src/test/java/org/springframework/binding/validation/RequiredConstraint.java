package org.springframework.binding.validation;

public class RequiredConstraint {

	public void validate(Object value, ValidationContext context) {
		if (value == null) {
			context.addDefaultFailure();
		}
	}

}