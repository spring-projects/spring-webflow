package org.springframework.js.ajax;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

public class AjaxRedirectView extends RedirectView implements View {

	private AjaxHandler ajaxHandler = new SpringJavascriptAjaxHandler();

	public AjaxRedirectView(String redirectUrl, boolean redirectContextRelative, boolean redirectHttp10Compatible) {
		super(redirectUrl, redirectContextRelative, redirectHttp10Compatible);
	}

	protected void sendRedirect(HttpServletRequest request, HttpServletResponse response, String targetUrl,
			boolean http10Compatible) throws IOException {
		ServletContext context = RequestContextUtils.getWebApplicationContext(request).getServletContext();
		if (ajaxHandler.isAjaxRequest(context, request, response)) {
			ajaxHandler.sendAjaxRedirect(context, request, response, targetUrl, false);
		} else {
			super.sendRedirect(request, response, targetUrl, http10Compatible);
		}
	}

}
