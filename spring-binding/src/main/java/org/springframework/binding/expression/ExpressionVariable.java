package org.springframework.binding.expression;

import org.springframework.util.Assert;

/**
 * An expression variable.
 * @author Keith Donald
 */
public class ExpressionVariable {

	private String name;

	private String value;

	/**
	 * Creates a new expression variable
	 * @param name the name of the variable, acting as an convenient alias
	 * @param value the initial value of the variable
	 */
	public ExpressionVariable(String name, String value) {
		Assert.hasText(name, "The expression variable must be named");
		Assert.hasText(value, "The expression variable value is required");
		this.name = name;
		this.value = value;
	}

	/**
	 * Returns the variable name, typically vary simple like "index".
	 * @return the variable name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the expression that will be evaluated when the variable is referenced by its name in another expression.
	 * @return the expression value.
	 */
	public String getValue() {
		return value;
	}

	public boolean equals(Object o) {
		if (!(o instanceof ExpressionVariable)) {
			return false;
		}
		ExpressionVariable var = (ExpressionVariable) o;
		return name.equals(var.name);
	}

	public int hashCode() {
		return name.hashCode();
	}

	public String toString() {
		return "[Expression Variable '" + name + "']";
	}
}
