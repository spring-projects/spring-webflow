package org.springframework.webflow.mvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.binding.expression.Expression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.OrderComparator;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.webflow.engine.builder.ViewFactoryCreator;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.View;
import org.springframework.webflow.execution.ViewFactory;

/**
 * View factory creator implementation that produces View Factories that create Spring MVC-based views.
 * 
 * This class is used by a flow builder in a Spring MVC environment to configure view factories on flows that render
 * Spring MVC-based views.
 * 
 * This class supports rendering views resolved by existing Spring MVC-based resolver infrastructure, or, if no such
 * infrastructure is configured, JSP resources relative to the flow definition being built.
 * 
 * @author Keith Donald
 */
public class MvcViewFactoryCreator implements ViewFactoryCreator, ApplicationContextAware {

	private static final boolean jstlPresent = ClassUtils.isPresent("javax.servlet.jsp.jstl.fmt.LocalizationContext");

	private List viewResolvers;

	private ApplicationContext applicationContext;

	public Action createFinalResponseAction(Expression viewId, ResourceLoader viewResourceLoader) {
		return new ViewFactoryActionAdapter(createViewFactory(viewId, viewResourceLoader));
	}

	public ViewFactory createViewFactory(Expression viewId, ResourceLoader viewResourceLoader) {
		if (viewResolvers != null) {
			return new ViewResolvingMvcViewFactory(viewId, viewResolvers);
		} else {
			return new InternalFlowResourceMvcViewFactory(viewId, applicationContext, viewResourceLoader);
		}
	}

	public void setApplicationContext(ApplicationContext context) {
		initViewResolvers(context);
		this.applicationContext = context;
	}

	private void initViewResolvers(ApplicationContext context) {
		Map matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(context, ViewResolver.class, true, false);
		if (!matchingBeans.isEmpty()) {
			viewResolvers = new ArrayList(matchingBeans.values());
			Collections.sort(viewResolvers, new OrderComparator());
		}
	}

	/**
	 * View factory implementation that creates a Spring-MVC Internal Resource view to render a flow-relative view
	 * resource such as a JSP or Velocity template.
	 * @author Keith Donald
	 */
	static class InternalFlowResourceMvcViewFactory implements ViewFactory {
		private Expression viewExpression;

		private ApplicationContext applicationContext;

		private ResourceLoader resourceLoader;

		public InternalFlowResourceMvcViewFactory(Expression viewExpression, ApplicationContext context,
				ResourceLoader resourceLoader) {
			this.viewExpression = viewExpression;
			this.applicationContext = context;
			this.resourceLoader = resourceLoader;
		}

		public View getView(RequestContext context) {
			String viewId = (String) viewExpression.getValue(context);
			if (viewId.startsWith("/")) {
				return getViewInternal(viewId, context);
			} else {
				ContextResource viewResource = (ContextResource) resourceLoader.getResource(viewId);
				return getViewInternal(viewResource.getPathWithinContext(), context);
			}
		}

		private View getViewInternal(String viewPath, RequestContext context) {
			if (viewPath.endsWith(".jsp")) {
				if (jstlPresent) {
					JstlView view = new JstlView(viewPath);
					view.setApplicationContext(applicationContext);
					return new MvcView(view, context);
				} else {
					InternalResourceView view = new InternalResourceView(viewPath);
					view.setApplicationContext(applicationContext);
					return new MvcView(view, context);
				}
			} else {
				throw new IllegalArgumentException("Unsupported view type " + viewPath
						+ " only types supported are [.jsp]");
			}
		}
	}

	/**
	 * View factory implementation that delegates to the Spring-configured view resolver chain to resolve the Spring MVC
	 * view implementation to render.
	 * @author Keith Donald
	 */
	private static class ViewResolvingMvcViewFactory implements ViewFactory {
		private Expression viewId;

		private List viewResolvers;

		public ViewResolvingMvcViewFactory(Expression viewId, List viewResolvers) {
			this.viewId = viewId;
			this.viewResolvers = viewResolvers;
		}

		public View getView(RequestContext context) {
			String view = (String) viewId.getValue(context);
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

	private static class MvcView implements View {

		private RequestContext context;

		private org.springframework.web.servlet.View view;

		public MvcView(org.springframework.web.servlet.View view, RequestContext context) {
			this.view = view;
			this.context = context;
		}

		public boolean eventSignaled() {
			return context.getRequestParameters().contains("_eventId");
		}

		public Event getEvent() {
			return new Event(view, context.getRequestParameters().get("_eventId"), context.getRequestParameters()
					.asAttributeMap());
		}

		public void render() {
			Map model = new HashMap();
			model.putAll(context.getConversationScope().union(context.getFlowScope()).union(context.getFlashScope())
					.union(context.getRequestScope()).asMap());
			try {
				view.render(model, (HttpServletRequest) context.getExternalContext().getRequest(),
						(HttpServletResponse) context.getExternalContext().getResponse());
			} catch (Exception e) {
				throw new IllegalStateException("Exception rendering view", e);
			}
		}
	}

	/**
	 * Simple adapter that adapts a view factory render cycle to the action interface.
	 * @author Keith Donald
	 */
	private static class ViewFactoryActionAdapter implements Action {
		private ViewFactory viewFactory;

		public ViewFactoryActionAdapter(ViewFactory viewFactory) {
			this.viewFactory = viewFactory;
		}

		public Event execute(RequestContext context) throws Exception {
			viewFactory.getView(context).render();
			return new Event(this, "success");
		}
	}

}