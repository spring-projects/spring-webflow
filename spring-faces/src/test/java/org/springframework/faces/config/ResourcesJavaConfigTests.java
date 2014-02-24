package org.springframework.faces.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

public class ResourcesJavaConfigTests extends AbstractResourcesConfigurationTests {


	@Override
	protected ApplicationContext initApplicationContext() {
		return new AnnotationConfigApplicationContext(FacesFlowConfig.class);
	}

	@Configuration
	static class FacesFlowConfig extends AbstractFacesPortletFlowConfiguration {

	}

}
