/*
 * Copyright 2004-2012 the original author or authors.
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
package org.springframework.faces.webflow;

import junit.framework.TestCase;

import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.test.MockRequestContext;

public class JsfManagedBeanPropertyAccessorTests extends TestCase {

	JSFMockHelper jsfMock = new JSFMockHelper();

	JsfManagedBeanPropertyAccessor accessor = new JsfManagedBeanPropertyAccessor();

	private MockRequestContext requestContext;

	protected void setUp() throws Exception {
		this.jsfMock.setUp();
		this.requestContext = new MockRequestContext();
		RequestContextHolder.setRequestContext(this.requestContext);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		this.jsfMock.tearDown();
		RequestContextHolder.setRequestContext(null);
	}

	public void testCanRead() throws Exception {
		this.jsfMock.externalContext().getRequestMap().put("myJsfBean", new Object());
		assertTrue(this.accessor.canRead(null, null, "myJsfBean"));
	}

	public void testRead() throws Exception {
		Object jsfBean = new Object();
		this.jsfMock.externalContext().getRequestMap().put("myJsfBean", jsfBean);
		assertEquals(jsfBean, this.accessor.read(null, null, "myJsfBean").getValue());
	}

	public void testCanWrite() throws Exception {
		assertFalse(this.accessor.canWrite(null, null, "myJsfBean"));

		MutableAttributeMap<Object> map = this.requestContext.getExternalContext().getRequestMap();
		map.put("myJsfBean", new Object());
		assertTrue(this.accessor.canWrite(null, null, "myJsfBean"));
		map.clear();

		map = this.requestContext.getExternalContext().getSessionMap();
		map.put("myJsfBean", new Object());
		assertTrue(this.accessor.canWrite(null, null, "myJsfBean"));
		map.clear();

		map = this.requestContext.getExternalContext().getApplicationMap();
		map.put("myJsfBean", new Object());
		assertTrue(this.accessor.canWrite(null, null, "myJsfBean"));
		map.clear();
	}

	public void testWrite() throws Exception {
		Object jsfBean1 = new Object();
		Object jsfBean2 = new Object();

		MutableAttributeMap<Object> map = this.requestContext.getExternalContext().getRequestMap();
		this.accessor.write(null, null, "myJsfBean", jsfBean1);
		assertNull("Write occurs only if bean is present in the map", map.get("myJsfBean"));

		map.put("myJsfBean", jsfBean1);
		this.accessor.write(null, null, "myJsfBean", jsfBean2);
		assertEquals(jsfBean2, map.get("myJsfBean"));
		map.clear();
	}

}
