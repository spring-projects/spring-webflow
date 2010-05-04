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

	// BeanWrapper-based EL does not accept result type hints.
	// Hence it requires a conversion service.
	public void testGetFieldValueNonStringNoConversionService() {
		model = new BindingModel("testBean", testBean, getExpressionParser(), null, messages);
		testBean.datum2 = 3;
		assertEquals(new Integer(3), model.getFieldValue("datum2"));
	}

}
