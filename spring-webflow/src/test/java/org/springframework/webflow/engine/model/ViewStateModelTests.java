/*
 * Copyright 2004-2012 the original author or authors.
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
package org.springframework.webflow.engine.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedList;

import org.junit.jupiter.api.Test;


/**
 * Unit tests for {@link ViewStateModel}.
 */
public class ViewStateModelTests {

	@Test
	public void testMergeable() {
		ViewStateModel child = new ViewStateModel("child");
		assertTrue(child.isMergeableWith(child));
	}

	@Test
	public void testNotMergeable() {
		ViewStateModel child = new ViewStateModel("child");
		ViewStateModel parent = new ViewStateModel("parent");
		assertFalse(child.isMergeableWith(parent));
	}

	@Test
	public void testNotMergeableWithNull() {
		ViewStateModel child = new ViewStateModel("child");
		assertFalse(child.isMergeableWith(null));
	}

	@Test
	public void testMerge() {
		ViewStateModel child = new ViewStateModel("child");
		ViewStateModel parent = new ViewStateModel("parent");

		LinkedList<AttributeModel> attributes = new LinkedList<>();
		attributes.add(new AttributeModel("foo", "bar"));
		parent.setAttributes(attributes);

		BinderModel binder = new BinderModel();
		LinkedList<BindingModel> bindings = new LinkedList<>();
		bindings.add(new BindingModel("foo", "fooConverter", "true"));
		binder.setBindings(bindings);
		parent.setBinder(binder);

		parent.setSecured(new SecuredModel("secured"));

		parent.setRedirect("true");
		parent.setPopup("true");
		parent.setModel("fooModel");
		parent.setValidationHints("foo");
		parent.setView("fooView");

		LinkedList<TransitionModel> transitions = new LinkedList<>();
		TransitionModel tx = new TransitionModel();
		tx.setOn("submit");
		tx.setTo("bar");
		transitions.add(tx);
		parent.setTransitions(transitions);

		EvaluateModel eval = new EvaluateModel("foo.bar");
		LinkedList<AbstractActionModel> actions = new LinkedList<>();
		actions.add(eval);
		parent.setOnEntryActions(actions);
		parent.setOnExitActions(actions);
		parent.setOnRenderActions(actions);

		LinkedList<VarModel> vars = new LinkedList<>();
		vars.add(new VarModel("foo", "class"));
		parent.setVars(vars);

		LinkedList<ExceptionHandlerModel> eh = new LinkedList<>();
		eh.add(new ExceptionHandlerModel("foo"));
		parent.setExceptionHandlers(eh);

		child.merge(parent);
		assertNotNull(child.getSecured());

		assertEquals("true", child.getRedirect());
		assertEquals("true", child.getPopup());
		assertEquals("fooModel", child.getModel());
		assertEquals("foo", child.getValidationHints());
		assertEquals("fooView", child.getView());
		assertEquals("bar", child.getAttributes().get(0).getValue());
		assertEquals("foo", child.getBinder().getBindings().get(0).getProperty());
		assertEquals("bar", child.getTransitions().get(0).getTo());
		assertEquals("foo.bar", ((EvaluateModel) child.getOnEntryActions().get(0)).getExpression());
		assertEquals("foo.bar", ((EvaluateModel) child.getOnExitActions().get(0)).getExpression());
		assertEquals("foo.bar", ((EvaluateModel) child.getOnRenderActions().get(0)).getExpression());
		assertEquals("foo", child.getVars().get(0).getName());
		assertEquals("foo", child.getExceptionHandlers().get(0).getBean());
	}
}
