package org.springframework.webflow.mvc.view;

import org.springframework.binding.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.webflow.expression.spel.WebFlowSpringELExpressionParser;

public class DefaultBindingModelTests extends AbstractBindingModelTests {

	protected ExpressionParser getExpressionParser() {
		return new WebFlowSpringELExpressionParser(new SpelExpressionParser());
	}

}
