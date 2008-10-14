/*
 * Copyright 2004-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.webflow.config;

import junit.framework.TestCase;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.ServletContextResourceLoader;

/**
 * Unit tests for {@link FlowDefinitionResourceFactory}.
 */
public class FlowDefinitionResourceFactoryTests extends TestCase {

	private ResourceLoader resourceLoader;
	private FlowDefinitionResourceFactory factory;

	protected void setUp() throws Exception {
		resourceLoader = new ServletContextResourceLoader(new MockServletContext());
		factory = new FlowDefinitionResourceFactory();
	}

	public void testGetFlowId() {
		Resource resource = resourceLoader.getResource("/WEB-INF/hotels/booking/booking-flow.xml");
		assertEquals("booking-flow", factory.getFlowId(resource));
	}

	public void testGetFlowIdCustomBasePath() {
		Resource resource = resourceLoader.getResource("/WEB-INF/hotels/booking/booking-flow.xml");
		factory.setBasePath("/WEB-INF");
		assertEquals("hotels/booking", factory.getFlowId(resource));
	}

	public void testGetFlowIdCustomBasePathTrailingSlash() {
		Resource resource = resourceLoader.getResource("/WEB-INF/hotels/booking/booking-flow.xml");
		factory.setBasePath("/WEB-INF/");
		assertEquals("hotels/booking", factory.getFlowId(resource));
	}

	public void testGetFlowIdFlowPathIsBasePath() {
		Resource resource = resourceLoader.getResource("/WEB-INF/hotels/booking/booking-flow.xml");
		factory.setBasePath("/WEB-INF/hotels/booking");
		assertEquals("booking-flow", factory.getFlowId(resource));
	}

	public void testGetFlowIdClassPathResource() {
		Resource resource = new ClassPathResource("org/springframework/webflow/sample/sample-flow.xml");
		factory.setBasePath("classpath:org/springframework/webflow");
		assertEquals("sample", factory.getFlowId(resource));
	}

}
