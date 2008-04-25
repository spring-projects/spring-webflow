package org.springframework.js.ajax;

import java.util.Locale;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

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
}
