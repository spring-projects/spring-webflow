package org.springframework.webflow.engine.support;

import junit.framework.TestCase;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.webflow.TestBean;
import org.springframework.webflow.test.MockRequestContext;

public class BeanFactoryVariableValueFactoryTests extends TestCase {
	private BeanFactoryVariableValueFactory factory;

	public void testCreateValue() {
		factory = new BeanFactoryVariableValueFactory(TestBean.class, new DefaultListableBeanFactory());
		MockRequestContext context = new MockRequestContext();
		Object value = factory.createVariableValue(context);
		assertTrue(value instanceof TestBean);
	}
}
