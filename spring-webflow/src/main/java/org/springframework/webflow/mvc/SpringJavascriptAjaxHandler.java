package org.springframework.webflow.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;

/**
 * Ajax handler that works with Spring Javascript (Spring.js).
 * 
 * @author Jeremy Grelle
 * @author Keith Donald
 */
public class SpringJavascriptAjaxHandler implements AjaxHandler {

	/**
	 * The response header to be set on an Ajax redirect
	 */
	private static final String FLOW_REDIRECT_URL_HEADER = "Flow-Redirect-URL";

	/**
	 * The response header to be set on an redirect that should be issued from a popup window.
	 */
	private static final String POPUP_VIEW_HEADER = "Flow-Modal-View";

	/**
	 * The accept header value that signifies an Ajax request.
	 */
	private static final String AJAX_ACCEPT_CONTENT_TYPE = "text/html;type=ajax";

	/**
	 * Alternate request parameter to indicate an Ajax request for cases when control of the header is not available.
	 */
	private static final String AJAX_SOURCE_PARAM = "ajaxSource";

	public boolean isAjaxRequest(HttpServletRequest request) {
		String acceptHeader = request.getHeader("Accept");
		String ajaxParam = request.getParameter(AJAX_SOURCE_PARAM);
		if (AJAX_ACCEPT_CONTENT_TYPE.equals(acceptHeader) || StringUtils.hasText(ajaxParam)) {
			return true;
		} else {
			return false;
		}
	}

	public void sendAjaxRedirect(HttpServletResponse response, String targetUrl, boolean popup) {
		if (popup) {
			response.setHeader(POPUP_VIEW_HEADER, "true");
		}
		response.setHeader(FLOW_REDIRECT_URL_HEADER, response.encodeRedirectURL(targetUrl));
	}

}
