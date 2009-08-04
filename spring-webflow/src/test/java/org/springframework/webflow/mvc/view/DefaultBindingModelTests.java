package org.springframework.webflow.mvc.view;

import org.springframework.binding.expression.ExpressionParser;
import org.springframework.webflow.expression.DefaultExpressionParserFactory;

public class DefaultBindingModelTests extends AbstractBindingModelTests {

	protected ExpressionParser getExpressionParser() {
		return DefaultExpressionParserFactory.getExpressionParser();
	}
}
