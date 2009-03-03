package org.springframework.binding.validation;

import org.springframework.util.ObjectUtils;

public class ConfirmationConstraint {

	public void validate(ConfirmationForm form, ValidationContext context) {
		if (form.getValue() == null) {
			return;
		}
		if (!ObjectUtils.nullSafeEquals(form.getValue(), form.getConfirmedValue())) {
			context.addDefaultFailure();
		}
	}

	public static class ConfirmationForm {

		private Object value;

		private Object confirmedValue;

		public ConfirmationForm(String value, String confirmedValue) {
			this.value = value;
			this.confirmedValue = confirmedValue;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		public Object getConfirmedValue() {
			return confirmedValue;
		}

		public void setConfirmedValue(Object confirmedValue) {
			this.confirmedValue = confirmedValue;
		}

	}

}