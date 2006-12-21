/*
 * Copyright 2002-2006 the original author or authors.
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
package org.springframework.webflow.engine.builder.xml;

import junit.framework.TestCase;

import org.springframework.binding.mapping.AttributeMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.engine.ActionState;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.FlowAttributeMapper;
import org.springframework.webflow.engine.FlowExecutionExceptionHandler;
import org.springframework.webflow.engine.RequestControlContext;
import org.springframework.webflow.engine.SubflowState;
import org.springframework.webflow.engine.builder.BaseFlowServiceLocator;
import org.springframework.webflow.engine.builder.FlowArtifactLookupException;
import org.springframework.webflow.engine.builder.FlowAssembler;
import org.springframework.webflow.engine.support.AbstractFlowAttributeMapper;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ViewSelection;

/**
 * Test case for XML flow builder, testing pluggability of custom types.
 * 
 * @see org.springframework.webflow.engine.builder.xml.XmlFlowBuilder
 * 
 * @author Erwin Vervaet
 */
public class XmlFlowBuilderCustomTypeTests extends TestCase {

	private Flow flow;

	protected void setUp() throws Exception {
		XmlFlowBuilder builder = new XmlFlowBuilder(new ClassPathResource("testFlow3.xml",
				XmlFlowBuilderCustomTypeTests.class), new CustomFlowArtifactFactory());
		FlowAssembler assembler = new FlowAssembler("testFlow3", builder);
		flow = assembler.assembleFlow();
	}

	public void testBuildResult() {
		assertEquals("testFlow3", flow.getId());
		assertEquals(5, flow.getStateCount());
		assertEquals(1, flow.getExceptionHandlerSet().size());
		assertSame(((ActionState)flow.getState("actionState1")).getActionList().getAnnotated(0).getTargetAction()
				.getClass(), CustomAction.class);
		assertSame(((SubflowState)flow.getState("subFlowState1")).getAttributeMapper().getClass(),
				CustomAttributeMapper.class);
		assertSame(flow.getExceptionHandlerSet().toArray()[0].getClass(), CustomExceptionHandler.class);
	}

	public static class CustomAction extends AbstractAction {
		protected Event doExecute(RequestContext context) throws Exception {
			return success();
		}
	}

	public static class CustomAttributeMapper extends AbstractFlowAttributeMapper {
		protected AttributeMapper getInputMapper() {
			return null;
		}
		
		protected AttributeMapper getOutputMapper() {
			return null;
		}
	}

	public static class CustomExceptionHandler implements FlowExecutionExceptionHandler {
		public boolean handles(FlowExecutionException exception) {
			return false;
		}

		public ViewSelection handle(FlowExecutionException exception, RequestControlContext context) {
			return null;
		}
	}

	public static class CustomFlowArtifactFactory extends BaseFlowServiceLocator {

		public Action getAction(String id) throws FlowArtifactLookupException {
			return new CustomAction();
		}

		public FlowAttributeMapper getAttributeMapper(String id) throws FlowArtifactLookupException {
			return new CustomAttributeMapper();
		}

		public FlowExecutionExceptionHandler getExceptionHandler(String id) throws FlowArtifactLookupException {
			return new CustomExceptionHandler();
		}

	}

}