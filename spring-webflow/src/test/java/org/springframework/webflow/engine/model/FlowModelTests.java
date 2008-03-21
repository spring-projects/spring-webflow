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
package org.springframework.webflow.engine.model;

import junit.framework.TestCase;

/**
 * Unit tests for {@link FlowModel}.
 */
public class FlowModelTests extends TestCase {

	public void testMerge() {
		FlowModel child = new FlowModel();
		child.setStartStateId("child");
		FlowModel parent = new FlowModel();
		parent.setStartStateId("parent");
		child.merge(parent);
		assertEquals("child", child.getStartStateId());
	}

	public void testMergeNullParent() {
		FlowModel child = new FlowModel();
		child.setStartStateId("child");
		FlowModel parent = null;
		child.merge(parent);
		assertEquals("child", child.getStartStateId());
	}

	public void testMergeOverrideMatch() {
		FlowModel child = new FlowModel();
		FlowModel parent = new FlowModel();
		parent.addViewState(new ViewStateModel("view"));
		child.merge(parent);
		assertEquals(1, child.getStates().size());
	}

	public void testMergeOverrideMatchFailed() {
		FlowModel child = new FlowModel();
		FlowModel parent = new FlowModel();
		parent.addViewState(new ViewStateModel("view"));
		child.merge(parent);
		// flows will always merge, regardless of likeness
		assertEquals(1, child.getStates().size());
	}

	public void testIntegrationAttributes() {
		FlowModel child = new FlowModel();
		FlowModel parent = new FlowModel();
		child.addAttribute(new AttributeModel("name", "value"));
		parent.addAttribute(new AttributeModel("name", "value", "type"));
		parent.addAttribute(new AttributeModel("name2", "value2", "type2"));
		child.merge(parent);
		assertEquals(2, child.getAttributes().size());
		assertEquals("name", ((AttributeModel) child.getAttributes().get(0)).getName());
		assertEquals("type", ((AttributeModel) child.getAttributes().get(0)).getType());
		assertEquals("name2", ((AttributeModel) child.getAttributes().get(1)).getName());
		assertEquals("type2", ((AttributeModel) child.getAttributes().get(1)).getType());
	}

	public void testIntegrationSecured() {
		FlowModel child = new FlowModel();
		FlowModel parent = new FlowModel();
		child.setSecured(new SecuredModel("secured"));
		parent.setSecured(new SecuredModel("secured", "all"));
		child.merge(parent);
		assertEquals("all", child.getSecured().getMatch());
	}

	public void testIntegrationPersistenceContext() {
		FlowModel child = new FlowModel();
		FlowModel parent = new FlowModel();
		parent.setPersistenceContext(new PersistenceContextModel());
		child.merge(parent);
		assertNotNull(child.getPersistenceContext());
	}

	public void testIntegrationVars() {
		FlowModel child = new FlowModel();
		FlowModel parent = new FlowModel();
		child.addVar(new VarModel("name", "value"));
		parent.addVar(new VarModel("name", "", "scope"));
		parent.addVar(new VarModel("name2", "value2"));
		child.merge(parent);
		assertEquals(2, child.getVars().size());
		assertEquals("scope", ((VarModel) child.getVars().get(1)).getScope());
	}

	public void testIntegrationMappings() {
		FlowModel child = new FlowModel();
		FlowModel parent = new FlowModel();
		child.addInput(new InputModel("name", "value"));
		child.addInput(new InputModel("name2", "value2", "type2", "required2"));
		child.addInput(new InputModel("name3", "value3", "type3", "required3"));
		parent.addInput(new InputModel("name", "value", "type", "required"));
		parent.addInput(new InputModel("name3", "value3", "type3", "required3"));
		child.merge(parent);
		assertEquals(3, child.getInputs().size());
	}

	public void testIntegrationOnStart() {
		FlowModel child = new FlowModel();
		FlowModel parent = new FlowModel();
		child.addOnStartAction(new EvaluateModel("expression"));
		child.addOnStartAction(new RenderModel("expression"));
		child.addOnStartAction(new SetModel("expression", "value"));
		parent.addOnStartAction(new EvaluateModel("expression", "result"));
		parent.addOnStartAction(new RenderModel("expression"));
		parent.addOnStartAction(new SetModel("expression", "value"));
		child.merge(parent);
		assertEquals(3, child.getOnStartActions().size());
		assertEquals("result", ((EvaluateModel) child.getOnStartActions().get(0)).getResult());
	}

	public void testIntegrationStates() {
		FlowModel child = new FlowModel();
		FlowModel parent = new FlowModel();
		child.addViewState(new ViewStateModel("view"));
		child.addEndState(new EndStateModel("end"));
		parent.addViewState(new ViewStateModel("view", "jsp"));
		parent.addState(new DecisionStateModel("decider"));
		parent.addActionState(new ActionStateModel("end"));
		child.merge(parent);
		assertEquals(4, child.getStates().size());
		assertEquals("jsp", ((ViewStateModel) child.getStates().get(0)).getView());
	}

	public void testIntegrationGlobalTransitions() {
		FlowModel child = new FlowModel();
		FlowModel parent = new FlowModel();
		child.addGlobalTransition(new TransitionModel("end"));
		child.addGlobalTransition(new TransitionModel("start"));
		parent.addGlobalTransition(new TransitionModel("search"));
		parent.addGlobalTransition(new TransitionModel("end", "theend"));
		child.merge(parent);
		assertEquals(3, child.getGlobalTransitions().size());
		assertEquals("theend", ((TransitionModel) child.getGlobalTransitions().get(0)).getTo());
	}

	public void testIntegrationOnEnd() {
		FlowModel child = new FlowModel();
		FlowModel parent = new FlowModel();
		child.addOnEndAction(new EvaluateModel("expression"));
		child.addOnEndAction(new RenderModel("expression"));
		child.addOnEndAction(new SetModel("expression", "value"));
		parent.addOnEndAction(new EvaluateModel("expression", "result"));
		parent.addOnEndAction(new RenderModel("expression"));
		parent.addOnEndAction(new SetModel("expression", "value"));
		child.merge(parent);
		assertEquals(3, child.getOnEndActions().size());
		assertEquals("result", ((EvaluateModel) child.getOnEndActions().get(0)).getResult());
	}

	public void testIntegrationExceptionHandlers() {
		FlowModel child = new FlowModel();
		FlowModel parent = new FlowModel();
		child.addExceptionHandler(new ExceptionHandlerModel("bean1"));
		child.addExceptionHandler(new ExceptionHandlerModel("bean2"));
		parent.addExceptionHandler(new ExceptionHandlerModel("bean2"));
		parent.addExceptionHandler(new ExceptionHandlerModel("bean3"));
		child.merge(parent);
		assertEquals(3, child.getExceptionHandlers().size());
	}

	public void testIntegrationBeanImports() {
		FlowModel child = new FlowModel();
		FlowModel parent = new FlowModel();
		child.addBeanImport(new BeanImportModel("path1"));
		child.addBeanImport(new BeanImportModel("path2"));
		parent.addBeanImport(new BeanImportModel("path2"));
		parent.addBeanImport(new BeanImportModel("path3"));
		child.merge(parent);
		assertEquals(3, child.getBeanImports().size());
	}

}
