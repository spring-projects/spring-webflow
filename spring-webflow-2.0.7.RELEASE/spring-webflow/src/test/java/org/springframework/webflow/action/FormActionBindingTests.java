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
package org.springframework.webflow.action;

import junit.framework.TestCase;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Errors;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ScopeType;
import org.springframework.webflow.test.MockRequestContext;

/**
 * Unit test for the {@link FormAction} class, dealing with binding related issues.
 * 
 * @author Erwin Vervaet
 */
public class FormActionBindingTests extends TestCase {

	public static class TestBean {

		private Long prop;
		public String otherProp;

		public Long getProp() {
			return prop;
		}

		public void setProp(Long prop) {
			this.prop = prop;
		}
	}

	public void testMessageCodesOnBindFailure() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setPathInfo("/fooFlow");
		request.setMethod("POST");
		request.addParameter("prop", "A");
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockRequestContext context = new MockRequestContext();
		context.setExternalContext(new ServletExternalContext(new MockServletContext(), request, response));
		context.setAttribute("method", "bindAndValidate");

		// use a FormAction to do the binding
		FormAction formAction = new FormAction();
		formAction.setFormObjectClass(TestBean.class);
		formAction.setFormObjectName("formObject");
		formAction.execute(context);
		Errors formActionErrors = new FormObjectAccessor(context).getCurrentFormErrors(formAction.getFormErrorsScope());
		assertNotNull(formActionErrors);
		assertTrue(formActionErrors.hasErrors());

		assertEquals(1, formActionErrors.getErrorCount());
		assertEquals(0, formActionErrors.getGlobalErrorCount());
		assertEquals(1, formActionErrors.getFieldErrorCount("prop"));
	}

	public void testFieldBinding() throws Exception {
		FormAction formAction = new FormAction() {
			protected Object createFormObject(RequestContext context) throws Exception {
				TestBean res = new TestBean();
				res.setProp(new Long(-1));
				res.otherProp = "initialValue";
				return res;
			}

			protected void initBinder(RequestContext context, DataBinder binder) {
				binder.initDirectFieldAccess();
			}
		};
		formAction.setFormObjectName("formObject");

		MockRequestContext context = new MockRequestContext();

		context.setAttribute("method", "setupForm");
		formAction.execute(context);
		Errors errors = new FormObjectAccessor(context).getFormErrors("formObject", ScopeType.FLASH);
		assertNotNull(errors);
		assertEquals(new Long(-1), errors.getFieldValue("prop"));

		// this fails because of SWF-193
		assertEquals("initialValue", errors.getFieldValue("otherProp"));

		context.putRequestParameter("prop", "1");
		context.putRequestParameter("otherProp", "value");
		context.setAttribute("method", "bind");
		formAction.execute(context);

		TestBean formObject = (TestBean) new FormObjectAccessor(context).getFormObject("formObject", ScopeType.FLOW);
		errors = new FormObjectAccessor(context).getFormErrors("formObject", ScopeType.FLASH);
		assertNotNull(formObject);
		assertEquals(new Long(1), formObject.getProp());
		assertEquals(new Long(1), errors.getFieldValue("prop"));
		assertEquals("value", formObject.otherProp);
		assertEquals("value", errors.getFieldValue("otherProp"));
	}
}
