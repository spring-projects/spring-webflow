package org.springframework.faces.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class FacesFlowBuilderServicesBeanDefinitionParserTests
		extends AbstractFacesFlowBuilderServicesConfigurationTests {


	@Override
	protected ApplicationContext initApplicationContext() {
		return new ClassPathXmlApplicationContext("org/springframework/faces/config/flow-builder-services.xml");
	}

}
