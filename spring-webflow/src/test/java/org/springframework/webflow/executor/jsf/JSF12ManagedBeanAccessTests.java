package org.springframework.webflow.executor.jsf;

import org.jboss.el.ExpressionFactoryImpl;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.webflow.test.MockFlowServiceLocator;

public class JSF12ManagedBeanAccessTests extends JSF11ManagedBeanAccessTests {

    protected void registerMockServices(MockFlowServiceLocator serviceRegistry) {
	serviceRegistry.setExpressionParser(new Jsf12ELExpressionParser(new ExpressionFactoryImpl()));
	serviceRegistry.registerBean("serviceBean", service);

	ctx = new GenericWebApplicationContext();
	XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ctx);
	xmlReader.loadBeanDefinitions(new ClassPathResource(
		"org/springframework/webflow/executor/jsf/jsf-flow-beans.xml"));
	ctx.refresh();

	jsf.externalContext().getApplicationMap().put(
		GenericWebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, ctx);
    }

}
