package org.springframework.faces.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ResourcesBeanDefinitionParserTests extends AbstractResourcesConfigurationTests {


	@Override
	protected ApplicationContext initApplicationContext() {
		return new ClassPathXmlApplicationContext("org/springframework/faces/config/resources.xml");
	}

}
