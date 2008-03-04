package org.springframework.webflow.expression.el;

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
		if (base != null) {
			return null;
		}
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		String attributeName = property.toString();
		if (requestContext.getRequestScope().contains(attributeName)) {
			elContext.setPropertyResolved(true);
			return requestContext.getRequestScope().get(attributeName).getClass();
		} else if (requestContext.getFlashScope().contains(attributeName)) {
			elContext.setPropertyResolved(true);
			return requestContext.getFlashScope().get(attributeName).getClass();
		} else if (requestContext.getFlowScope().contains(attributeName)) {
			elContext.setPropertyResolved(true);
			return requestContext.getFlowScope().get(attributeName).getClass();
		} else if (requestContext.getConversationScope().contains(attributeName)) {
			elContext.setPropertyResolved(true);
			return requestContext.getConversationScope().get(attributeName).getClass();
		} else {
			return null;
		}
	}

	public Object getValue(ELContext elContext, Object base, Object property) {
		if (base != null) {
			return null;
		}
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		String attributeName = property.toString();
		if (requestContext.getRequestScope().contains(attributeName)) {
			elContext.setPropertyResolved(true);
			return requestContext.getRequestScope().get(attributeName);
		} else if (requestContext.getFlashScope().contains(attributeName)) {
			elContext.setPropertyResolved(true);
			return requestContext.getFlashScope().get(attributeName);
		} else if (requestContext.getFlowScope().contains(attributeName)) {
			elContext.setPropertyResolved(true);
			return requestContext.getFlowScope().get(attributeName);
		} else if (requestContext.getConversationScope().contains(attributeName)) {
			elContext.setPropertyResolved(true);
			return requestContext.getConversationScope().get(attributeName);
		} else {
			return null;
		}
	}

	public boolean isReadOnly(ELContext elContext, Object base, Object property) {
		if (base != null) {
			return false;
		}
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		String attributeName = property.toString();
		if (requestContext.getRequestScope().contains(attributeName)) {
			elContext.setPropertyResolved(true);
			return false;
		} else if (requestContext.getFlashScope().contains(attributeName)) {
			elContext.setPropertyResolved(true);
			return false;
		} else if (requestContext.getFlowScope().contains(attributeName)) {
			elContext.setPropertyResolved(true);
			return false;
		} else if (requestContext.getConversationScope().contains(attributeName)) {
			elContext.setPropertyResolved(true);
			return false;
		} else {
			return false;
		}
	}

	public void setValue(ELContext elContext, Object base, Object property, Object value) {
		if (base != null) {
			return;
		}
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		String attributeName = property.toString();
		if (requestContext.getRequestScope().contains(attributeName)) {
			elContext.setPropertyResolved(true);
			requestContext.getRequestScope().put(attributeName, value);
		} else if (requestContext.getFlashScope().contains(attributeName)) {
			elContext.setPropertyResolved(true);
			requestContext.getFlashScope().put(attributeName, value);
		} else if (requestContext.getFlowScope().contains(attributeName)) {
			elContext.setPropertyResolved(true);
			requestContext.getFlowScope().put(attributeName, value);
		} else if (requestContext.getConversationScope().contains(attributeName)) {
			elContext.setPropertyResolved(true);
			requestContext.getConversationScope().put(attributeName, value);
		}
	}
}