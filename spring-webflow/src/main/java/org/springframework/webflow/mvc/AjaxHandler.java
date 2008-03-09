package org.springframework.webflow.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Strategy interface that encapsulates knowledge about a client-side ajax system and how to communicate with that
 * system.
 * @author Keith Donald
 */
public interface AjaxHandler {

	/**
	 * Is the request from the client an Ajax request?
	 * @param request the current request
	 */
	public boolean isAjaxRequest(HttpServletRequest request);

	/**
	 * Send a redirect request to the Ajax client. This should cause the client to send a new request to the specified
	 * target url.
	 * @param response the response object
	 * @param targetUrl the target url to redirect to
	 * @param popup wheter the redirect should be sent from a new popup dialog window
	 */
	public void sendAjaxRedirect(HttpServletResponse response, String targetUrl, boolean popup);
}
