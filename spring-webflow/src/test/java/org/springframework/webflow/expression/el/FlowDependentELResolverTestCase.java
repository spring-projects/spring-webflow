package org.springframework.webflow.expression.el;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.List;
import javax.el.ELContext;
import javax.el.ELResolver;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.binding.expression.el.DefaultELContext;
import org.springframework.binding.expression.el.DefaultELResolver;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.test.MockFlowSession;
import org.springframework.webflow.test.MockRequestContext;

public abstract class FlowDependentELResolverTestCase {

	protected ELContext context;

	@Before
	public void setUp() {
		context = new DefaultELContext(new DefaultELResolver(getCustomResolvers()), null, null);
	}

	@After
	public void tearDown() {
		RequestContextHolder.setRequestContext(null);
	}

	@Test
	public void testGetType_NoActiveFlow() {
		assertNull("getType should return null when no flow is active",
				context.getELResolver().getType(context, null, getBaseVariable()));
		assertFalse(context.isPropertyResolved());
	}

	@Test
	public void testGetValue_NoActiveFlow() {
		assertNull("getValue should return null when no flow is active",
				context.getELResolver().getValue(context, null, getBaseVariable()));
		assertFalse(context.isPropertyResolved());
	}

	@Test
	public void testIsReadOnly_NoActiveFlow() {
		assertFalse("isReadOnly should return false when no flow is active",
				context.getELResolver().isReadOnly(context, null, getBaseVariable()));
		assertFalse(context.isPropertyResolved());
	}

	@Test
	public void testSetValue_NoActiveFlow() {
		context.getELResolver().setValue(context, null, getBaseVariable(), null);
		assertFalse("setValue should be a no-op when no flow is active", context.isPropertyResolved());
	}

	protected void initView(MockRequestContext requestContext) {
		((MockFlowSession) requestContext.getFlowExecutionContext().getActiveSession()).setState(new ViewState(
				requestContext.getRootFlow(), "view", context -> {
					throw new UnsupportedOperationException("Auto-generated method stub");
				}));
	}

	protected abstract String getBaseVariable();

	protected abstract List<ELResolver> getCustomResolvers();

}
