package org.springframework.webflow.expression.el;

import java.util.List;
import javax.el.ELContext;
import javax.el.ELResolver;

import junit.framework.TestCase;

import org.springframework.binding.expression.el.DefaultELContext;
import org.springframework.binding.expression.el.DefaultELResolver;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.test.MockFlowSession;
import org.springframework.webflow.test.MockRequestContext;

public abstract class FlowDependentELResolverTestCase extends TestCase {

	protected ELContext context;

	public void setUp() {
		context = new DefaultELContext(new DefaultELResolver(getCustomResolvers()), null, null);
	}

	public void tearDown() {
		RequestContextHolder.setRequestContext(null);
	}

	public void testGetType_NoActiveFlow() {
		assertNull("getType should return null when no flow is active",
				context.getELResolver().getType(context, null, getBaseVariable()));
		assertFalse(context.isPropertyResolved());
	}

	public void testGetValue_NoActiveFlow() {
		assertNull("getValue should return null when no flow is active",
				context.getELResolver().getValue(context, null, getBaseVariable()));
		assertFalse(context.isPropertyResolved());
	}

	public void testIsReadOnly_NoActiveFlow() {
		assertFalse("isReadOnly should return false when no flow is active",
				context.getELResolver().isReadOnly(context, null, getBaseVariable()));
		assertFalse(context.isPropertyResolved());
	}

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
