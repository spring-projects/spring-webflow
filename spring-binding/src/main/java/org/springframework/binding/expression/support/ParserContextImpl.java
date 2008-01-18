package org.springframework.binding.expression.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.binding.expression.ExpressionVariable;
import org.springframework.binding.expression.ParserContext;

public class ParserContextImpl implements ParserContext {

	private Class evaluationContextType;

	private Class evaluationResultType;

	private List expressionVariables;

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

	public ParserContextImpl context(Class contextType) {
		evaluationContextType = contextType;
		return ParserContextImpl.this;
	}

	public ParserContextImpl expect(Class resultType) {
		evaluationResultType = resultType;
		return ParserContextImpl.this;
	}

	public ParserContextImpl variable(ExpressionVariable variable) {
		expressionVariables.add(variable);
		return ParserContextImpl.this;
	}

	public ParserContextImpl variables(ExpressionVariable[] variables) {
		expressionVariables.addAll(Arrays.asList(variables));
		return ParserContextImpl.this;
	}

	private void init() {
		expressionVariables = new ArrayList();
	}
}
