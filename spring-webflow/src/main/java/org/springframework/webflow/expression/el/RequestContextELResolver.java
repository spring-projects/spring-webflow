package org.springframework.webflow.expression.el;

import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.PropertyNotWritableException;

import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * Custom EL resolver that resolves to a thread-bound RequestContext object for binding expressions prefixed with a
 * {@link #REQUEST_CONTEXT_VARIABLE_NAME}. For instance "#{requestContext.conversationScope.myProperty}".
 * @author Jeremy Grelle
 */
public class RequestContextELResolver extends ELResolver {

	/**
	 * Name of the request context variable.
	 */
	public static final String REQUEST_CONTEXT_VARIABLE_NAME = "requestContext";

	public Class getCommonPropertyType(ELContext elContext, Object base) {
		return Object.class;
	}

	public Iterator getFeatureDescriptors(ELContext elContext, Object base) {
		return null;
	}

	public Class getType(ELContext elContext, Object base, Object property) {
		if (base == null && REQUEST_CONTEXT_VARIABLE_NAME.equals(property)) {
			elContext.setPropertyResolved(true);
			return RequestContext.class;
		} else {
			return null;
		}
	}

	public Object getValue(ELContext elContext, Object base, Object property) {
		if (base == null && REQUEST_CONTEXT_VARIABLE_NAME.equals(property)) {
			elContext.setPropertyResolved(true);
			return RequestContextHolder.getRequestContext();
		} else {
			return null;
		}
	}

	public boolean isReadOnly(ELContext elContext, Object base, Object property) {
		if (base == null && REQUEST_CONTEXT_VARIABLE_NAME.equals(property)) {
			elContext.setPropertyResolved(true);
			return true;
		} else {
			return false;
		}
	}

	public void setValue(ELContext elContext, Object base, Object property, Object value) {
		if (base == null && REQUEST_CONTEXT_VARIABLE_NAME.equals(property)) {
			elContext.setPropertyResolved(true);
			throw new PropertyNotWritableException("The RequestContext cannot be set with an expression.");
		}
	}
}