package org.springframework.webflow.engine.support;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.webflow.TestBean;
import org.springframework.webflow.test.MockRequestContext;

public class BeanFactoryVariableValueFactoryTests {
	private BeanFactoryVariableValueFactory factory;

	@Test
	public void testCreateValue() {
		factory = new BeanFactoryVariableValueFactory(TestBean.class, new DefaultListableBeanFactory());
		MockRequestContext context = new MockRequestContext();
		Object value = factory.createInitialValue(context);
		assertTrue(value instanceof TestBean);
	}

	@Test
	public void testRestoreValue() {
		factory = new BeanFactoryVariableValueFactory(TestBean.class, new DefaultListableBeanFactory());
		MockRequestContext context = new MockRequestContext();
		TestBean bean = new TestBean();
		factory.restoreReferences(bean, context);
	}
}
