package org.springframework.webflow.expression.el;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import javax.el.ELContext;
import javax.el.ELResolver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.binding.expression.el.DefaultELContext;
import org.springframework.binding.expression.el.DefaultELResolver;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.test.MockFlowSession;
import org.springframework.webflow.test.MockRequestContext;

public abstract class FlowDependentELResolverTestCase {

	protected ELContext context;

	@BeforeEach
	public void setUp() {
		context = new DefaultELContext(new DefaultELResolver(getCustomResolvers()), null, null);
	}

	@AfterEach
	public void tearDown() {
		RequestContextHolder.setRequestContext(null);
	}

	@Test
	public void testGetType_NoActiveFlow() {
		assertNull(context.getELResolver().getType(context, null, getBaseVariable()),
				"getType should return null when no flow is active");
		assertFalse(context.isPropertyResolved());
	}

	@Test
	public void testGetValue_NoActiveFlow() {
		assertNull(context.getELResolver().getValue(context, null, getBaseVariable()),
				"getValue should return null when no flow is active");
		assertFalse(context.isPropertyResolved());
	}

	@Test
	public void testIsReadOnly_NoActiveFlow() {
		assertFalse(context.getELResolver().isReadOnly(context, null, getBaseVariable()),
				"isReadOnly should return false when no flow is active");
		assertFalse(context.isPropertyResolved());
	}

	@Test
	public void testSetValue_NoActiveFlow() {
		context.getELResolver().setValue(context, null, getBaseVariable(), null);
		assertFalse(context.isPropertyResolved(), "setValue should be a no-op when no flow is active");
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
