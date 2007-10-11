package org.springframework.binding.message;

import org.springframework.core.enums.StaticLabeledEnum;

/**
 * Enum exposing supported message severities.
 * 
 * @author Keith Donald
 * @see Message
 */
public class Severity extends StaticLabeledEnum {

	/**
	 * The "Informational" severity. Used to indicate a successful operation or result.
	 */
	public static final Severity INFO = new Severity(0, "Info");

	/**
	 * The "Warning" severity. Used to indicate there is a minor problem, or to inform the message receiver of possible
	 * misuse, or to indicate a problem may arise in the future.
	 */
	public static final Severity WARNING = new Severity(1, "Warning");

	/**
	 * THe "Error" severity. Used to indicate a significant problem like a business rule violation.
	 */
	public static final Severity ERROR = new Severity(2, "Error");

	private Severity(int code, String label) {
		super(code, label);
	}
}
