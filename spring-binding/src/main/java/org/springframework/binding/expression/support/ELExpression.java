package org.springframework.binding.expression.support;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ValueExpression;

import org.springframework.binding.expression.EvaluationAttempt;
import org.springframework.binding.expression.EvaluationContext;
import org.springframework.binding.expression.EvaluationException;
import org.springframework.binding.expression.SettableExpression;

/**
 * Evaluates a parsed EL expression.
 * 
 * @author Jeremy Grelle
 */
public class ELExpression implements SettableExpression {

	ValueExpression expression;

	public ELExpression(ValueExpression expression) {
		this.expression = expression;
	}

	public void evaluateToSet(Object target, Object value, EvaluationContext context) throws EvaluationException {
		ELContext ctx = getELContext(target);
		try {
			expression.setValue(ctx, value);
		}
		catch (ELException ex) {
			throw new EvaluationException(new EvaluationAttempt(this, target, context), ex);
		}
	}

	public Object evaluate(Object target, EvaluationContext context) throws EvaluationException {
		ELContext ctx = getELContext(target);
		try {
			return expression.getValue(ctx);
		} 
		catch (ELException ex) {
			throw new EvaluationException(new EvaluationAttempt(this, target, context), ex);
		}
	}
	
	protected Class getType(Object target, EvaluationContext context) throws EvaluationException{
		ELContext ctx = getELContext(target);
		try {
			return expression.getType(ctx);
		} 
		catch (ELException ex) {
			throw new EvaluationException(new EvaluationAttempt(this, target, context), ex);
		}
	}
	
	/**
	 * Retrieves the thread-bound {@link ELContext} instance, configured with a DefaultELResolver if
	 * no other resolvers have been configured.
	 * 
	 * @return {@link ELContext} The thread-bound {@link ELContext} instance.
	 */
	private ELContext getELContext(Object target) {
		ELContext ctx = DelegatingELContext.getCurrentInstance();
		if (ctx.getELResolver() == null)
			((DelegatingELContext)ctx).setELResolver(new DefaultELResolver());
		if (ctx.getELResolver() instanceof DefaultELResolver)
			((DefaultELResolver)ctx.getELResolver()).setTarget(target);
		return ctx;
	}

	public int hashCode() {
		return expression.hashCode();
	}

	public boolean equals(Object o) {
		if (!(o instanceof ELExpression)) {
			return false;
		}
		ELExpression other = (ELExpression) o;
		return expression.equals(other.expression);
	}
	
	public String toString()
	{
		return expression.getExpressionString();
	}
	

}
