package org.springframework.webflow.samples.gettingstarted;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.RequestToViewNameTranslator;

/**
 * Handles requests for the application welcome page.
 */
@Controller
public class WelcomeController {

	/**
	 * Simply selects the welcome view to render by returning void and relying
	 * on the default {@link RequestToViewNameTranslator}.
	 */
	@RequestMapping(method = RequestMethod.GET)
	public void welcome() {
	}
}
