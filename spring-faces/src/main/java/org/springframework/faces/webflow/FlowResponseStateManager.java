package org.springframework.faces.webflow;

import java.io.IOException;
import java.io.Writer;

import javax.el.ValueExpression;
import javax.faces.application.StateManager.SerializedView;
import javax.faces.context.FacesContext;
import javax.faces.render.ResponseStateManager;

import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.RequestContextHolder;

public class FlowResponseStateManager extends ResponseStateManager {

	private static final int VIEW_ID_INDEX = 0;

	private static final int TREE_STATE_INDEX = 1;

	private static final int COMPONENT_STATE_INDEX = 2;

	// TODO - This needs to be replaced with a common static, probably from FlowExecutorArgumentExtractor
	private static final String VIEW_STATE_PARAM = "org.springframework.webflow.FlowExecutionKey";

	private static final char[] STATE_FIELD_START = ("<input type=\"hidden\" name=\"" + VIEW_STATE_PARAM + "\" id=\""
			+ VIEW_STATE_PARAM + "\" value=\"").toCharArray();

	private static final char[] STATE_FIELD_END = "\" />".toCharArray();

	public Object getState(FacesContext context, String viewId) {
		Object[] structureAndState = new Object[2];
		structureAndState[0] = getTreeStructureToRestore(context, viewId);
		if (structureAndState[0] == null) {
			return null;
		}
		structureAndState[1] = getComponentStateToRestore(context);
		return structureAndState;
	}

	public Object getComponentStateToRestore(FacesContext context) {
		Object[] state = (Object[]) RequestContextHolder.getRequestContext().getFlowScope().get(JsfView.STATE_KEY);
		if (state == null) {
			return null;
		} else {
			return state[COMPONENT_STATE_INDEX];
		}
	}

	public Object getTreeStructureToRestore(FacesContext context, String viewId) {
		Object[] state = (Object[]) RequestContextHolder.getRequestContext().getFlowScope().get(JsfView.STATE_KEY);
		if (state == null || !state[VIEW_ID_INDEX].equals(viewId)) {
			return null;
		} else {
			return state[TREE_STATE_INDEX];
		}
	}

	private FlowExecution getFlowExecution() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ValueExpression expr = ctx.getApplication().getExpressionFactory().createValueExpression(ctx.getELContext(),
				"#{flowExecution}", FlowExecution.class);
		return (FlowExecution) expr.getValue(ctx.getELContext());
	}

	/**
	 * Stores the serializable component state in Flash scope and writes out the FlowExecutionKey
	 */
	public void writeState(FacesContext context, SerializedView state) throws IOException {

		Object[] serializableState = new Object[] { context.getViewRoot().getViewId(), state.getStructure(),
				state.getState() };

		RequestContextHolder.getRequestContext().getFlowScope().put(JsfView.STATE_KEY, serializableState);

		Writer writer = context.getResponseWriter();
		writer.write(STATE_FIELD_START);
		writer.write(RequestContextHolder.getRequestContext().getFlowExecutionContext().getKey().toString());
		writer.write(STATE_FIELD_END);
	}

}
