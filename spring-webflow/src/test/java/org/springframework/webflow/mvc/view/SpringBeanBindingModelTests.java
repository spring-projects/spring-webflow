package org.springframework.webflow.mvc.view;

import java.beans.PropertyEditor;

import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.beanwrapper.BeanWrapperExpressionParser;

public class SpringBeanBindingModelTests extends AbstractBindingModelTests {

	protected ExpressionParser getExpressionParser() {
		return new BeanWrapperExpressionParser();
	}

	// See SWF-1132
	public void testFindPropertyEditorForUndeterminableType() {
		PropertyEditor editor = model.findEditor("emptyMap['foo']", null);
		assertNull(editor);
	}
}
