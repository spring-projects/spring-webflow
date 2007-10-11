package org.springframework.faces.webflow;

import java.io.OutputStream;
import java.io.Writer;

import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.faces.render.Renderer;
import javax.faces.render.ResponseStateManager;

public class FlowRenderKitWrapper extends RenderKit {

	RenderKit delegate;

	ResponseStateManager manager = new FlowResponseStateManager();

	public FlowRenderKitWrapper(RenderKit renderKit) {
		this.delegate = renderKit;
	}

	public void addRenderer(String family, String rendererType, Renderer renderer) {
		delegate.addRenderer(family, rendererType, renderer);
	}

	public ResponseStream createResponseStream(OutputStream out) {
		return delegate.createResponseStream(out);
	}

	public ResponseWriter createResponseWriter(Writer writer, String contentTypeList, String characterEncoding) {
		return delegate.createResponseWriter(writer, contentTypeList, characterEncoding);
	}

	public Renderer getRenderer(String family, String rendererType) {
		return delegate.getRenderer(family, rendererType);
	}

	public ResponseStateManager getResponseStateManager() {
		return manager;
	}

}
