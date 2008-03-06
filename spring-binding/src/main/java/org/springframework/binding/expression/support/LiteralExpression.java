package org.springframework.binding.expression.support;

import org.springframework.binding.expression.EvaluationException;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.SetValueAttempt;
import org.springframework.util.Assert;

public class LiteralExpression implements Expression {

	/**
	 * The string literal.
	 */
	private String literal;

	/**
	 * Create a literal expression for the given literal.
	 * @param literal the literal
	 */
	public LiteralExpression(String literal) {
		Assert.notNull(literal, "The literal is required");
		this.literal = literal;
	}

	public int hashCode() {
		return literal.hashCode();
	}

	public boolean equals(Object o) {
		if (!(o instanceof LiteralExpression)) {
			return false;
		}
		LiteralExpression other = (LiteralExpression) o;
		return literal.equals(other.literal);
	}

	public Object getValue(Object context) throws EvaluationException {
		return literal;
	}

	public void setValue(Object context, Object value) throws EvaluationException {
		throw new EvaluationException(new SetValueAttempt(this, context, value), new UnsupportedOperationException(
				"Cannot set a literal expression value.  Are you attempting to set a property expression?  "
						+ "If so, should the expression string be enclosed in eval delimiters?"));
	}

	public String toString() {
		return "literal('" + literal + "')";
	}
}
