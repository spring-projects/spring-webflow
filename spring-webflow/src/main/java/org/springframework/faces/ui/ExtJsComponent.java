package org.springframework.faces.ui;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

public class ExtJsComponent extends UIComponentBase {

    /**
     * The component will render the default ExtJs css resources by default. This may be set to false if the page
     * developer wants to include their own stylesheet.
     */
    private Boolean includeExtStyles = new Boolean(true);

    /**
     * The component will render an optimized version of the ExtJs javascript that contains only the pieces of the
     * library used by SpringFaces. This may be set to false if the page developer wants to include their own ExtJs
     * resources.
     */
    private Boolean includeExtScript = new Boolean(true);

    public String getFamily() {

	return "spring.faces.ExtAdvisor";
    }

    public Boolean getIncludeExtStyles() {
	return includeExtStyles;
    }

    public void setIncludeExtStyles(Boolean includeExtStyles) {
	this.includeExtStyles = includeExtStyles;
    }

    public Boolean getIncludeExtScript() {
	return includeExtScript;
    }

    public void setIncludeExtScript(Boolean includeExtScript) {
	this.includeExtScript = includeExtScript;
    }

    public Object saveState(FacesContext context) {
	Object[] values = new Object[3];
	values[0] = super.saveState(context);
	values[1] = includeExtScript;
	values[2] = includeExtStyles;
	return values;
    }

    public void restoreState(FacesContext context, Object state) {
	Object values[] = (Object[]) state;
	super.restoreState(context, values[0]);
	includeExtScript = (Boolean) values[1];
	includeExtStyles = (Boolean) values[2];
    }

}
