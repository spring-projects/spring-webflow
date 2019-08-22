package org.springframework.faces.config;

import org.junit.After;
import org.junit.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.faces.webflow.JSFMockHelper;

public class ResourcesBeanDefinitionParserTests extends AbstractResourcesConfigurationTests {

	/**
	 * JSF Mock Helper
	 */
	private final JSFMockHelper jsfMockHelper = new JSFMockHelper();

	@Override
	@Before
	public void setUp() throws Exception {
		this.jsfMockHelper.setUp();
		super.setUp();
	}

	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();
		this.jsfMockHelper.tearDown();
	}

	@Override
	protected ApplicationContext initApplicationContext() {
		return new ClassPathXmlApplicationContext("org/springframework/faces/config/resources.xml");
	}

}
