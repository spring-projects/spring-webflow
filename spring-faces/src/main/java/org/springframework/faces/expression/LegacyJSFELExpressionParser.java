package org.springframework.faces.expression;

import java.util.ArrayList;
import java.util.List;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;

import org.springframework.binding.expression.el.DefaultELResolver;
import org.springframework.binding.expression.el.ELContextFactory;
import org.springframework.binding.expression.el.ELExpressionParser;
import org.springframework.util.ClassUtils;
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

	public LegacyJSFELExpressionParser(ExpressionFactory expressionFactory) {
		super(expressionFactory);
		putContextFactory(RequestContext.class, new RequestContextELContextFactory());
		putContextFactory(MutableAttributeMap.class, new AttributeMapELContextFactory());
	}

	public LegacyJSFELExpressionParser() {
		this(getDefaultExpressionFactory());
	}

	private static ExpressionFactory getDefaultExpressionFactory() {
		if (!System.getProperties().containsKey("javax.el.ExpressionFactory")) {
			System.setProperty("javax.el.ExpressionFactory", "org.jboss.el.ExpressionFactoryImpl");
		}
		return ExpressionFactory.newInstance();
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
