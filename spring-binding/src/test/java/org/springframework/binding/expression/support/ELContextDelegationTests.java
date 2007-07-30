package org.springframework.binding.expression.support;

import java.util.Iterator;

import javax.el.BeanELResolver;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;

import junit.framework.TestCase;

import org.springframework.binding.expression.ExpressionParser;

/**
 * Tests to verify the delegation behavior of ELContextImpl.
 * 
 * @author Jeremy Grelle
 *
 */
public class ELContextDelegationTests extends TestCase {

	private ELContext context = new MockELContext();
	private ExpressionParser expressionParser  = new ELExpressionParser();
	private TestBean bean;
	
	protected void setUp() throws Exception {
		bean = new TestBean();
		bean.setFlag(true);
	}
	
	public final void testGetValueByContextDelegation()
	{
		DelegatingELContext internalContext = DelegatingELContext.getCurrentInstance();
		internalContext.addDelegate(context);
		assertEquals(Boolean.TRUE, expressionParser.parseExpression("#{bean.flag}").evaluate(bean, null));
		assertEquals(Boolean.TRUE, expressionParser.parseExpression("#{bean.flag}").evaluate(null, null));
		assertFalse(internalContext.getELResolver() instanceof DefaultELResolver);
	}

	private class MockELContext extends ELContext
	{

		ELResolver resolver = new MockELResolver();
		
		public ELResolver getELResolver() {
			return resolver;
		}

		public FunctionMapper getFunctionMapper() {
			return null;
		}

		public VariableMapper getVariableMapper() {
			return null;
		}
		
	}
	
	private class MockELResolver extends ELResolver
	{
		
		public Class getCommonPropertyType(ELContext context, Object base) {
			return null;
		}

		public Iterator getFeatureDescriptors(ELContext context, Object base) {
			return null;
		}

		public Class getType(ELContext context, Object base, Object property) {
			if (base != null)
				return base.getClass();
			return null;
		}

		public Object getValue(ELContext context, Object base, Object property) {
			if (base == null && "bean".equals(property))
			{
				context.setPropertyResolved(true);
				return bean;
			}
			if (base == bean)
			{
				return new BeanELResolver().getValue(context, base, property);
			}
			return null;
		}

		public boolean isReadOnly(ELContext context, Object base, Object property) {
			return false;
		}

		public void setValue(ELContext context, Object base, Object property, Object value) {
			if (base == null && "bean".equals(property))
			{
				context.setPropertyResolved(true);
			}
			else if (base == bean)
			{
				new BeanELResolver().setValue(context, base, property, value);
			}			
		}
		
	}
}
