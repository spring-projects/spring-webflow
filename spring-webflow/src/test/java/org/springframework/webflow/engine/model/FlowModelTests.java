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
package org.springframework.webflow.engine.model;

import junit.framework.TestCase;

/**
 * Unit tests for {@link FlowModel}.
 */
public class FlowModelTests extends TestCase {

	public void testMergeable() {
		FlowModel child = new FlowModel();
		FlowModel parent = new FlowModel();
		assertTrue(child.isMergeableWith(parent));
	}

	public void testNotMergeableWithNull() {
		FlowModel child = new FlowModel();
		assertFalse(child.isMergeableWith(null));
	}

	public void testMergeAttributes() {
		FlowModel child = new FlowModel();
		FlowModel parent = new FlowModel();
		AttributeModel attribute;
		child.addAttribute(new AttributeModel("name", "value"));
		attribute = new AttributeModel("name", "value");
		attribute.setType("type");
		parent.addAttribute(attribute);
		attribute = new AttributeModel("name2", "value2");
		attribute.setType("type2");
		parent.addAttribute(attribute);
		child.merge(parent);
		assertEquals(2, child.getAttributes().size());
		assertEquals("name", ((AttributeModel) child.getAttributes().get(0)).getName());
		assertEquals("type", ((AttributeModel) child.getAttributes().get(0)).getType());
		assertEquals("name2", ((AttributeModel) child.getAttributes().get(1)).getName());
		assertEquals("type2", ((AttributeModel) child.getAttributes().get(1)).getType());
	}

	public void testMergeSecured() {
		FlowModel child = new FlowModel();
		FlowModel parent = new FlowModel();
		child.setSecured(new SecuredModel("secured"));
		SecuredModel secured = new SecuredModel("secured");
		secured.setMatch("all");
		parent.setSecured(secured);
		child.merge(parent);
		assertEquals("all", child.getSecured().getMatch());
	}

	public void testMergePersistenceContext() {
		FlowModel child = new FlowModel();
		FlowModel parent = new FlowModel();
		parent.setPersistenceContext(new PersistenceContextModel());
		child.merge(parent);
		assertNotNull(child.getPersistenceContext());
	}

	public void testMergeVars() {
		FlowModel parent = new FlowModel();
		VarModel var = new VarModel("name", "value");
		parent.addVar(var);
		FlowModel child = new FlowModel();
		var = new VarModel("name", "value2");
		child.addVar(var);
		child.merge(parent);
		assertEquals(1, child.getVars().size());
		assertEquals("value2", ((VarModel) child.getVars().get(0)).getClassName());
	}

	public void testMergeMappings() {
		FlowModel child = new FlowModel();
		FlowModel parent = new FlowModel();
		InputModel input;
		child.addInput(new InputModel("name", "value"));
		input = new InputModel("name2", "value2");
		input.setType("type2");
		input.setRequired("required2");
		child.addInput(input);
		input = new InputModel("name3", "value3");
		input.setType("type3");
		input.setRequired("required3");
		child.addInput(input);
		input = new InputModel("name", "value");
		input.setType("type");
		input.setRequired("required");
		parent.addInput(input);
		input = new InputModel("name3", "value3");
		input.setType("type3");
		input.setRequired("required3");
		parent.addInput(input);
		child.merge(parent);
		assertEquals(3, child.getInputs().size());
	}

	public void testMergeOnStart() {
		FlowModel child = new FlowModel();
		FlowModel parent = new FlowModel();
		child.addOnStartAction(new EvaluateModel("expression"));
		child.addOnStartAction(new RenderModel("expression"));
		child.addOnStartAction(new SetModel("expression", "value"));
		EvaluateModel eval = new EvaluateModel("expression");
		eval.setResult("result");
		parent.addOnStartAction(eval);
		parent.addOnStartAction(new RenderModel("expression"));
		parent.addOnStartAction(new SetModel("expression", "value"));
		child.merge(parent);
		assertEquals(6, child.getOnStartActions().size());
		assertNotNull(((EvaluateModel) child.getOnStartActions().get(0)).getResult());
	}

	public void testMergeStates() {
		FlowModel child = new FlowModel();
		FlowModel parent = new FlowModel();
		child.addViewState(new ViewStateModel("view"));
		child.addEndState(new EndStateModel("end"));
		ViewStateModel view = new ViewStateModel("view");
		view.setView("jsp");
		parent.addViewState(view);
		parent.addState(new DecisionStateModel("decider"));
		parent.addActionState(new ActionStateModel("end"));
		child.merge(parent);
		assertEquals(4, child.getStates().size());
		assertEquals("jsp", ((ViewStateModel) child.getStates().get(0)).getView());
	}

	public void testMergeGlobalTransitions() {
		FlowModel child = new FlowModel();
		FlowModel parent = new FlowModel();
		TransitionModel transition;
		transition = new TransitionModel();
		transition.setOn("end");
		child.addGlobalTransition(transition);
		transition = new TransitionModel();
		transition.setOn("start");
		child.addGlobalTransition(transition);
		transition = new TransitionModel();
		transition.setOn("search");
		parent.addGlobalTransition(transition);
		transition = new TransitionModel();
		transition.setOn("end");
		transition.setTo("theend");
		parent.addGlobalTransition(transition);
		child.merge(parent);
		assertEquals(3, child.getGlobalTransitions().size());
		assertEquals("theend", ((TransitionModel) child.getGlobalTransitions().get(0)).getTo());
	}

	public void testMergeOnEnd() {
		FlowModel child = new FlowModel();
		FlowModel parent = new FlowModel();
		child.addOnEndAction(new EvaluateModel("expression"));
		child.addOnEndAction(new RenderModel("expression"));
		child.addOnEndAction(new SetModel("expression", "value"));
		EvaluateModel eval = new EvaluateModel("expression");
		eval.setResult("result");
		parent.addOnEndAction(eval);
		parent.addOnEndAction(new RenderModel("expression"));
		parent.addOnEndAction(new SetModel("expression", "value"));
		child.merge(parent);
		assertEquals(6, child.getOnEndActions().size());
		assertNotNull(((EvaluateModel) child.getOnEndActions().get(0)).getResult());
	}

	public void testMergeExceptionHandlers() {
		FlowModel child = new FlowModel();
		FlowModel parent = new FlowModel();
		child.addExceptionHandler(new ExceptionHandlerModel("bean1"));
		child.addExceptionHandler(new ExceptionHandlerModel("bean2"));
		parent.addExceptionHandler(new ExceptionHandlerModel("bean2"));
		parent.addExceptionHandler(new ExceptionHandlerModel("bean3"));
		child.merge(parent);
		assertEquals(4, child.getExceptionHandlers().size());
	}

	public void testMergeBeanImports() {
		FlowModel child = new FlowModel();
		FlowModel parent = new FlowModel();
		child.addBeanImport(new BeanImportModel("path1"));
		child.addBeanImport(new BeanImportModel("path2"));
		parent.addBeanImport(new BeanImportModel("path2"));
		parent.addBeanImport(new BeanImportModel("path3"));
		child.merge(parent);
		assertEquals(4, child.getBeanImports().size());
	}

}
