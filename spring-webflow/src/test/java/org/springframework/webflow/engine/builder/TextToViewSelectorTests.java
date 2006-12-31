/*
 * Copyright 2002-2007 the original author or authors.
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

import org.springframework.webflow.engine.NullViewSelector;
import org.springframework.webflow.engine.ViewSelector;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ViewSelection;
import org.springframework.webflow.execution.support.ApplicationView;
import org.springframework.webflow.execution.support.ExternalRedirect;
import org.springframework.webflow.execution.support.FlowDefinitionRedirect;
import org.springframework.webflow.execution.support.FlowExecutionRedirect;
import org.springframework.webflow.test.MockFlowServiceLocator;
import org.springframework.webflow.test.MockRequestContext;

/**
 * Test case for ${link TextToViewSelector}.
 * 
 * @author Erwin Vervaet
 */
public class TextToViewSelectorTests extends TestCase {

	private MockFlowServiceLocator serviceLocator;
	private TextToViewSelector converter;

	public void setUp() {
		serviceLocator = new MockFlowServiceLocator();
		converter = new TextToViewSelector(serviceLocator);
	}
	
	public void testNullView() {
		assertSame(NullViewSelector.INSTANCE, viewSelector(null));
		assertSame(NullViewSelector.INSTANCE, viewSelector(""));
	}

	public void testApplicationView() {
		ViewSelector selector = viewSelector("myView");
		RequestContext context = getRequestContext();
		ApplicationView view = (ApplicationView)selector.makeEntrySelection(context);
		assertEquals("myView", view.getViewName());
		assertEquals(5, view.getModel().size());
	}

	public void testFlowExecutionRedirect() {
		ViewSelector selector = viewSelector("redirect:myView");
		RequestContext context = getRequestContext();
		FlowExecutionRedirect redirect = (FlowExecutionRedirect)selector.makeEntrySelection(context);
		assertSame(redirect, FlowExecutionRedirect.INSTANCE);
		context.getRequestScope().clear();
		ApplicationView view = (ApplicationView)selector.makeRefreshSelection(context);
		assertEquals("myView", view.getViewName());
		assertEquals(3, view.getModel().size());
	}

	public void testFlowRedirect() {
		ViewSelector selector = viewSelector("flowRedirect:myFlow");
		RequestContext context = getRequestContext();
		FlowDefinitionRedirect redirect = (FlowDefinitionRedirect)selector.makeEntrySelection(context);
		assertEquals("myFlow", redirect.getFlowDefinitionId());
		assertEquals(0, redirect.getExecutionInput().size());
	}

	public void testFlowRedirectWithModel() {
		ViewSelector selector = viewSelector(
				"flowRedirect:myFlow?foo=${flowScope.foo}&bar=${requestScope.oven}");
		RequestContext context = getRequestContext();
		FlowDefinitionRedirect redirect = (FlowDefinitionRedirect)selector.makeEntrySelection(context);
		assertEquals("myFlow", redirect.getFlowDefinitionId());
		assertEquals(2, redirect.getExecutionInput().size());
		assertEquals("bar", redirect.getExecutionInput().get("foo"));
		assertEquals("mit", redirect.getExecutionInput().get("bar"));
	}

	public void testExternalRedirect() {
		ViewSelector selector = viewSelector(
				"externalRedirect:myUrl.htm?foo=${flowScope.foo}&bar=${requestScope.oven}");
		RequestContext context = getRequestContext();
		ExternalRedirect view = (ExternalRedirect)selector.makeEntrySelection(context);
		assertEquals("myUrl.htm?foo=bar&bar=mit", view.getUrl());
	}
	
	public void testBean() {
		ViewSelector myViewSelector = new ViewSelector() {
			public boolean isEntrySelectionRenderable(RequestContext context) {
				return true;
			}
			
			public ViewSelection makeEntrySelection(RequestContext context) {
				return null;
			}
			
			public ViewSelection makeRefreshSelection(RequestContext context) {
				return null;
			}
		};
		serviceLocator.registerBean("myViewSelector", myViewSelector);
		assertSame(myViewSelector, viewSelector("bean:myViewSelector"));
	}

	private RequestContext getRequestContext() {
		MockRequestContext ctx = new MockRequestContext();
		ctx.getFlowScope().put("foo", "bar");
		ctx.getFlowScope().put("bar", "car");
		ctx.getRequestScope().put("oven", "mit");
		ctx.getRequestScope().put("cat", "woman");
		ctx.getFlowScope().put("boo", new Integer(3));
		ctx.setLastEvent(new Event(this, "sample"));
		return ctx;
	}

	/**
	 * Turn given view name into a corresponding view selector.
	 * @param viewName the view name (might be encoded)
	 * @return the corresponding view selector
	 */
	protected ViewSelector viewSelector(String viewName) {
		return (ViewSelector)converter.convert(viewName);
	}
}