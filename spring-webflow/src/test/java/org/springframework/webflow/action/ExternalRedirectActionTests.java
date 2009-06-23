package org.springframework.webflow.action;

import junit.framework.TestCase;

import org.springframework.binding.expression.support.StaticExpression;
import org.springframework.webflow.test.MockRequestContext;

public class ExternalRedirectActionTests extends TestCase {
	private ExternalRedirectAction action;

	public void testExecute() throws Exception {
		action = new ExternalRedirectAction(new StaticExpression("/wherever"));
		MockRequestContext context = new MockRequestContext();
		action.execute(context);
		assertEquals("/wherever", context.getMockExternalContext().getExternalRedirectUrl());
	}

	public void testExecuteWithNullResourceUri() throws Exception {
		try {
			action = new ExternalRedirectAction(null);
			fail("Should have failed");
		} catch (IllegalArgumentException e) {

		}
	}
}
