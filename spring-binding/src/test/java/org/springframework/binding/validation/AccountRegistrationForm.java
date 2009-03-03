package org.springframework.binding.validation;

import org.springframework.binding.validation.ConfirmationConstraint.ConfirmationForm;

public class AccountRegistrationForm {
	private String username;
	private String password;
	private String confirmedPassword;

	public void validate(ValidationContext context) {
		context.setProperty("username");
		context.validate(new RequiredConstraint());
		context.validate(new LengthConstraint(3, 10));

		context.setProperty("password");
		context.validate(new RequiredConstraint());
		context.validate(new LengthConstraint(6, 10));
		context.validate(new PasswordStrengthConstraint());

		context.setProperty("confirmedPassword");
		context.validate(new ConfirmationConstraint(), new ConfirmationForm(password, confirmedPassword));
	}
}
