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

public class WebFlowImplicitObjectELResolver extends ELResolver {

	public Class getCommonPropertyType(ELContext context, Object base) {
		return Object.class;
	}

	public Iterator getFeatureDescriptors(ELContext context, Object base) {
		return null;
	}

	public Class getType(ELContext context, Object base, Object property) {
		if (base == null && RequestContextHolder.getRequestContext() == null && ImplicitVariable.matches(property)) {
			return ImplicitVariable.value(context, RequestContextHolder.getRequestContext(), property).getClass();
		}
		return null;
	}

	public Object getValue(ELContext context, Object base, Object property) {
		if (base == null && RequestContextHolder.getRequestContext() == null && ImplicitVariable.matches(property)) {
			return ImplicitVariable.value(context, RequestContextHolder.getRequestContext(), property);
		}
		return null;
	}

	public boolean isReadOnly(ELContext context, Object base, Object property) {
		if (base == null && RequestContextHolder.getRequestContext() == null && ImplicitVariable.matches(property)) {
			context.setPropertyResolved(true);
			return true;
		}
		return false;
	}

	public void setValue(ELContext context, Object base, Object property, Object value) {
		if (base == null && RequestContextHolder.getRequestContext() == null && ImplicitVariable.matches(property)) {
			context.setPropertyResolved(true);
			throw new PropertyNotWritableException("The implicit flow variable " + property + " is not writable.");
		}
	}

	private static final class ImplicitVariable {
		private static final Set vars = new HashSet();

		static {
			vars.add("requestScope");
			vars.add("flashScope");
			vars.add("flowScope");
			vars.add("conversationScope");
			vars.add("requestParameters");
		}

		private static final BeanELResolver internalResolver = new BeanELResolver();

		public static boolean matches(Object property) {
			return vars.contains(property);
		}

		public static Object value(ELContext elContext, RequestContext requestContext, Object property) {
			return internalResolver.getValue(elContext, requestContext, property);
		};
	}
}
