package org.springframework.webflow.action;

import org.springframework.binding.expression.support.StaticExpression;
import org.springframework.webflow.test.MockRequestContext;

import junit.framework.TestCase;

public class ExternalRedirectActionTests extends TestCase {
	private ExternalRedirectAction action;

	public void testExecute() throws Exception {
		action = new ExternalRedirectAction(new StaticExpression("/wherever"));
		MockRequestContext context = new MockRequestContext();
		action.execute(context);
		assertEquals("/wherever", context.getMockExternalContext().getExternalRedirectResult());
	}

	public void testExecuteWithNullResourceUri() throws Exception {
		try {
			action = new ExternalRedirectAction(null);
			fail("Should have failed");
		} catch (IllegalArgumentException e) {

		}
	}
}
