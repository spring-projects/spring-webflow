package org.springframework.webflow.expression.el;

import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.PropertyNotWritableException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.webflow.engine.AnnotatedAction;
import org.springframework.webflow.execution.Action;

/**
 * Resolves the method to invoke on a resolved Web Flow Action instance. The resolved Action is usually a
 * {@link org.springframework.webflow.action.MultiAction}. Returns an AnnotatedAction wrapper around the target Action
 * configured with the appropriate method dispatching rules.
 * 
 * @author Keith Donald
 */
public class ActionMethodELResolver extends ELResolver {

	private static final Log logger = LogFactory.getLog(ActionMethodELResolver.class);

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
			AnnotatedAction annotated = new AnnotatedAction(action);
			annotated.setMethod(property.toString());
			return annotated;
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