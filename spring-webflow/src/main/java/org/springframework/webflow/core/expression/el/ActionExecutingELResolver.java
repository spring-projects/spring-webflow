package org.springframework.webflow.core.expression.el;

import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.PropertyNotWritableException;

import org.springframework.webflow.engine.ActionExecutor;
import org.springframework.webflow.engine.AnnotatedAction;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.RequestContextHolder;

public class ActionExecutingELResolver extends ELResolver {

	public Class getCommonPropertyType(ELContext elContext, Object base) {
		return Action.class;
	}

	public Iterator getFeatureDescriptors(ELContext elContext, Object base) {
		return null;
	}

	public Class getType(ELContext elContext, Object base, Object property) {
		if (base instanceof Action) {
			elContext.setPropertyResolved(true);
			return Action.class;
		} else {
			return null;
		}
	}

	public Object getValue(ELContext elContext, Object base, Object property) {
		if (base instanceof Action) {
			Action action = (Action) base;
			elContext.setPropertyResolved(true);
			if (property == null) {
				return ActionExecutor.execute(action, RequestContextHolder.getRequestContext());
			} else {
				AnnotatedAction decorator = new AnnotatedAction(action);
				decorator.setMethod((String) property);
				return ActionExecutor.execute(decorator, RequestContextHolder.getRequestContext());
			}
		} else {
			return null;
		}
	}

	public boolean isReadOnly(ELContext elContext, Object base, Object property) {
		if (base instanceof Action) {
			elContext.setPropertyResolved(true);
			return true;
		} else {
			return false;
		}
	}

	public void setValue(ELContext elContext, Object base, Object property, Object value) {
		if (base instanceof Action) {
			elContext.setPropertyResolved(true);
			throw new PropertyNotWritableException("The Action cannot be set with an expression.");
		}
	}
}