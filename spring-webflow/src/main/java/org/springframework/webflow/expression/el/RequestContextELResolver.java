package org.springframework.webflow.expression.el;

import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.PropertyNotWritableException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * Custom EL resolver that resolves to a thread-bound RequestContext object for binding expressions prefixed with a
 * {@link #REQUEST_CONTEXT_VARIABLE_NAME}. For instance "#{flowRequestContext.conversationScope.myProperty}".
 * @author Jeremy Grelle
 */
public class RequestContextELResolver extends ELResolver {

	/**
	 * Name of the request context variable.
	 */
	public static final String REQUEST_CONTEXT_VARIABLE_NAME = "flowRequestContext";

	private static final Log logger = LogFactory.getLog(RequestContextELResolver.class);

	private RequestContext context;

	public RequestContextELResolver() {
	}

	public RequestContextELResolver(RequestContext context) {
		this.context = context;
	}

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
			if (logger.isDebugEnabled()) {
				logger.debug("Successfully resolved the current RequestContext under variable '" + property + "'");
			}
			elContext.setPropertyResolved(true);
			return getRequestContext();
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

	protected RequestContext getRequestContext() {
		return context != null ? context : RequestContextHolder.getRequestContext();
	}

}