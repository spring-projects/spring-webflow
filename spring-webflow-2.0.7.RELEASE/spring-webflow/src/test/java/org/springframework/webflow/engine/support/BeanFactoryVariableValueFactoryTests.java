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
		Object value = factory.createInitialValue(context);
		assertTrue(value instanceof TestBean);
	}

	public void testRestoreValue() {
		factory = new BeanFactoryVariableValueFactory(TestBean.class, new DefaultListableBeanFactory());
		MockRequestContext context = new MockRequestContext();
		TestBean bean = new TestBean();
		factory.restoreReferences(bean, context);
	}
}
