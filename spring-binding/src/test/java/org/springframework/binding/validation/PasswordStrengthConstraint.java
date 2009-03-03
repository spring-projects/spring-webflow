package org.springframework.binding.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordStrengthConstraint {

	private Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]$");

	public void validate(String password, ValidationContext context) {
		Matcher matcher = pattern.matcher(password);
		if (!matcher.find()) {
			context.addFailure(createFailure());
		}
	}

	protected ValidationFailure createFailure() {
		return new ValidationFailureBuilder().warning().detail("cause").detail("recommendedAction")
				.build();
	}

}