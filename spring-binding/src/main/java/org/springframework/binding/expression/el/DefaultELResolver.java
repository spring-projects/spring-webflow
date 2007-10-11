package org.springframework.binding.expression.el;

import java.util.Iterator;
import java.util.List;

import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ResourceBundleELResolver;

/**
 * A generic ELResolver to be used as a default when no other ELResolvers have been configured by the client
 * application.
 * 
 * This implementation will resolve the first part of the expression to the pre-configured base object, and will then
 * delegate through the chain of standard resolvers for the rest of the expression.
 * 
 * Note - Requires Java 5 or higher due to the use of generics in the API's basic resolvers.
 * 
 * @author Jeremy Grelle
 */
public class DefaultELResolver extends CompositeELResolver {

	private Object target;

	/**
	 * Creates a new default EL resolver for resolving properties of the root object.
	 * @param target the target, or "root", object of the expression
	 */
	public DefaultELResolver(Object target, List customResolvers) {
		this.target = target;
		configureResolvers(customResolvers);
	}

	public Object getTarget() {
		return target;
	}

	public Class getType(ELContext context, Object base, Object property) {
		return super.getType(context, base, property);
	}

	public Object getValue(ELContext context, Object base, Object property) {
		if (base == null) {
			return super.getValue(context, target, property);
		} else {
			return super.getValue(context, base, property);
		}
	}

	public void setValue(ELContext context, Object base, Object property, Object val) {
		if (base == null) {
			super.setValue(context, target, property, val);
		} else {
			super.setValue(context, base, property, val);
		}
	}

	private void configureResolvers(List customResolvers) {
		if (customResolvers != null) {
			Iterator i = customResolvers.iterator();
			while (i.hasNext()) {
				ELResolver resolver = (ELResolver) i.next();
				add(resolver);
			}
		}
		add(new MapAdaptableELResolver());
		add(new ArrayELResolver());
		add(new ListELResolver());
		add(new MapELResolver());
		add(new ResourceBundleELResolver());
		add(new BeanELResolver());
	}

}