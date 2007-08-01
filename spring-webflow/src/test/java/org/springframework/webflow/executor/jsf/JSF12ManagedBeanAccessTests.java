package org.springframework.webflow.executor.jsf;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.webflow.test.MockFlowServiceLocator;

public class JSF12ManagedBeanAccessTests extends JSF11ManagedBeanAccessTests {

    protected void registerMockServices(MockFlowServiceLocator serviceRegistry) {
	serviceRegistry.setExpressionParser(new Jsf12ELExpressionParser());
	serviceRegistry.registerBean("serviceBean", service);
	StaticWebApplicationContext ctx = new StaticWebApplicationContext();
	ctx.registerPrototype("jsfModel", JSFModel.class);
	jsf.externalContext().getApplicationMap()
		.put(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, ctx);
    }

}
