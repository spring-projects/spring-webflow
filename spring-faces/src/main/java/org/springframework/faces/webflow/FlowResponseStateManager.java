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

	private static final int TREE_STATE_INDEX = 0;

	private static final int COMPONENT_STATE_INDEX = 1;

	// TODO - This needs to be replaced with a common static, probably from FlowExecutorArgumentExtractor
	private static final String VIEW_STATE_PARAM = "org.springframework.webflow.FlowExecutionKey";

	private static final char[] STATE_FIELD_START = ("<input type=\"hidden\" name=\"" + VIEW_STATE_PARAM + "\" id=\""
			+ VIEW_STATE_PARAM + "\" value=\"").toCharArray();

	private static final char[] STATE_FIELD_END = "\" />".toCharArray();

	public Object getComponentStateToRestore(FacesContext context) {

		Object[] state = (Object[]) RequestContextHolder.getRequestContext().getFlashScope().get(JsfView.STATE_KEY);
		return state[COMPONENT_STATE_INDEX];
	}

	public Object getTreeStructureToRestore(FacesContext context, String viewId) {
		Object[] state = (Object[]) RequestContextHolder.getRequestContext().getFlashScope().get(JsfView.STATE_KEY);
		return state[TREE_STATE_INDEX];
	}

	public boolean isPostback(FacesContext context) {

		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Auto-generated method stub");
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

		Object[] serializableState = new Object[] { state.getStructure(), state.getState() };

		RequestContextHolder.getRequestContext().getFlashScope().put(JsfView.STATE_KEY, serializableState);

		Writer writer = context.getResponseWriter();
		writer.write(STATE_FIELD_START);
		writer.write(RequestContextHolder.getRequestContext().getFlowExecutionContext().getKey().toString());
		writer.write(STATE_FIELD_END);
	}

}
