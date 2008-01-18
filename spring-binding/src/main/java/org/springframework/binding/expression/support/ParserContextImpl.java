package org.springframework.binding.expression.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.binding.expression.ExpressionVariable;
import org.springframework.binding.expression.ParserContext;

/**
 * Default implementation of the ParserContext interface that has a fluent API for building parser context attributes.
 * 
 * @author Keith Donald
 */
public class ParserContextImpl implements ParserContext {

	private Class evaluationContextType;

	private Class evaluationResultType;

	private List expressionVariables;

	/**
	 * Create a new parser context, initially with all context attributes as null. Post construction, call one or more
	 * of the fluent builder methods to configure this context.
	 * @see #eval(Class)
	 * @see #expect(Class)
	 * @see #variable(ExpressionVariable)
	 */
	public ParserContextImpl() {
		init();
	}

	public Class getEvaluationContextType() {
		return evaluationContextType;
	}

	public Class getExpectedEvaluationResultType() {
		return evaluationResultType;
	}

	public ExpressionVariable[] getExpressionVariables() {
		return (ExpressionVariable[]) expressionVariables.toArray(new ExpressionVariable[expressionVariables.size()]);
	}

	/**
	 * Configure the evaluationContextType attribute with the value provided.
	 * @param contextType the type of context object the parsed expression will evaluate in
	 * @return this
	 */
	public ParserContextImpl eval(Class contextType) {
		evaluationContextType = contextType;
		return ParserContextImpl.this;
	}

	/**
	 * Configure the expectedEvaluationResult attribute with the value provided.
	 * @param resultType the type of result object the parsed expression should return when evaluated
	 * @return this
	 */
	public ParserContextImpl expect(Class resultType) {
		evaluationResultType = resultType;
		return ParserContextImpl.this;
	}

	/**
	 * Add an expression variable that can be referenced by the expression.
	 * @param variable the expression variable
	 * @return this
	 */
	public ParserContextImpl variable(ExpressionVariable variable) {
		expressionVariables.add(variable);
		return ParserContextImpl.this;
	}

	/**
	 * Add an array of expression variables that can be referenced by the expression.
	 * @param variables the expression variables
	 * @return this
	 */
	public ParserContextImpl variables(ExpressionVariable[] variables) {
		expressionVariables.addAll(Arrays.asList(variables));
		return ParserContextImpl.this;
	}

	private void init() {
		expressionVariables = new ArrayList();
	}
}
