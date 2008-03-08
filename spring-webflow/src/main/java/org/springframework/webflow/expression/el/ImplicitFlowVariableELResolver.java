package org.springframework.webflow.expression.el;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.el.BeanELResolver;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.PropertyNotWritableException;

import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

public class ImplicitFlowVariableELResolver extends ELResolver {

	private RequestContext requestContext;

	public ImplicitFlowVariableELResolver() {
	}

	public ImplicitFlowVariableELResolver(RequestContext requestContext) {
		this.requestContext = requestContext;
	}

	public Class getCommonPropertyType(ELContext context, Object base) {
		return Object.class;
	}

	public Iterator getFeatureDescriptors(ELContext context, Object base) {
		return null;
	}

	public Class getType(ELContext context, Object base, Object property) {
		RequestContext requestContext = getRequestContext();
		if (base != null || requestContext == null) {
			return null;
		}
		if (ImplicitVariables.matches(property)) {
			context.setPropertyResolved(true);
			return ImplicitVariables.value(context, requestContext, property).getClass();
		} else {
			return null;
		}
	}

	public Object getValue(ELContext context, Object base, Object property) {
		RequestContext requestContext = getRequestContext();
		if (base != null || requestContext == null) {
			return null;
		}
		if (ImplicitVariables.matches(property)) {
			context.setPropertyResolved(true);
			return ImplicitVariables.value(context, requestContext, property);
		} else {
			return null;
		}
	}

	public boolean isReadOnly(ELContext context, Object base, Object property) {
		if (base != null) {
			return false;
		}
		if (ImplicitVariables.matches(property)) {
			context.setPropertyResolved(true);
			return true;
		} else {
			return false;
		}
	}

	public void setValue(ELContext context, Object base, Object property, Object value) {
		if (base != null) {
			return;
		}
		if (ImplicitVariables.matches(property)) {
			context.setPropertyResolved(true);
			throw new PropertyNotWritableException("The implicit flow variable " + property + " is not writable.");
		}
	}

	protected RequestContext getRequestContext() {
		return requestContext != null ? requestContext : RequestContextHolder.getRequestContext();
	}

	private static final class ImplicitVariables {
		private static final Set vars = new HashSet();

		static {
			vars.add("requestParameters");
			vars.add("requestScope");
			vars.add("flashScope");
			vars.add("flowScope");
			vars.add("conversationScope");
			vars.add("messageContext");
			vars.add("flowExecutionContext");
			vars.add("flowExecutionUrl");
		}

		private static final BeanELResolver internalResolver = new BeanELResolver();

		public static boolean matches(Object property) {
			return vars.contains(property);
		}

		public static Object value(ELContext elContext, RequestContext requestContext, Object property) {
			return internalResolver.getValue(elContext, requestContext, property);
		}
	}
}
