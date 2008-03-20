package org.springframework.webflow.mvc.view;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.format.FormatterRegistry;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.View;
import org.springframework.webflow.execution.ViewFactory;

/**
 * View factory implementation that delegates to the Spring-configured view resolver chain to resolve the Spring MVC
 * view implementation to render.
 * @author Keith Donald
 */
class ViewResolvingMvcViewFactory implements ViewFactory {

	private Expression viewIdExpression;

	private ExpressionParser expressionParser;

	private FormatterRegistry formatterRegistry;

	private List viewResolvers;

	public ViewResolvingMvcViewFactory(Expression viewIdExpression, ExpressionParser expressionParser,
			FormatterRegistry formatterRegistry, List viewResolvers) {
		this.viewIdExpression = viewIdExpression;
		this.viewResolvers = viewResolvers;
	}

	public View getView(RequestContext context) {
		String viewName = (String) viewIdExpression.getValue(context);
		MvcView view = new MvcView(resolveView(viewName), context);
		view.setExpressionParser(expressionParser);
		view.setFormatterRegistry(formatterRegistry);
		return view;
	}

	protected org.springframework.web.servlet.View resolveView(String viewName) {
		for (Iterator it = viewResolvers.iterator(); it.hasNext();) {
			ViewResolver viewResolver = (ViewResolver) it.next();
			try {
				Locale locale = LocaleContextHolder.getLocale();
				org.springframework.web.servlet.View view = viewResolver.resolveViewName(viewName, locale);
				if (view != null) {
					return view;
				}
			} catch (Exception e) {
				throw new IllegalStateException("Exception resolving view with name '" + viewName + "'", e);
			}
		}
		return null;
	}
}