package org.springframework.faces.webflow;

import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.PropertyNotWritableException;
import javax.faces.context.FacesContext;

import org.springframework.context.MessageSource;
import org.springframework.util.StringUtils;
import org.springframework.webflow.execution.RequestContextHolder;

public class FlowMessageSourceResolver extends ELResolver {

	private String MESSAGE_SOURCE_KEY = "resourceBundle";

	public Class getCommonPropertyType(ELContext context, Object base) {
		return Object.class;
	}

	public Iterator getFeatureDescriptors(ELContext context, Object base) {
		return null;
	}

	public Class getType(ELContext context, Object base, Object property) {
		if (base == null && MESSAGE_SOURCE_KEY.equals(property)) {
			context.setPropertyResolved(true);
			return MessageSource.class;
		} else if (base != null && base instanceof MessageSource) {
			MessageSource messageSource = (MessageSource) base;
			String message = messageSource.getMessage(property.toString(), null, FacesContext.getCurrentInstance()
					.getViewRoot().getLocale());
			if (StringUtils.hasText(message)) {
				context.setPropertyResolved(true);
				return String.class;
			}
		}
		return null;
	}

	public Object getValue(ELContext context, Object base, Object property) {
		if (base == null && MESSAGE_SOURCE_KEY.equals(property)) {
			context.setPropertyResolved(true);
			return RequestContextHolder.getRequestContext().getActiveFlow().getApplicationContext();
		} else if (base != null && base instanceof MessageSource) {
			MessageSource messageSource = (MessageSource) base;
			String message = messageSource.getMessage(property.toString(), null, FacesContext.getCurrentInstance()
					.getViewRoot().getLocale());
			if (StringUtils.hasText(message)) {
				context.setPropertyResolved(true);
				return message;
			}
		}
		return null;
	}

	public boolean isReadOnly(ELContext context, Object base, Object property) {
		if (base == null && MESSAGE_SOURCE_KEY.equals(property)) {
			context.setPropertyResolved(true);
			return true;
		}
		return false;
	}

	public void setValue(ELContext context, Object base, Object property, Object value) {
		if (base == null && MESSAGE_SOURCE_KEY.equals(property)) {
			throw new PropertyNotWritableException("The flow's MessageSource is not writable.");
		}
	}

}
