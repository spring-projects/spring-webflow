package org.springframework.webflow.core.expression.el;

import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELResolver;

import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * Custom EL resolver that searches the current request context for variables to resolve. The search algorithm looks in
 * request scope first, then flash scope, then flow scope, then conversation scope.
 * 
 * Suitable for use along side other variable resolvers to support EL binding expressions like "#{bean.property}" where
 * "bean" could be a property in any supported scope.
 * 
 * @author Jeremy Grelle
 */
public class ScopeSearchingELResolver extends ELResolver {

	public Class getCommonPropertyType(ELContext elContext, Object base) {
		return Object.class;
	}

	public Iterator getFeatureDescriptors(ELContext elContext, Object base) {
		return null;
	}

	public Class getType(ELContext elContext, Object base, Object property) {
		RequestContext requestContext;
		if (base != null && base instanceof RequestContext) {
			requestContext = (RequestContext) base;
		} else if (base == null) {
			requestContext = RequestContextHolder.getRequestContext();
		} else {
			return null;
		}
		if (requestContext == null) {
			return null;
		}
		// flow execution is active: try request/flash/flow/conversation scope
		String name = property.toString();
		if (requestContext.getRequestScope().contains(name)) {
			elContext.setPropertyResolved(true);
			return requestContext.getRequestScope().get(name).getClass();
		} else if (requestContext.getFlashScope().contains(name)) {
			elContext.setPropertyResolved(true);
			return requestContext.getFlashScope().get(name).getClass();
		} else if (requestContext.getFlowScope().contains(name)) {
			elContext.setPropertyResolved(true);
			return requestContext.getFlowScope().get(name).getClass();
		} else if (requestContext.getConversationScope().contains(name)) {
			elContext.setPropertyResolved(true);
			return requestContext.getConversationScope().get(name).getClass();
		} else {
			return null;
		}
	}

	public Object getValue(ELContext elContext, Object base, Object property) {
		RequestContext requestContext;
		if (base != null && base instanceof RequestContext) {
			requestContext = (RequestContext) base;
		} else if (base == null) {
			requestContext = RequestContextHolder.getRequestContext();
		} else {
			return null;
		}
		if (requestContext == null) {
			return null;
		}
		String name = property.toString();
		if (requestContext.getRequestScope().contains(name)) {
			elContext.setPropertyResolved(true);
			return requestContext.getRequestScope().get(name);
		} else if (requestContext.getFlashScope().contains(name)) {
			elContext.setPropertyResolved(true);
			return requestContext.getFlashScope().get(name);
		} else if (requestContext.getFlowScope().contains(name)) {
			elContext.setPropertyResolved(true);
			return requestContext.getFlowScope().get(name);
		} else if (requestContext.getConversationScope().contains(name)) {
			elContext.setPropertyResolved(true);
			return requestContext.getConversationScope().get(name);
		} else {
			return null;
		}
	}

	public boolean isReadOnly(ELContext elContext, Object base, Object property) {
		RequestContext requestContext;
		if (base != null && base instanceof RequestContext) {
			requestContext = (RequestContext) base;
		} else if (base == null) {
			requestContext = RequestContextHolder.getRequestContext();
		} else {
			return false;
		}
		if (requestContext == null) {
			return false;
		}
		// flow execution is active: try request/flash/flow/conversation scope
		String name = property.toString();
		if (requestContext.getRequestScope().contains(name)) {
			elContext.setPropertyResolved(true);
			return false;
		} else if (requestContext.getFlashScope().contains(name)) {
			elContext.setPropertyResolved(true);
			return false;
		} else if (requestContext.getFlowScope().contains(name)) {
			elContext.setPropertyResolved(true);
			return false;
		} else if (requestContext.getConversationScope().contains(name)) {
			elContext.setPropertyResolved(true);
			return false;
		} else {
			return false;
		}
	}

	public void setValue(ELContext elContext, Object base, Object property, Object value) {
		RequestContext requestContext;
		if (base != null && base instanceof RequestContext) {
			requestContext = (RequestContext) base;
		} else if (base == null) {
			requestContext = RequestContextHolder.getRequestContext();
		} else {
			return;
		}
		if (requestContext == null) {
			return;
		}
		// flow execution is active: try request/flash/flow/conversation scope
		String name = property.toString();
		if (requestContext.getRequestScope().contains(name)) {
			elContext.setPropertyResolved(true);
			requestContext.getRequestScope().put(name, value);
		} else if (requestContext.getFlashScope().contains(name)) {
			elContext.setPropertyResolved(true);
			requestContext.getFlashScope().put(name, value);
		} else if (requestContext.getFlowScope().contains(name)) {
			elContext.setPropertyResolved(true);
			requestContext.getFlowScope().put(name, value);
		} else if (requestContext.getConversationScope().contains(name)) {
			elContext.setPropertyResolved(true);
			requestContext.getConversationScope().put(name, value);
		}
	}
}