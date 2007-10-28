package org.springframework.webflow.mvc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.binding.expression.Expression;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.webflow.engine.builder.ViewFactoryCreator;
import org.springframework.webflow.engine.builder.support.ActionInvokingViewFactory;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.View;
import org.springframework.webflow.execution.ViewFactory;

public class MvcViewFactoryCreator implements ViewFactoryCreator {

	public Action createFinalResponseAction(Expression viewId, ResourceLoader viewResourceLoader) {
		return new TestAction();
	}

	public ViewFactory createViewFactory(Expression viewId, ResourceLoader viewResourceLoader) {
		return new ActionInvokingViewFactory(new TestAction());
	}

	public class TestAction implements Action {
		public Event execute(RequestContext context) throws Exception {
			System.out.println("Render me...");
			return new Event(this, "success");
		}
	}

	private static class MvcViewFactory implements ViewFactory {
		private ResourceLoader viewResourceLoader;

		private Expression viewExpression;

		private List viewResolvers;

		private boolean internalResourceResolverOnly;

		public View getView(RequestContext context) {
			String view = (String) viewExpression.getValue(context);
			return new MvcView(resolveView(view), context);
		}

		protected org.springframework.web.servlet.View resolveView(String viewName) {
			if (internalResourceResolverOnly && !viewName.startsWith("/")) {
				viewName = ((ContextResource) viewResourceLoader.getResource(viewName)).getPathWithinContext();
			}
			for (Iterator it = viewResolvers.iterator(); it.hasNext();) {
				ViewResolver viewResolver = (ViewResolver) it.next();
				try {
					org.springframework.web.servlet.View view = viewResolver.resolveViewName(viewName,
							LocaleContextHolder.getLocale());
					if (view != null) {
						return view;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
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
				model.putAll(context.getConversationScope().union(context.getFlowScope())
						.union(context.getFlashScope()).union(context.getRequestScope()).asMap());
				try {
					view.render(model, (HttpServletRequest) context.getExternalContext().getRequest(),
							(HttpServletResponse) context.getExternalContext().getResponse());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
