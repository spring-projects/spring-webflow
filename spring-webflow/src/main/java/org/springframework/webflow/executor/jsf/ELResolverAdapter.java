package org.springframework.webflow.executor.jsf;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.PropertyNotWritableException;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.PropertyNotFoundException;
import javax.faces.el.PropertyResolver;
import javax.faces.el.VariableResolver;

/**
 * An adapter for using JSF 1.1 {@link VariableResolver}s and {@link PropertyResolver}s in an {@link ELContext}.
 * @author Jeremy Grelle
 * 
 */
public class ELResolverAdapter extends ELResolver {

    private FacesContext facesContext;

    public ELResolverAdapter(FacesContext facesContext) {
	this.facesContext = facesContext;
    }

    public Class getCommonPropertyType(ELContext context, Object base) {
	return Object.class;
    }

    public Iterator getFeatureDescriptors(ELContext context, Object base) {
	return Collections.EMPTY_LIST.iterator();
    }

    public Class getType(ELContext context, Object base, Object property) {
	if (property == null) {
	    return null;
	}
	try {
	    context.setPropertyResolved(true);
	    if (base == null) {
		Object var = getVariableResolver().resolveVariable(facesContext, property.toString());
		return (var != null) ? var.getClass() : null;
	    } else {
		if (base instanceof List || base.getClass().isArray()) {
		    return getPropertyResolver().getType(base, Integer.parseInt(property.toString()));
		} else {
		    return getPropertyResolver().getType(base, property);
		}
	    }
	} catch (PropertyNotFoundException ex) {
	    throw new javax.el.PropertyNotFoundException(ex.getMessage(), ex.getCause());
	} catch (EvaluationException ex) {
	    throw new ELException(ex.getMessage(), ex.getCause());
	}
    }

    public Object getValue(ELContext context, Object base, Object property) {
	if (property == null) {
	    return null;
	}
	try {
	    context.setPropertyResolved(true);
	    if (base == null) {
		return getVariableResolver().resolveVariable(facesContext, property.toString());
	    } else {
		if (base instanceof List || base.getClass().isArray()) {
		    return getPropertyResolver().getValue(base, Integer.parseInt(property.toString()));
		} else {
		    return getPropertyResolver().getValue(base, property);
		}
	    }
	} catch (PropertyNotFoundException ex) {
	    throw new javax.el.PropertyNotFoundException(ex.getMessage(), ex.getCause());
	} catch (EvaluationException ex) {
	    throw new ELException(ex.getMessage(), ex.getCause());
	}
    }

    public boolean isReadOnly(ELContext context, Object base, Object property) {
	if (property == null) {
	    return true;
	}
	try {
	    context.setPropertyResolved(true);
	    if (base == null) {
		return false; // VariableResolver provides no way to determine isReadOnly
	    } else {
		if (base instanceof List || base.getClass().isArray()) {
		    return getPropertyResolver().isReadOnly(base, Integer.parseInt(property.toString()));
		} else {
		    return getPropertyResolver().isReadOnly(base, property);
		}
	    }
	} catch (PropertyNotFoundException ex) {
	    throw new javax.el.PropertyNotFoundException(ex.getMessage(), ex.getCause());
	} catch (EvaluationException ex) {
	    throw new ELException(ex.getMessage(), ex.getCause());
	}
    }

    public void setValue(ELContext context, Object base, Object property, Object value) {
	if (property == null) {
	    throw new PropertyNotWritableException("Property is Null");
	}
	try {
	    context.setPropertyResolved(true);
	    if (base instanceof List || base.getClass().isArray()) {
		getPropertyResolver().setValue(base, Integer.parseInt(property.toString()), value);
	    } else {
		getPropertyResolver().setValue(base, property, value);
	    }

	} catch (PropertyNotFoundException ex) {
	    throw new javax.el.PropertyNotFoundException(ex.getMessage(), ex.getCause());
	} catch (EvaluationException ex) {
	    throw new ELException(ex.getMessage(), ex.getCause());
	}
    }

    private VariableResolver getVariableResolver() {
	return facesContext.getApplication().getVariableResolver();
    }

    private PropertyResolver getPropertyResolver() {
	return facesContext.getApplication().getPropertyResolver();
    }

}
