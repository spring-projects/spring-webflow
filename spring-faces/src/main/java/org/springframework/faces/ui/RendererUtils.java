package org.springframework.faces.ui;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

public class RendererUtils {

	public static String getFormId(FacesContext context, UIComponent component) {
		if (component.getParent() instanceof UIForm) {
			return component.getParent().getClientId(context);
		} else if (component.getParent() instanceof UIViewRoot) {
			throw new FacesException("Could not render " + component.getClass().getName() + " component with id "
					+ component.getId() + " - no enclosing UIForm was found.");
		} else {
			return getFormId(context, component.getParent());
		}
	}
}
