package org.springframework.webflow.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class FlowBuilderServicesBeanDefinitionParserTests extends AbstractFlowBuilderServicesConfigurationTests {

	@Override
	protected ApplicationContext initApplicationContext() {
		return new ClassPathXmlApplicationContext("org/springframework/webflow/config/flow-builder-services.xml");
	}

}
