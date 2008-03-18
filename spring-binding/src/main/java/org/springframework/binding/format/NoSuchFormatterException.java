package org.springframework.binding.format;

public class NoSuchFormatterException extends RuntimeException {

	private Class formatterFormattedClass;

	private String formatterId;

	public NoSuchFormatterException(Class formatterFormattedClass) {
		super("No default formatter for class " + formatterFormattedClass + "; make sure a formatter is registered");
		this.formatterFormattedClass = formatterFormattedClass;
	}

	public NoSuchFormatterException(String formatterId, Class formatterFormattedClass) {
		super("No custom formatter could be found with id '" + formatterId + "' for class " + formatterFormattedClass
				+ "; check your spelling and make sure the formatter is registered");
		this.formatterId = formatterId;
	}

	public Class getFormatterFormattedClass() {
		return formatterFormattedClass;
	}

	public String getFormatterId() {
		return formatterId;
	}

}
