/*
 * Copyright 2004-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.webflow.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.MalformedURLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.ServletContextResourceLoader;

/**
 * Unit tests for {@link FlowDefinitionResourceFactory}.
 */
public class FlowDefinitionResourceFactoryTests {

	private ResourceLoader resourceLoader;

	private FlowDefinitionResourceFactory factory;

	@BeforeEach
	public void setUp() throws Exception {
		resourceLoader = new ServletContextResourceLoader(new MockServletContext());
		factory = new FlowDefinitionResourceFactory();
	}

	@Test
	public void testGetFlowIdNoBasePath() {
		Resource resource = resourceLoader.getResource("/WEB-INF/hotels/booking/booking-flow.xml");
		assertEquals("booking-flow", factory.getFlowId(resource));
	}

	@Test
	public void testGetFlowIdFileSystemResourceBasePathMatch() {
		Resource resource = new FileSystemResource("/the/path/on/the/file/system/sample-flow.xml");
		factory.setBasePath("file:/the/path");
		assertEquals("on/the/file/system", factory.getFlowId(resource));
	}

	@Test
	public void testGetFlowIdCustomBasePath() {
		Resource resource = resourceLoader.getResource("/WEB-INF/hotels/booking/booking-flow.xml");
		factory.setBasePath("WEB-INF");
		assertEquals("hotels/booking", factory.getFlowId(resource));
	}

	@Test
	public void testGetFlowCustomBasePathTrailingSlash() {
		Resource resource = resourceLoader.getResource("/WEB-INF/hotels/booking/booking-flow.xml");
		factory.setBasePath("WEB-INF/");
		assertEquals("hotels/booking", factory.getFlowId(resource));
	}

	@Test
	public void testGetFlowIdCustomBasePathLeadingSlash() {
		Resource resource = resourceLoader.getResource("/WEB-INF/hotels/booking/booking-flow.xml");
		factory.setBasePath("/WEB-INF");
		assertEquals("hotels/booking", factory.getFlowId(resource));
	}

	@Test
	public void testGetFlowIdCustomBasePathLeadingAndTrailingSlash() {
		Resource resource = resourceLoader.getResource("/WEB-INF/hotels/booking/booking-flow.xml");
		factory.setBasePath("/WEB-INF/");
		assertEquals("hotels/booking", factory.getFlowId(resource));
	}

	@Test
	public void testGetFlowIdFlowPathIsBasePath() {
		Resource resource = resourceLoader.getResource("/WEB-INF/hotels/booking/booking-flow.xml");
		factory.setBasePath("/WEB-INF/hotels/booking");
		assertEquals("booking-flow", factory.getFlowId(resource));
	}

	@Test
	public void testGetFlowIdBasePathMismatch() {
		Resource resource = resourceLoader.getResource("/WEB-INF/hotels/booking/booking-flow.xml");
		factory.setBasePath("/foo/bar");
		assertEquals("WEB-INF/hotels/booking", factory.getFlowId(resource));
	}

	@Test
	public void testGetFlowIdClassPathResource() {
		Resource resource = new ClassPathResource("org/springframework/webflow/sample/sample-flow.xml");
		factory.setBasePath("classpath:org/springframework/webflow/");
		assertEquals("sample", factory.getFlowId(resource));
	}

	@Test
	public void testGetFlowIdClassPathStarResource() {
		Resource resource = new ClassPathResource("org/springframework/webflow/sample/sample-flow.xml");
		factory.setBasePath("classpath*:org/springframework/webflow/");
		assertEquals("sample", factory.getFlowId(resource));
	}

	@Test
	public void testGetFlowIdFileSystemResource() {
		Resource resource = new FileSystemResource(
				"/the/path/on/the/file/system/org/springframework/webflow/sample/sample-flow.xml");
		factory.setBasePath("classpath:org/springframework/webflow/");
		assertEquals("sample", factory.getFlowId(resource));
	}

	@Test
	public void testGetFlowIdFileSystemResourceNoBasePathMatch() {
		Resource resource = new FileSystemResource("/the/path/on/the/file/system/sample-flow.xml");
		factory.setBasePath("classpath:org/springframework/webflow/");
		assertEquals("the/path/on/the/file/system", factory.getFlowId(resource));
	}

	@Test
	public void testGetFlowIdUrlResource() throws MalformedURLException {
		Resource resource = new UrlResource(
				"file:/the/path/on/the/file/system/org/springframework/webflow/sample/sample-flow.xml");
		factory.setBasePath("classpath:org/springframework/webflow/");
		assertEquals("sample", factory.getFlowId(resource));
	}

	@Test
	public void testGetFlowIdUrlResourceNoBasePathMatch() throws MalformedURLException {
		Resource resource = new UrlResource("file:/the/path/on/the/file/system/sample-flow.xml");
		factory.setBasePath("classpath:org/springframework/webflow/");
		assertEquals("the/path/on/the/file/system", factory.getFlowId(resource));
	}

}
