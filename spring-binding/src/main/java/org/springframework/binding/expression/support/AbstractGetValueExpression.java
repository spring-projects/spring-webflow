package org.springframework.binding.expression.support;

import org.springframework.binding.expression.EvaluationException;
import org.springframework.binding.expression.Expression;

/**
 * Trivial helper for concrete expression types that do not support setting their values. Simply throws an unsupported
 * operation exception if {@link #setValue(Object, Object)} is called.
 * 
 * Subclasses must implement {@link #getValue(Object)}.
 * 
 * @author Keith Donald
 */
public abstract class AbstractGetValueExpression implements Expression {

	public abstract Object getValue(Object context) throws EvaluationException;

	public void setValue(Object context, Object value) throws EvaluationException {
		throw new UnsupportedOperationException("Setting this expression's value is not supported");
	}

}
