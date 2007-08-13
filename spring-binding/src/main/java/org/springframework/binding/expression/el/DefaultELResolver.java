package org.springframework.binding.expression.el;

import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ResourceBundleELResolver;

import org.springframework.binding.collection.MapAdaptable;
import org.springframework.util.Assert;

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

	public DefaultELResolver() {
		configureResolvers();
	}

	public Class getType(ELContext context, Object base, Object property) {
		return super.getType(context, adaptIfNecessary(base), property);
	}

	public Object getValue(ELContext context, Object base, Object property) {
		Assert.notNull(target, "The DefaultELResolver must have a target base property set.");
		if (base == null) {
			return super.getValue(context, target, property);
		} else {
			return super.getValue(context, adaptIfNecessary(base), property);
		}
	}

	public void setValue(ELContext context, Object base, Object property, Object val) {
		Assert.notNull(target, "The DefaultELResolver must have a target base property set.");
		if (base == null) {
			super.setValue(context, target, property, val);
		} else {
			super.setValue(context, adaptIfNecessary(base), property, val);
		}
	}

	public Object getTarget() {
		return target;
	}

	public void setTarget(Object target) {
		this.target = adaptIfNecessary(target);
	}

	private Object adaptIfNecessary(Object base) {
		if (base instanceof MapAdaptable) {
			return ((MapAdaptable) base).asMap();
		} else {
			return base;
		}
	}

	private void configureResolvers() {
		add(new ArrayELResolver());
		add(new ListELResolver());
		add(new MapELResolver());
		add(new ResourceBundleELResolver());
		add(new BeanELResolver());
	}

}
