package org.springframework.webflow.mvc.view;

import java.util.Iterator;
import java.util.List;

import org.springframework.binding.expression.Expression;
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

	private List viewResolvers;

	public ViewResolvingMvcViewFactory(Expression viewIdExpression, List viewResolvers) {
		this.viewIdExpression = viewIdExpression;
		this.viewResolvers = viewResolvers;
	}

	public View getView(RequestContext context) {
		String view = (String) viewIdExpression.getValue(context);
		return new MvcView(resolveView(view), context);
	}

	protected org.springframework.web.servlet.View resolveView(String viewName) {
		for (Iterator it = viewResolvers.iterator(); it.hasNext();) {
			ViewResolver viewResolver = (ViewResolver) it.next();
			try {
				org.springframework.web.servlet.View view = viewResolver.resolveViewName(viewName,
						LocaleContextHolder.getLocale());
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