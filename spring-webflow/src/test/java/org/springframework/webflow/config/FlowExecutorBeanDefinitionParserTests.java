package org.springframework.webflow.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class FlowExecutorBeanDefinitionParserTests extends AbstractFlowExecutorConfigurationTests {

	protected ApplicationContext initApplicationContext() {
		return new ClassPathXmlApplicationContext("org/springframework/webflow/config/flow-executor.xml");
	}

}
