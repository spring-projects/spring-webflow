/*
 * Copyright 2004-2007 the original author or authors.
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
package org.springframework.webflow.engine.builder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.engine.builder.xml.XmlFlowBuilder;
import org.springframework.webflow.util.ResourceHolder;

/**
 * Unit tests for {@link RefreshableFlowDefinitionHolder}.
 */
public class RefreshableFlowDefinitionHolderTests extends TestCase {

	public void testNoRefreshOnNoChange() {
		File parent = new File("src/test/java/org/springframework/webflow/engine/builder/xml");
		Resource location = new FileSystemResource(new File(parent, "flow.xml"));
		XmlFlowBuilder flowBuilder = new XmlFlowBuilder(location);
		FlowAssembler assembler = new FlowAssembler("flow", flowBuilder);
		RefreshableFlowDefinitionHolder holder = new RefreshableFlowDefinitionHolder(assembler);
		assertEquals("flow", holder.getFlowDefinitionId());
		assertSame(flowBuilder, holder.getFlowBuilder());
		assertEquals(0, holder.getLastModified());
		assertTrue(!holder.isAssembled());
		FlowDefinition flow1 = holder.getFlowDefinition();
		assertTrue(holder.isAssembled());
		long lastModified = holder.getLastModified();
		assertTrue(lastModified != -1);
		assertTrue(lastModified > 0);
		FlowDefinition flow2 = holder.getFlowDefinition();
		assertEquals("flow", flow2.getId());
		assertEquals(lastModified, holder.getLastModified());
		assertSame(flow1, flow2);
	}
	
	public void testReloadOnChange() throws Exception {
		MockFlowBuilder mockFlowBuilder = new MockFlowBuilder();
		FlowAssembler assembler = new FlowAssembler("mockFlow", mockFlowBuilder);
		RefreshableFlowDefinitionHolder holder = new RefreshableFlowDefinitionHolder(assembler);

		mockFlowBuilder.lastModified = 0L;
		assertEquals(0, mockFlowBuilder.buildCallCount);
		holder.getFlowDefinition();
		assertEquals(1, mockFlowBuilder.buildCallCount);
		holder.getFlowDefinition();
		assertEquals(1, mockFlowBuilder.buildCallCount);
		holder.getFlowDefinition();
		assertEquals(1, mockFlowBuilder.buildCallCount);
		mockFlowBuilder.lastModified = 10L;
		holder.getFlowDefinition();
		assertEquals(2, mockFlowBuilder.buildCallCount);
		holder.getFlowDefinition();
		assertEquals(2, mockFlowBuilder.buildCallCount);
		holder.refresh();
		assertEquals(3, mockFlowBuilder.buildCallCount);
		holder.refresh();
		assertEquals(4, mockFlowBuilder.buildCallCount);
	}
	
	private class MockFlowBuilder extends AbstractFlowBuilder implements ResourceHolder {
		
		public int buildCallCount = 0;
		public long lastModified = 0L;
		
		public void buildStates() throws FlowBuilderException {
			addEndState("end");
			buildCallCount++;
		}
		
		public Resource getResource() {
			return new AbstractResource() {
				
				public File getFile() throws IOException {
					return new File("mock") {
						public long lastModified() {
							return lastModified;
						}
					};
				}
				
				public String getDescription() {
					return null;
				}
				
				public InputStream getInputStream() throws IOException {
					return null;
				}
			};
		}
	}
	
	
}