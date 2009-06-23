/*
 * Copyright 2004-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.js.ajax;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

/**
 * View resolver that provides special view resolution for Spring Javascript Ajax requests.
 * 
 * @author Jeremy Grelle
 * 
 */
public class AjaxUrlBasedViewResolver extends UrlBasedViewResolver {

	/**
	 * Overridden to implement check for "redirect:" prefix.
	 * <p>
	 * Redirect requires special behavior on an Ajax request.
	 */
	protected View createView(String viewName, Locale locale) throws Exception {
		// If this resolver is not supposed to handle the given view,
		// return null to pass on to the next resolver in the chain.
		if (!canHandle(viewName, locale)) {
			return null;
		}
		// Check for special "redirect:" prefix.
		if (viewName.startsWith(REDIRECT_URL_PREFIX)) {
			String redirectUrl = viewName.substring(REDIRECT_URL_PREFIX.length());
			return new AjaxRedirectView(redirectUrl, isRedirectContextRelative(), isRedirectHttp10Compatible());
		}
		return super.createView(viewName, locale);
	}

	private class AjaxRedirectView extends RedirectView implements View {

		private AjaxHandler ajaxHandler = new SpringJavascriptAjaxHandler();

		public AjaxRedirectView(String redirectUrl, boolean redirectContextRelative, boolean redirectHttp10Compatible) {
			super(redirectUrl, redirectContextRelative, redirectHttp10Compatible);
		}

		protected void sendRedirect(HttpServletRequest request, HttpServletResponse response, String targetUrl,
				boolean http10Compatible) throws IOException {
			if (ajaxHandler.isAjaxRequest(request, response)) {
				ajaxHandler.sendAjaxRedirect(targetUrl, request, response, false);
			} else {
				super.sendRedirect(request, response, targetUrl, http10Compatible);
			}
		}

	}
}
