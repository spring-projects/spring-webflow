package org.springframework.faces.mvc.richfaces;

import java.io.IOException;

import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajax4jsf.context.AjaxContext;
import org.springframework.faces.webflow.FlowLifecycle;
import org.springframework.webflow.mvc.SpringJavascriptAjaxHandler;

public class RichFacesAjaxHandler extends SpringJavascriptAjaxHandler {

	public boolean isAjaxRequest(ServletContext context, HttpServletRequest request, HttpServletResponse response) {
		FacesContextHelper helper = new FacesContextHelper();
		if (AjaxContext.getCurrentInstance(helper.getFacesContext(context, request, response)).isAjaxRequest(
				helper.getFacesContext(context, request, response))) {
			helper.cleanup();
			return true;
		} else {
			helper.cleanup();
			return super.isAjaxRequest(context, request, response);
		}
	}

	public void sendAjaxRedirect(ServletContext context, HttpServletRequest request, HttpServletResponse response,
			String targetUrl, boolean popup) throws IOException {
		FacesContextHelper helper = new FacesContextHelper();
		if (AjaxContext.getCurrentInstance(helper.getFacesContext(context, request, response)).isAjaxRequest(
				helper.getFacesContext(context, request, response))) {
			helper.cleanup();
			response.sendRedirect(response.encodeRedirectURL(targetUrl));
		} else {
			helper.cleanup();
			super.sendAjaxRedirect(context, request, response, targetUrl, popup);
		}
	}

	private static class FacesContextHelper {

		private boolean created = false;

		protected FacesContext getFacesContext(ServletContext context, HttpServletRequest request,
				HttpServletResponse response) {
			if (FacesContext.getCurrentInstance() != null) {
				return FacesContext.getCurrentInstance();
			} else {
				created = true;
				FacesContextFactory facesContextFactory = (FacesContextFactory) FactoryFinder
						.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
				FacesContext defaultFacesContext = facesContextFactory.getFacesContext(context, request, response,
						FlowLifecycle.newInstance());
				return defaultFacesContext;
			}
		}

		protected void cleanup() {
			if (created) {
				FacesContext.getCurrentInstance().release();
			}
		}
	}
}
