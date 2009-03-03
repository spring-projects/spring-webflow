package org.springframework.binding.validation;

public class LengthConstraint {

	private Integer min;

	private Integer max;

	public LengthConstraint(int min, int max) {
		this.min = new Integer(min);
		this.max = new Integer(max);
	}

	public Integer getMin() {
		return min;
	}

	public Integer getMax() {
		return max;
	}

	public void validate(String value, ValidationContext context) {
		if (value == null || value.length() < min.intValue() || value.length() > max.intValue()) {
			context.addFailure(createFailure());
		}
	}

	protected ValidationFailure createFailure() {
		return new ValidationFailureBuilder().arg("min", getMin()).arg("max", getMax()).build();
	}

}
