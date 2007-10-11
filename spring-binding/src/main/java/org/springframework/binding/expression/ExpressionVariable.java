package org.springframework.binding.expression;

import org.springframework.util.Assert;

/**
 * A simple, convenient alias for a more-complex expression.
 * 
 * TODO - consider making the valueExpressionString a parsed Expression object for more flexibility.
 * 
 * @author Keith Donald
 */
public class ExpressionVariable {

	private String name;

	private String valueExpressionString;

	/**
	 * Creates a new expression variable
	 * @param name the name of the variable, acting as an convenient alias
	 * @param valueExpressionString the complex expression to be aliased in string form
	 */
	public ExpressionVariable(String name, String valueExpressionString) {
		Assert.hasText(name, "The expression variable must be named");
		Assert.hasText(valueExpressionString, "The expression value expression string is required");
		this.name = name;
		this.valueExpressionString = valueExpressionString;
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
	public String getValueExpressionString() {
		return valueExpressionString;
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
