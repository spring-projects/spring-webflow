package org.springframework.faces.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.faces.component.ContextCallback;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Customizes the behavior of an existing UIViewRoot with Ajax-aware processing.
 * 
 * @author Jeremy Grelle
 */
public class AjaxViewRoot extends DelegatingViewRoot {

	public static final String AJAX_SOURCE_PARAM = "ajaxSource";

	public static final String PROCESS_IDS_PARAM = "processIds";

	public static final String RENDER_IDS_PARAM = "renderIds";

	protected static final String FORM_RENDERED = "formRendered";

	private List events = new ArrayList();

	private String[] processIds;

	private String[] renderIds;

	public AjaxViewRoot(UIViewRoot original) {
		super(original);
		swapChildren(original, this);
		setId(original.getId() + "_ajax");
	}

	private void swapChildren(UIViewRoot source, UIViewRoot target) {
		target.getChildren().addAll(source.getChildren());
		Iterator i = target.getChildren().iterator();
		while (i.hasNext()) {
			UIComponent child = (UIComponent) i.next();
			child.setParent(target);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.faces.webflow.DelegatingViewRoot#encodeAll(javax.faces.context.FacesContext)
	 */
	public void encodeAll(FacesContext context) throws IOException {
		processRequestParams(context);
		for (int i = 0; i < renderIds.length; i++) {
			String renderId = renderIds[i];
			ContextCallback callback = new ContextCallback() {
				public void invokeContextCallback(FacesContext context, UIComponent target) {
					try {
						target.encodeAll(context);
						if (target instanceof UIForm) {
							context.getViewRoot().getAttributes().put(FORM_RENDERED, FORM_RENDERED);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			invokeOnComponent(context, renderId, callback);
		}
		swapChildren(this, getOriginalViewRoot());
		context.setViewRoot(getOriginalViewRoot());
		if (!getAttributes().containsKey(FORM_RENDERED)) {
			context.getApplication().getViewHandler().writeState(context);
			updateFormAction(context);
		}

		broadCastEvents(context, PhaseId.APPLY_REQUEST_VALUES);
	}

	/**
	 * 
	 * @param context
	 */
	private void updateFormAction(FacesContext context) {
		ResponseWriter writer = context.getResponseWriter();
		try {
			String formId = findContainingFormId(context);
			if (StringUtils.hasLength(formId)) {
				String script = "dojo.byId('" + formId + "').action = "
						+ context.getApplication().getViewHandler().getActionURL(context, getViewId());
				writer.startElement("script", null);
				writer.writeText(script, null);
				writer.endElement("script");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String findContainingFormId(FacesContext context) {
		for (int i = 0; i < renderIds.length; i++) {
			UIComponent component = context.getViewRoot().findComponent(renderIds[i]);
			while (!(component instanceof UIViewRoot)) {
				component = component.getParent();
				if (component instanceof UIForm) {
					return component.getClientId(context);
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.faces.webflow.DelegatingViewRoot#processApplication(javax.faces.context.FacesContext)
	 */
	public void processApplication(FacesContext context) {
		broadCastEvents(context, PhaseId.INVOKE_APPLICATION);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.faces.webflow.DelegatingViewRoot#processDecodes(javax.faces.context.FacesContext)
	 */
	public void processDecodes(FacesContext context) {
		processRequestParams(context);
		for (int i = 0; i < processIds.length; i++) {
			String processId = processIds[i];
			ContextCallback callback = new ContextCallback() {
				public void invokeContextCallback(FacesContext context, UIComponent target) {
					target.processDecodes(context);
				}
			};
			invokeOnComponent(context, processId, callback);
		}
		broadCastEvents(context, PhaseId.APPLY_REQUEST_VALUES);
	}

	private void processRequestParams(FacesContext context) {

		String processIdsParam = (String) context.getExternalContext().getRequestParameterMap().get(PROCESS_IDS_PARAM);
		processIds = StringUtils.delimitedListToStringArray(processIdsParam, ",", " ");
		processIds = removeNestedChildren(context, processIds);

		String renderIdsParam = (String) context.getExternalContext().getRequestParameterMap().get(RENDER_IDS_PARAM);
		renderIds = StringUtils.delimitedListToStringArray(renderIdsParam, ",", " ");
		renderIds = removeNestedChildren(context, renderIds);
	}

	private String[] removeNestedChildren(FacesContext context, String[] ids) {
		List idList = Arrays.asList(ids);
		final List trimmedIds = new ArrayList(idList);

		for (final ListIterator i = trimmedIds.listIterator(); i.hasNext();) {
			String id = (String) i.next();
			invokeOnComponent(context, id, new ContextCallback() {
				public void invokeContextCallback(FacesContext context, UIComponent component) {
					while (!(component.getParent() instanceof UIViewRoot)) {
						component = component.getParent();
						if (trimmedIds.contains(component.getClientId(context))) {
							i.remove();
						}
					}
				}
			});
		}
		return (String[]) trimmedIds.toArray(new String[trimmedIds.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.faces.webflow.DelegatingViewRoot#processUpdates(javax.faces.context.FacesContext)
	 */
	public void processUpdates(FacesContext context) {
		for (int i = 0; i < processIds.length; i++) {
			String processId = processIds[i];
			ContextCallback callback = new ContextCallback() {
				public void invokeContextCallback(FacesContext context, UIComponent target) {
					target.processUpdates(context);
				}
			};
			invokeOnComponent(context, processId, callback);
		}
		broadCastEvents(context, PhaseId.UPDATE_MODEL_VALUES);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.faces.webflow.DelegatingViewRoot#processValidators(javax.faces.context.FacesContext)
	 */
	public void processValidators(FacesContext context) {
		for (int i = 0; i < processIds.length; i++) {
			String processId = processIds[i];
			ContextCallback callback = new ContextCallback() {
				public void invokeContextCallback(FacesContext context, UIComponent target) {
					target.processValidators(context);
				}
			};
			invokeOnComponent(context, processId, callback);
		}
		broadCastEvents(context, PhaseId.PROCESS_VALIDATIONS);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.faces.webflow.DelegatingViewRoot#queueEvent(javax.faces.event.FacesEvent)
	 */
	public void queueEvent(FacesEvent event) {
		Assert.notNull(event, "Cannot queue a null event.");
		events.add(event);
	}

	private void broadCastEvents(FacesContext context, PhaseId phaseId) {

		List processedEvents = new ArrayList();

		if (events.size() == 0)
			return;

		boolean abort = false;

		int phaseIdOrdinal = phaseId.getOrdinal();
		Iterator i = events.iterator();
		while (i.hasNext()) {
			FacesEvent event = (FacesEvent) i.next();
			int ordinal = event.getPhaseId().getOrdinal();
			if (ordinal == PhaseId.ANY_PHASE.getOrdinal() || ordinal == phaseIdOrdinal) {
				UIComponent source = event.getComponent();
				try {
					processedEvents.add(event);
					source.broadcast(event);
				} catch (AbortProcessingException e) {
					abort = true;
					break;
				}
			}
		}

		if (abort) {
			events.clear();
		} else {
			events.removeAll(processedEvents);
		}
	}

	protected String[] getProcessIds() {
		return processIds;
	}

	protected String[] getRenderIds() {
		return renderIds;
	}

}
