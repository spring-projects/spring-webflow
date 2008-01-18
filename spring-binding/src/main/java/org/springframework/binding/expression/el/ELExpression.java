package org.springframework.binding.expression.el;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ValueExpression;
import javax.el.VariableMapper;

import org.springframework.binding.expression.EvaluationAttempt;
import org.springframework.binding.expression.EvaluationException;
import org.springframework.binding.expression.Expression;
import org.springframework.util.Assert;

/**
 * Evaluates a parsed EL expression.
 * 
 * @author Jeremy Grelle
 */
public class ELExpression implements Expression {

	private ELContextFactory elContextFactory;

	private ValueExpression valueExpression;

	private VariableMapper variableMapper;

	/**
	 * Creates a new el expression
	 * @param factory the el context factory for creating the EL context that will be used during expression evaluation
	 * @param valueExpression the value expression to evaluate
	 * @param variableMapper the variable mapper containing variables needed during expression evaluation
	 */
	public ELExpression(ELContextFactory factory, ValueExpression valueExpression, VariableMapper variableMapper) {
		Assert.notNull(factory, "The ELContextFactory is required to evaluate EL expressions");
		Assert.notNull(valueExpression, "The EL value expression is required for evaluation");
		this.elContextFactory = factory;
		this.valueExpression = valueExpression;
		this.variableMapper = variableMapper;
	}

	public Object getValue(Object context) throws EvaluationException {
		ELContext ctx = elContextFactory.getELContext(context, variableMapper);
		try {
			return valueExpression.getValue(ctx);
		} catch (ELException ex) {
			throw new EvaluationException(new EvaluationAttempt(this, context), ex);
		}
	}

	public void setValue(Object context, Object value) throws EvaluationException {
		ELContext ctx = elContextFactory.getELContext(context, variableMapper);
		try {
			valueExpression.setValue(ctx, value);
		} catch (ELException ex) {
			throw new EvaluationException(new EvaluationAttempt(this, context), ex);
		}
	}

	public int hashCode() {
		return valueExpression.hashCode();
	}

	public boolean equals(Object o) {
		if (!(o instanceof ELExpression)) {
			return false;
		}
		ELExpression other = (ELExpression) o;
		return valueExpression.equals(other.valueExpression);
	}

	public String toString() {
		return valueExpression.getExpressionString();
	}

}