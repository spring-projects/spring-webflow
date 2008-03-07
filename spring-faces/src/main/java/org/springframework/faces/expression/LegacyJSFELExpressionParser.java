package org.springframework.faces.expression;

import java.util.ArrayList;
import java.util.List;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;

import org.springframework.beans.BeanUtils;
import org.springframework.binding.expression.el.DefaultELResolver;
import org.springframework.binding.expression.el.ELContextFactory;
import org.springframework.binding.expression.el.ELExpressionParser;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.expression.el.ActionMethodELResolver;
import org.springframework.webflow.expression.el.ImplicitFlowVariableELResolver;
import org.springframework.webflow.expression.el.RequestContextELResolver;
import org.springframework.webflow.expression.el.ScopeSearchingELResolver;
import org.springframework.webflow.expression.el.SpringBeanWebFlowELResolver;
import org.springframework.webflow.expression.el.SpringSecurityELResolver;

/**
 * A JSF-specific ExpressionParser that allows beans managed by either JSF, Spring, or Web Flow referenced in
 * expressions in the FlowDefinition.
 * 
 * @author Jeremy Grelle
 */
public class LegacyJSFELExpressionParser extends ELExpressionParser {

	private static final String EXPRESSION_FACTORY_PROPERTY = "javax.el.ExpressionFactory";

	private static final String DEFAULT_EXPRESSION_FACTORY = "org.jboss.el.ExpressionFactoryImpl";

	public LegacyJSFELExpressionParser(ExpressionFactory expressionFactory) {
		super(expressionFactory);
		putContextFactory(RequestContext.class, new RequestContextELContextFactory());
		putContextFactory(MutableAttributeMap.class, new AttributeMapELContextFactory());
	}

	public LegacyJSFELExpressionParser() {
		this(getDefaultExpressionFactory());
	}

	private static ExpressionFactory getDefaultExpressionFactory() {
		if (!System.getProperties().containsKey(EXPRESSION_FACTORY_PROPERTY)) {
			System.setProperty(EXPRESSION_FACTORY_PROPERTY, DEFAULT_EXPRESSION_FACTORY);
		}
		if (ReflectionUtils.findMethod(ExpressionFactory.class, "newInstance") != null) {
			return ExpressionFactory.newInstance();
		} else { // Fallback in case using an older version of el-api
			try {
				return (ExpressionFactory) BeanUtils.instantiateClass(Class.forName(DEFAULT_EXPRESSION_FACTORY));
			} catch (Exception e) {
				throw new ELException("Could not create the default ExpressionFactory", e);
			}
		}
	}

	private static class RequestContextELContextFactory implements ELContextFactory {
		public ELContext getELContext(Object target) {
			List customResolvers = new ArrayList();
			customResolvers.add(new RequestContextELResolver());
			if (ClassUtils.isPresent("org.springframework.security.context.SecurityContextHolder")) {
				customResolvers.add(new SpringSecurityELResolver());
			}
			customResolvers.add(new ImplicitFlowVariableELResolver());
			customResolvers.add(new SpringBeanWebFlowELResolver());
			customResolvers.add(new ActionMethodELResolver());
			customResolvers.add(new ScopeSearchingELResolver());
			customResolvers.add(new LegacyJSFBeanResolver());
			ELResolver resolver = new DefaultELResolver(target, customResolvers);
			return new WebFlowELContext(resolver);
		}
	}

	private static class AttributeMapELContextFactory implements ELContextFactory {
		public ELContext getELContext(Object target) {
			ELResolver resolver = new DefaultELResolver(target, null);
			return new WebFlowELContext(resolver);
		}
	}

	private static class WebFlowELContext extends ELContext {

		private ELResolver resolver;

		public WebFlowELContext(ELResolver resolver) {
			this.resolver = resolver;
		}

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

}
