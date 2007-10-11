package org.springframework.faces.webflow;

import java.util.Iterator;

import javax.faces.context.FacesContext;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;

public class FlowRenderKitFactory extends RenderKitFactory {

	private RenderKitFactory delegate;

	public FlowRenderKitFactory(RenderKitFactory renderKitFactory) {
		this.delegate = renderKitFactory;
	}

	public void addRenderKit(String renderKitId, RenderKit renderKit) {
		FlowRenderKitWrapper wrapper = new FlowRenderKitWrapper(renderKit);
		delegate.addRenderKit(renderKitId, wrapper);
	}

	public RenderKit getRenderKit(FacesContext context, String renderKitId) {
		RenderKit renderKit = delegate.getRenderKit(context, renderKitId);
		if (!(renderKit instanceof FlowRenderKitWrapper)) {
			return new FlowRenderKitWrapper(renderKit);
		}
		return renderKit;
	}

	public Iterator getRenderKitIds() {
		return delegate.getRenderKitIds();
	}

}
