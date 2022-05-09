package org.springframework.webflow.context.servlet;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.context.support.WebApplicationObjectSupport;

public abstract class AbstractAjaxHandler extends WebApplicationObjectSupport implements AjaxHandler {

	private AbstractAjaxHandler delegate;

	public AbstractAjaxHandler(AbstractAjaxHandler delegate) {
		this.delegate = delegate;
	}

	public final boolean isAjaxRequest(HttpServletRequest request, HttpServletResponse response) {
		if (isAjaxRequestInternal(request, response)) {
			return true;
		}
		if (delegate != null) {
			return delegate.isAjaxRequest(request, response);
		}
		return false;
	}

	public final void sendAjaxRedirect(String targetUrl, HttpServletRequest request, HttpServletResponse response,
			boolean popup) throws IOException {
		if (isAjaxRequestInternal(request, response)) {
			sendAjaxRedirectInternal(targetUrl, request, response, popup);
		}
		if (delegate != null) {
			delegate.sendAjaxRedirect(targetUrl, request, response, popup);
		}
	}

	protected abstract boolean isAjaxRequestInternal(HttpServletRequest request, HttpServletResponse response);

	protected abstract void sendAjaxRedirectInternal(String targetUrl, HttpServletRequest request,
			HttpServletResponse response, boolean popup) throws IOException;

}
