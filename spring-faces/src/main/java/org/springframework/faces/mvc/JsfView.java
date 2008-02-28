package org.springframework.faces.mvc;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.event.PhaseId;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.faces.webflow.JsfUtils;
import org.springframework.util.Assert;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

/**
 * JSF View that renders a transient (stateless) JSF view template. The UIViewRoot will not be saved and thus the JSF
 * lifecycle will not be able to be invoked on postback.
 * 
 * @author Jeremy Grelle
 */
public class JsfView extends AbstractUrlBasedView {

	private Lifecycle facesLifecycle;

	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		facesLifecycle = createFacesLifecycle();
	}

	protected void renderMergedOutputModel(Map model, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		FacesContext facesContext = createFacesContext(request, response);

		populateRequestMap(facesContext, model);

		JsfUtils.notifyBeforeListeners(PhaseId.RESTORE_VIEW, facesLifecycle, facesContext);

		UIViewRoot viewRoot = facesContext.getApplication().getViewHandler().createView(facesContext, getUrl());

		Assert.notNull(viewRoot, "A JSF view could not be created for " + getUrl());
		viewRoot.setTransient(true);
		facesContext.setViewRoot(viewRoot);

		JsfUtils.notifyAfterListeners(PhaseId.RESTORE_VIEW, facesLifecycle, facesContext);

		facesContext.setViewRoot(viewRoot);
		facesContext.renderResponse();
		try {
			JsfUtils.notifyBeforeListeners(PhaseId.RENDER_RESPONSE, facesLifecycle, facesContext);
			logger.debug("Asking view handler to render view");
			facesContext.getApplication().getViewHandler().renderView(facesContext, viewRoot);
			JsfUtils.notifyAfterListeners(PhaseId.RENDER_RESPONSE, facesLifecycle, facesContext);
		} catch (IOException e) {
			throw new FacesException("An I/O error occurred during view rendering", e);
		} finally {
			logger.debug("View rendering complete");
			facesContext.responseComplete();
			facesContext.release();
		}
	}

	private void populateRequestMap(FacesContext facesContext, Map model) {
		Iterator i = model.keySet().iterator();
		while (i.hasNext()) {
			Object key = i.next();
			facesContext.getExternalContext().getRequestMap().put(key, model.get(key));
		}
	}

	private FacesContext createFacesContext(HttpServletRequest request, HttpServletResponse response) {
		FacesContextFactory facesContextFactory = (FacesContextFactory) FactoryFinder
				.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
		return facesContextFactory.getFacesContext(getServletContext(), request, response, facesLifecycle);
	}

	private Lifecycle createFacesLifecycle() {
		LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
				.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
		return lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
	}

}
