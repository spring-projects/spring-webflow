package org.springframework.binding.expression.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;

/**
 * An {@link ELContext} implementation that is meant to aggregate the {@link ELResolver}s of 
 * pre-existing {@link ELContext}s.  Can also be used standalone if no other {@link ELContext} 
 * exists in the current environment.  
 * 
 * Note - Using this context standalone requires Java 5 or higher.
 * 
 * @author Jeremy Grelle
 *
 */
public class DelegatingELContext extends ELContext {

	private ELResolver resolver;

	private FunctionMapper functionMapper;

	private VariableMapper variableMapper;
	
	private List delegates = new ArrayList();

	public ELResolver getELResolver() {
		return resolver;
	}

	public void setELResolver(ELResolver resolver) {
		this.resolver = resolver;
	}

	public FunctionMapper getFunctionMapper() {
		return functionMapper;
	}

	public void setFunctionMapper(FunctionMapper functionMapper) {
		this.functionMapper = functionMapper;
	}

	public VariableMapper getVariableMapper() {
		return variableMapper;
	}

	public void setVariableMapper(VariableMapper variableMapper) {
		this.variableMapper = variableMapper;
	}

	/**
	 * Add a delegate {@link ELContext}.
	 * 
	 * If this context is currently configured with the {@link DefaultELResolver}, that resolver
	 * instance will be replaced with a new {@link CompositeELResolver}.
	 * 
	 * The delegate's base {@link ELResolver} will be added to this context's base {@link CompositeELResolver}.
	 * 
	 * @param context The {@link ELContext} whose base {@link ELResolver} needs to be included in this
	 * context's base {@link CompositeELResolver}.
	 */
	public void addDelegate(ELContext context) {
		delegates.add(context);
		if (getELResolver() == null || getELResolver() instanceof DefaultELResolver) {
			CompositeELResolver composite = new CompositeELResolver();
			composite.add(context.getELResolver());
			setELResolver(composite);
		}
		else if (getELResolver() instanceof CompositeELResolver) {
			((CompositeELResolver) getELResolver()).add(context.getELResolver());
		}
	}

	public Object getContext(Class key) {
		Object context = super.getContext(key);
		if (context != null)
			return context;
		
		Iterator i = delegates.iterator();
		while(i.hasNext())
		{
			ELContext delegate = (ELContext) i.next();
			context = delegate.getContext(key);
			if (context != null)
				return context;
		}
		return null;
	}

	/**
	 * The <code>ThreadLocal</code> variable used to record the
	 * {@link ELContext} instance for each processing thread.
	 */
	private static ThreadLocal instance = new ThreadLocal() {
		protected Object initialValue() {
			return null;
		}
	};

	/**
	 * Return the {@link ELContext} instance for the request that is
	 * being processed by the current thread, if any.
	 */
	public static DelegatingELContext getCurrentInstance() {
		if (instance.get() == null)
			setCurrentInstance(defaultInstance());
		return ((DelegatingELContext) instance.get());
	}

	/**
	 * Set the {@link ELContext} instance for the request that is
	 * being processed by the current thread.
	 *
	 * @param context The {@link ELContext} instance for the current
	 * thread, or <code>null</code> if this thread no longer has an
	 * <code>ELContext</code> instance.
	 *
	 */
	public static void setCurrentInstance(DelegatingELContext context) {
		instance.set(context);
	}

	private static DelegatingELContext defaultInstance() {
		return new DelegatingELContext();
	}
}
