package org.springframework.webflow.expression.el;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import jakarta.el.ELResolver;
import jakarta.el.PropertyNotWritableException;

import org.junit.jupiter.api.Test;

import org.springframework.binding.message.MessageContext;
import org.springframework.util.ClassUtils;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowExecutionContext;
import org.springframework.webflow.test.MockFlowExecutionKey;
import org.springframework.webflow.test.MockRequestContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ImplicitFlowVariableELResolverTests extends FlowDependentELResolverTestCase {

	private static final List<String> vars = new ArrayList<>();
	{
		vars.add("requestParameters");
		vars.add("requestScope");
		vars.add("flashScope");
		vars.add("viewScope");
		vars.add("flowScope");
		vars.add("conversationScope");
		vars.add("messageContext");
		vars.add("externalContext");
		vars.add("flowExecutionContext");
		vars.add("flowExecutionUrl");
		vars.add("currentUser");
		vars.add("currentEvent");
	};

	@Test
	public void testGetType_RequestParameters() {
		RequestContextHolder.setRequestContext(new MockRequestContext());
		assertTrue(ClassUtils.isAssignable(ParameterMap.class,
				context.getELResolver().getType(context, null, "requestParameters")));
		assertTrue(context.isPropertyResolved());
	}

	@Test
	public void testGetType_RequestScope() {
		RequestContextHolder.setRequestContext(new MockRequestContext());
		assertTrue(ClassUtils.isAssignable(MutableAttributeMap.class,
				context.getELResolver().getType(context, null, "requestScope")));
		assertTrue(context.isPropertyResolved());
	}

	@Test
	public void testGetType_FlashScope() {
		RequestContextHolder.setRequestContext(new MockRequestContext());
		assertTrue(ClassUtils.isAssignable(MutableAttributeMap.class,
				context.getELResolver().getType(context, null, "flashScope")));
		assertTrue(context.isPropertyResolved());
	}

	@Test
	public void testGetType_ViewScope() {
		MockRequestContext requestContext = new MockRequestContext();
		RequestContextHolder.setRequestContext(requestContext);
		initView(requestContext);

		assertTrue(ClassUtils.isAssignable(MutableAttributeMap.class,
				context.getELResolver().getType(context, null, "viewScope")));
		assertTrue(context.isPropertyResolved());
	}

	@Test
	public void testGetType_FlowScope() {
		RequestContextHolder.setRequestContext(new MockRequestContext());
		assertTrue(ClassUtils.isAssignable(MutableAttributeMap.class,
				context.getELResolver().getType(context, null, "flowScope")));
		assertTrue(context.isPropertyResolved());
	}

	@Test
	public void testGetType_ConversationScope() {
		RequestContextHolder.setRequestContext(new MockRequestContext());
		assertTrue(ClassUtils.isAssignable(MutableAttributeMap.class,
				context.getELResolver().getType(context, null, "conversationScope")));
		assertTrue(context.isPropertyResolved());
	}

	@Test
	public void testGetType_MessageContext() {
		RequestContextHolder.setRequestContext(new MockRequestContext());
		assertTrue(ClassUtils.isAssignable(MessageContext.class,
				context.getELResolver().getType(context, null, "messageContext")));
		assertTrue(context.isPropertyResolved());
	}

	@Test
	public void testGetType_ExternalContext() {
		RequestContextHolder.setRequestContext(new MockRequestContext());
		assertTrue(ClassUtils.isAssignable(ExternalContext.class,
				context.getELResolver().getType(context, null, "externalContext")));
		assertTrue(context.isPropertyResolved());
	}

	@Test
	public void testGetType_FlowExecutionContext() {
		RequestContextHolder.setRequestContext(new MockRequestContext());
		assertTrue(ClassUtils.isAssignable(FlowExecutionContext.class,
				context.getELResolver().getType(context, null, "flowExecutionContext")));
		assertTrue(context.isPropertyResolved());
	}

	@Test
	public void testGetType_FlowExecutionUrl() {
		MockRequestContext requestContext = new MockRequestContext();
		((MockFlowExecutionContext) requestContext.getFlowExecutionContext()).setKey(new MockFlowExecutionKey("e1s1"));
		RequestContextHolder.setRequestContext(requestContext);

		assertTrue(ClassUtils.isAssignable(String.class,
				context.getELResolver().getType(context, null, "flowExecutionUrl")));
		assertTrue(context.isPropertyResolved());
	}

	@Test
	public void testGetType_CurrentUser() {
		MockRequestContext requestContext = new MockRequestContext();
		((MockExternalContext) requestContext.getExternalContext()).setCurrentUser("jjg");
		RequestContextHolder.setRequestContext(requestContext);

		assertTrue(ClassUtils.isAssignable(Principal.class,
				context.getELResolver().getType(context, null, "currentUser")));
		assertTrue(context.isPropertyResolved());
	}

	@Test
	public void testGetType_CurrentEvent() {
		MockRequestContext requestContext = new MockRequestContext();
		requestContext.setCurrentEvent(new Event(this, "foo"));
		RequestContextHolder.setRequestContext(requestContext);
		assertTrue(ClassUtils.isAssignable(Event.class, context.getELResolver().getType(context, null, "currentEvent")));
		assertTrue(context.isPropertyResolved());
	}

	@Test
	public void testGetValue_RequestParameters() {
		MockRequestContext requestContext = new MockRequestContext();
		RequestContextHolder.setRequestContext(requestContext);
		assertSame(requestContext.getRequestParameters(),
				context.getELResolver().getValue(context, null, "requestParameters"));
		assertTrue(context.isPropertyResolved());
	}

	@Test
	public void testGetValue_RequestScope() {
		MockRequestContext requestContext = new MockRequestContext();
		RequestContextHolder.setRequestContext(requestContext);
		assertSame(requestContext.getRequestScope(), context.getELResolver().getValue(context, null, "requestScope"));
		assertTrue(context.isPropertyResolved());
	}

	@Test
	public void testGetValue_FlashScope() {
		MockRequestContext requestContext = new MockRequestContext();
		RequestContextHolder.setRequestContext(requestContext);
		assertSame(requestContext.getFlashScope(), context.getELResolver().getValue(context, null, "flashScope"));
		assertTrue(context.isPropertyResolved());
	}

	@Test
	public void testGetValue_ViewScope() {
		MockRequestContext requestContext = new MockRequestContext();
		initView(requestContext);
		RequestContextHolder.setRequestContext(requestContext);
		assertSame(requestContext.getViewScope(), context.getELResolver().getValue(context, null, "viewScope"));
		assertTrue(context.isPropertyResolved());
	}

	@Test
	public void testGetValue_FlowScope() {
		MockRequestContext requestContext = new MockRequestContext();
		RequestContextHolder.setRequestContext(requestContext);
		assertSame(requestContext.getFlowScope(), context.getELResolver().getValue(context, null, "flowScope"));
		assertTrue(context.isPropertyResolved());
	}

	@Test
	public void testGetValue_ConversationScope() {
		MockRequestContext requestContext = new MockRequestContext();
		RequestContextHolder.setRequestContext(requestContext);
		assertSame(requestContext.getConversationScope(),
				context.getELResolver().getValue(context, null, "conversationScope"));
		assertTrue(context.isPropertyResolved());
	}

	@Test
	public void testGetValue_MessageContext() {
		MockRequestContext requestContext = new MockRequestContext();
		RequestContextHolder.setRequestContext(requestContext);
		assertSame(requestContext.getMessageContext(), context.getELResolver()
				.getValue(context, null, "messageContext"));
		assertTrue(context.isPropertyResolved());
	}

	@Test
	public void testGetValue_ExternalContext() {
		MockRequestContext requestContext = new MockRequestContext();
		RequestContextHolder.setRequestContext(requestContext);
		assertSame(requestContext.getExternalContext(),
				context.getELResolver().getValue(context, null, "externalContext"));
		assertTrue(context.isPropertyResolved());
	}

	@Test
	public void testGetValue_FlowExecutionContext() {
		MockRequestContext requestContext = new MockRequestContext();
		RequestContextHolder.setRequestContext(requestContext);
		assertSame(requestContext.getFlowExecutionContext(),
				context.getELResolver().getValue(context, null, "flowExecutionContext"));
		assertTrue(context.isPropertyResolved());
	}

	@Test
	public void testGetValue_FlowExecutionUrl() {
		MockRequestContext requestContext = new MockRequestContext();
		((MockFlowExecutionContext) requestContext.getFlowExecutionContext()).setKey(new MockFlowExecutionKey("e1s1"));
		RequestContextHolder.setRequestContext(requestContext);
		assertEquals(requestContext.getFlowExecutionUrl(),
				context.getELResolver().getValue(context, null, "flowExecutionUrl"));
		assertTrue(context.isPropertyResolved());
	}

	@Test
	public void testGetValue_CurrentUser() {
		MockRequestContext requestContext = new MockRequestContext();
		((MockExternalContext) requestContext.getExternalContext()).setCurrentUser("jjg");
		RequestContextHolder.setRequestContext(requestContext);
		assertSame(requestContext.getExternalContext().getCurrentUser(),
				context.getELResolver().getValue(context, null, "currentUser"));
		assertTrue(context.isPropertyResolved());
	}

	@Test
	public void testGetValue_CurrentEvent() {
		MockRequestContext requestContext = new MockRequestContext();
		requestContext.setCurrentEvent(new Event(this, "foo"));
		RequestContextHolder.setRequestContext(requestContext);
		assertSame(requestContext.getCurrentEvent(), context.getELResolver().getValue(context, null, "currentEvent"));
		assertTrue(context.isPropertyResolved());
	}

	@Test
	public void testIsReadOnly_AllVars() {
		RequestContextHolder.setRequestContext(new MockRequestContext());
		for (String var : vars) {
			assertTrue(context.getELResolver().isReadOnly(context, null, var));
		}
	}

	@Test
	public void testSetValue_AllVars() {
		RequestContextHolder.setRequestContext(new MockRequestContext());
		for (String var : vars) {
			try {
				context.getELResolver().setValue(context, null, var, new Object());
				fail("setValue should not be allowed");
			} catch (PropertyNotWritableException ex) {
				assertTrue(context.isPropertyResolved());
			}
		}
	}

	protected String getBaseVariable() {
		return "flowScope";
	}

	protected List<ELResolver> getCustomResolvers() {
		List<ELResolver> resolvers = new ArrayList<>();
		resolvers.add(new ImplicitFlowVariableELResolver());
		return resolvers;
	}

}
