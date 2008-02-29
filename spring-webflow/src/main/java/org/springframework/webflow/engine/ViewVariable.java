package org.springframework.webflow.engine;

import org.springframework.webflow.execution.RequestContext;

public class ViewVariable extends AnnotatedObject {
	private String name;

	private VariableValueFactory valueFactory;

	public ViewVariable(String name, VariableValueFactory valueFactory) {
		this.name = name;
		this.valueFactory = valueFactory;
	}

	public String getName() {
		return name;
	}

	public VariableValueFactory getValueFactory() {
		return valueFactory;
	}

	// name and scope based equality

	public boolean equals(Object o) {
		if (!(o instanceof ViewVariable)) {
			return false;
		}
		ViewVariable other = (ViewVariable) o;
		return name.equals(other.name) && valueFactory.equals(other.valueFactory);
	}

	public int hashCode() {
		return name.hashCode() + valueFactory.hashCode();
	}

	public final void create(RequestContext context) {
		Object value = valueFactory.createVariableValue(context);
		context.getFlowScope().put(name, value);
	}

	public final Object restore(RequestContext context) {
		Object value = context.getFlowScope().get(name);
		return valueFactory.restoreReferences(value, context);
	}

	public final Object destroy(RequestContext context) {
		return context.getFlowScope().remove(name);
	}
}
