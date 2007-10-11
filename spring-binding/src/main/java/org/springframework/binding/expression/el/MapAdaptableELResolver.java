package org.springframework.binding.expression.el;

import java.util.Map;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.MapELResolver;

import org.springframework.binding.collection.MapAdaptable;

/**
 * An {@link ELResolver} for properly resolving variables in an instance of {@link MapAdaptable}
 * @author Jeremy Grelle
 */
public class MapAdaptableELResolver extends MapELResolver {

	public Class getType(ELContext context, Object base, Object property) {
		if (base instanceof MapAdaptable) {
			return super.getType(context, adapt(base), property);
		} else {
			return null;
		}
	}

	public Object getValue(ELContext context, Object base, Object property) {
		if (base instanceof MapAdaptable) {
			return super.getValue(context, adapt(base), property);
		} else {
			return null;
		}
	}

	public boolean isReadOnly(ELContext context, Object base, Object property) {
		if (base instanceof MapAdaptable) {
			return super.isReadOnly(context, adapt(base), property);
		} else {
			return false;
		}
	}

	public void setValue(ELContext context, Object base, Object property, Object value) {
		if (base instanceof MapAdaptable) {
			super.setValue(context, adapt(base), property, value);
		}
	}

	private Map adapt(Object base) {
		MapAdaptable adaptable = (MapAdaptable) base;
		return adaptable.asMap();
	}

}
