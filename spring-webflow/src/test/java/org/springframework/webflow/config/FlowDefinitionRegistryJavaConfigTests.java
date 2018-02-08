package org.springframework.webflow.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;

public class FlowDefinitionRegistryJavaConfigTests extends AbstractFlowRegistryConfigurationTests {

	protected ApplicationContext initApplicationContext() {
		return new AnnotationConfigApplicationContext(WebFlowConfig.class);
	}


	static class WebFlowConfig extends AbstractFlowConfiguration {

		@Bean
		public FlowDefinitionRegistry flowRegistry() {

			Map<String, Object> flowAttributes = new HashMap<>();
			flowAttributes.put("foo", "bar");
			flowAttributes.put("bar", 2);

			return getFlowDefinitionRegistryBuilder().setParent(parentRegistry())
					.addFlowLocation("org/springframework/webflow/config/flow.xml", "flow", flowAttributes)
					.addFlowLocation("/some/path/that/is/bogus.xml")
					.addFlowLocationPattern("org/springframework/webflow/config/flows/*.xml")
					.addFlowBuilder(new FooFlowBuilder())
					.addFlowBuilder(new FooFlowBuilder(), "foo2")
					.addFlowBuilder(new FooFlowBuilder(), "foo3", flowAttributes)
					.build();
		}

		@Bean
		public FlowDefinitionRegistry parentRegistry() {
			return getFlowDefinitionRegistryBuilder()
					.addFlowLocation("org/springframework/webflow/config/flow.xml", "parentFlow")
					.build();
		}

	}

}
