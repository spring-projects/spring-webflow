package org.springframework.webflow.expression.el;

import java.util.Iterator;
import java.util.Locale;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.PropertyNotWritableException;

import org.springframework.context.MessageSource;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * Custom EL resolver that resolves the messages from the active flow's {@link MessageSource}. The message source is
 * indexed under the key <code>resourceBundle</code>. To access a message, specify its key e.g.
 * <code>resourceBundle.myMessage</code>.
 * 
 * @author Jeremy Grelle
 */
public class FlowResourceELResolver extends ELResolver {

	private String RESOURCE_BUNDLE_KEY = "resourceBundle";

	private RequestContext requestContext;

	public FlowResourceELResolver() {
	}

	public FlowResourceELResolver(RequestContext requestContext) {
		this.requestContext = requestContext;
	}

	public Class getCommonPropertyType(ELContext context, Object base) {
		if (base == null) {
			return MessageSource.class;
		} else if (base instanceof MessageSource) {
			return String.class;
		} else {
			return null;
		}
	}

	public Iterator getFeatureDescriptors(ELContext context, Object base) {
		return null;
	}

	public Class getType(ELContext context, Object base, Object property) {
		if (base == null && RESOURCE_BUNDLE_KEY.equals(property)) {
			context.setPropertyResolved(true);
			return MessageSource.class;
		} else if (base instanceof MessageSource) {
			MessageSource messageSource = (MessageSource) base;
			String message = messageSource.getMessage(property.toString(), null, null, getLocale());
			if (message != null) {
				context.setPropertyResolved(true);
				return String.class;
			}
		}
		return null;
	}

	public Object getValue(ELContext context, Object base, Object property) {
		if (base == null && RESOURCE_BUNDLE_KEY.equals(property)) {
			context.setPropertyResolved(true);
			return getMessageSource();
		} else if (base instanceof MessageSource) {
			MessageSource messageSource = (MessageSource) base;
			String message = messageSource.getMessage(property.toString(), null, null, getLocale());
			if (message != null) {
				context.setPropertyResolved(true);
				return message;
			}
		}
		return null;
	}

	public boolean isReadOnly(ELContext context, Object base, Object property) {
		if (base == null && RESOURCE_BUNDLE_KEY.equals(property)) {
			context.setPropertyResolved(true);
			return true;
		}
		return false;
	}

	public void setValue(ELContext context, Object base, Object property, Object value) {
		if (base == null && RESOURCE_BUNDLE_KEY.equals(property)) {
			throw new PropertyNotWritableException("The flow's MessageSource is not writable.");
		}
	}

	protected Locale getLocale() {
		return getRequestContext().getExternalContext().getLocale();
	}

	protected MessageSource getMessageSource() {
		return getRequestContext().getActiveFlow().getApplicationContext();
	}

	protected RequestContext getRequestContext() {
		return requestContext != null ? requestContext : RequestContextHolder.getRequestContext();
	}

}
