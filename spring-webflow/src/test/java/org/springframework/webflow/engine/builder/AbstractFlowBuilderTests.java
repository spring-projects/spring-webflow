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

import junit.framework.TestCase;

import org.springframework.webflow.action.MultiAction;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.engine.ActionState;
import org.springframework.webflow.engine.AnnotatedAction;
import org.springframework.webflow.engine.EndState;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.FlowAttributeMapper;
import org.springframework.webflow.engine.SubflowState;
import org.springframework.webflow.engine.Transition;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.engine.impl.FlowExecutionImplFactory;
import org.springframework.webflow.engine.support.ApplicationViewSelector;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ViewSelection;
import org.springframework.webflow.execution.support.ApplicationView;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockRequestContext;

/**
 * Test Java based flow builder logic (subclasses of AbstractFlowBuilder).
 * 
 * @see org.springframework.webflow.engine.builder.AbstractFlowBuilder
 * 
 * @author Keith Donald
 * @author Rod Johnson
 * @author Colin Sampaleanu
 */
public class AbstractFlowBuilderTests extends TestCase {

	private String PERSONS_LIST = "person.List";

	private static String PERSON_DETAILS = "person.Detail";

	private AbstractFlowBuilder builder = createBuilder();
	
	protected AbstractFlowBuilder createBuilder() {
		return new AbstractFlowBuilder() {
			public void buildStates() {
				addEndState("finish");
			}
		};
	}
	
	public void testDependencyLookup() {
		TestMasterFlowBuilderLookupById master = new TestMasterFlowBuilderLookupById();
		master.setFlowServiceLocator(new BaseFlowServiceLocator() {
			public Flow getSubflow(String id) throws FlowArtifactLookupException {
				if (id.equals(PERSON_DETAILS)) {
					BaseFlowBuilder builder = new TestDetailFlowBuilderLookupById();
					builder.setFlowServiceLocator(this);
					FlowAssembler assembler = new FlowAssembler(PERSON_DETAILS, builder);
					return assembler.assembleFlow();
				}
				else {
					throw new FlowArtifactLookupException(id, Flow.class);
				}
			}

			public Action getAction(String id) throws FlowArtifactLookupException {
				return new NoOpAction();
			}

			public FlowAttributeMapper getAttributeMapper(String id) throws FlowArtifactLookupException {
				if (id.equals("id.attributeMapper")) {
					return new PersonIdMapper();
				}
				else {
					throw new FlowArtifactLookupException(id, FlowAttributeMapper.class);
				}
			}
		});

		FlowAssembler assembler = new FlowAssembler(PERSONS_LIST, master);
		Flow flow = assembler.assembleFlow();

		assertEquals("person.List", flow.getId());
		assertTrue(flow.getStateCount() == 4);
		assertTrue(flow.containsState("getPersonList"));
		assertTrue(flow.getState("getPersonList") instanceof ActionState);
		assertTrue(flow.containsState("viewPersonList"));
		assertTrue(flow.getState("viewPersonList") instanceof ViewState);
		assertTrue(flow.containsState("person.Detail"));
		assertTrue(flow.getState("person.Detail") instanceof SubflowState);
		assertTrue(flow.containsState("finish"));
		assertTrue(flow.getState("finish") instanceof EndState);
	}

	public void testNoArtifactFactorySet() {
		TestMasterFlowBuilderLookupById master = new TestMasterFlowBuilderLookupById();
		try {
			FlowAssembler assembler = new FlowAssembler(PERSONS_LIST, master);
			assembler.assembleFlow();
			fail("Should have failed, artifact lookup not supported");
		}
		catch (UnsupportedOperationException e) {
			// expected
		}
	}

	public class TestMasterFlowBuilderLookupById extends AbstractFlowBuilder {
		public void buildStates() {
			addActionState("getPersonList", action("noOpAction"), transition(on(success()), to("viewPersonList")));
			addViewState("viewPersonList", "person.list.view", transition(on(submit()), to("person.Detail")));
			addSubflowState(PERSON_DETAILS, flow("person.Detail"), attributeMapper("id.attributeMapper"), transition(
					on("*"), to("getPersonList")));
			addEndState("finish");
		}
	}

	public class TestMasterFlowBuilderDependencyInjection extends AbstractFlowBuilder {
		private NoOpAction noOpAction;

		private Flow subFlow;

		private PersonIdMapper personIdMapper;

		public void setNoOpAction(NoOpAction noOpAction) {
			this.noOpAction = noOpAction;
		}

		public void setPersonIdMapper(PersonIdMapper personIdMapper) {
			this.personIdMapper = personIdMapper;
		}

		public void setSubFlow(Flow subFlow) {
			this.subFlow = subFlow;
		}

		public void buildStates() {
			addActionState("getPersonList", noOpAction, transition(on(success()), to("viewPersonList")));
			addViewState("viewPersonList", "person.list.view", transition(on(submit()), to("person.Detail")));
			addSubflowState(PERSON_DETAILS, subFlow, personIdMapper, transition(on("*"), to("getPersonList")));
			addEndState("finish");
		}
	}

	public static class PersonIdMapper implements FlowAttributeMapper {
		public MutableAttributeMap createFlowInput(RequestContext context) {
			LocalAttributeMap inputMap = new LocalAttributeMap();
			inputMap.put("personId", context.getFlowScope().get("personId"));
			return inputMap;
		}

		public void mapFlowOutput(AttributeMap subflowOutput, RequestContext context) {
		}
	}

	public static class TestDetailFlowBuilderLookupById extends AbstractFlowBuilder {
		public void buildStates() {
			addActionState("getDetails", action("noOpAction"), transition(on(success()), to("viewDetails")));
			addViewState("viewDetails", "person.Detail.view", transition(on(submit()), to("bindAndValidateDetails")));
			addActionState("bindAndValidateDetails", action("noOpAction"), new Transition[] {
					transition(on(error()), to("viewDetails")), transition(on(success()), to("finish")) });
			addEndState("finish");
		}
	}

	public static class TestDetailFlowBuilderDependencyInjection extends AbstractFlowBuilder {

		private NoOpAction noOpAction;

		public void setNoOpAction(NoOpAction noOpAction) {
			this.noOpAction = noOpAction;
		}

		public void buildStates() {
			addActionState("getDetails", noOpAction, transition(on(success()), to("viewDetails")));
			addViewState("viewDetails", "person.Detail.view", transition(on(submit()), to("bindAndValidateDetails")));
			addActionState("bindAndValidateDetails", noOpAction, new Transition[] {
					transition(on(error()), to("viewDetails")), transition(on(success()), to("finish")) });
			addEndState("finish");
		}
	};

	/**
	 * Action bean stub that does nothing, just returns a "success" result.
	 */
	public static final class NoOpAction implements Action {
		public Event execute(RequestContext context) throws Exception {
			return new Event(this, "success");
		}
	}
	
	public void testConfigureMultiAction() throws Exception {
		MultiAction multiAction = new MultiAction(new MultiActionTarget());
		AnnotatedAction action = builder.invoke("foo", multiAction);
		assertEquals("foo", action.getAttributeMap().get(AnnotatedAction.METHOD_ATTRIBUTE));
		assertEquals("success", action.execute(new MockRequestContext()).getId());
	}
	
	public static class MultiActionTarget {
		public Event foo(RequestContext context) {
			return new Event(this, "success");
		}
	}
	
	public void testEndStateRefresh() {
		FlowBuilder builder = new AbstractFlowBuilder() {
			public void buildStates() throws FlowBuilderException {
				addEndState("theEnd", "redirect:endView");
			}
		};
		Flow testFlow = new FlowAssembler("testFlow", builder).assembleFlow();
		assertTrue(testFlow.getStartState() instanceof EndState);
		assertTrue(((EndState)testFlow.getStartState()).getViewSelector() instanceof ApplicationViewSelector);
		assertTrue(((ApplicationViewSelector)((EndState)testFlow.getStartState()).getViewSelector()).isRedirect());
		
		FlowExecution execution = new FlowExecutionImplFactory().createFlowExecution(testFlow);
		ViewSelection viewSelection = execution.start(null, new MockExternalContext());
		assertTrue("redirect: should be ignored for end states", viewSelection instanceof ApplicationView);
		assertEquals("endView", ((ApplicationView)viewSelection).getViewName());
		assertFalse(execution.isActive());
	}
}