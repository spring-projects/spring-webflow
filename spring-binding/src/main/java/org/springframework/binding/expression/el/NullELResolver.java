package org.springframework.binding.expression.el;

import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.PropertyNotWritableException;

/**
 * Resolves the special 'null' variable, indicating a null value.
 * 
 * @author Keith Donald
 */
public class NullELResolver extends ELResolver {

	private static final String NULL_VARIABLE_NAME = "null";

	public Class getCommonPropertyType(ELContext context, Object base) {
		return null;
	}

	public Iterator getFeatureDescriptors(ELContext context, Object base) {
		return null;
	}

	public Class getType(ELContext context, Object base, Object property) {
		if (base == null && NULL_VARIABLE_NAME.equals(property)) {
			context.setPropertyResolved(true);
			return null;
		} else {
			return null;
		}
	}

	public Object getValue(ELContext context, Object base, Object property) {
		if (base == null && NULL_VARIABLE_NAME.equals(property)) {
			context.setPropertyResolved(true);
			return null;
		} else {
			return null;
		}
	}

	public boolean isReadOnly(ELContext context, Object base, Object property) {
		if (base == null && NULL_VARIABLE_NAME.equals(property)) {
			context.setPropertyResolved(true);
			return true;
		} else {
			return false;
		}
	}

	public void setValue(ELContext context, Object base, Object property, Object value) {
		if (base == null && NULL_VARIABLE_NAME.equals(property)) {
			context.setPropertyResolved(true);
			throw new PropertyNotWritableException("The 'null' value cannot be set");
		}
	}

}
