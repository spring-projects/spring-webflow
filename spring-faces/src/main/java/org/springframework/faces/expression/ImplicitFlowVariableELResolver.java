package org.springframework.faces.expression;

import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.PropertyNotFoundException;
import javax.el.PropertyNotWritableException;

import org.springframework.core.enums.StaticLabeledEnum;
import org.springframework.core.enums.StaticLabeledEnumResolver;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

public class ImplicitFlowVariableELResolver extends ELResolver {

	public Class getCommonPropertyType(ELContext context, Object base) {
		return Object.class;
	}

	public Iterator getFeatureDescriptors(ELContext context, Object base) {
		return null;
	}

	public Class getType(ELContext context, Object base, Object property) {
		if (base == null) {
			Object resolved = resolveVariable(property.toString());
			if (resolved != null) {
				context.setPropertyResolved(true);
				return resolved.getClass();
			}
		}
		return null;
	}

	public Object getValue(ELContext context, Object base, Object property) {
		if (base == null) {
			Object resolved = resolveVariable(property.toString());
			if (resolved != null) {
				context.setPropertyResolved(true);
				return resolved;
			}
		}
		return null;
	}

	public boolean isReadOnly(ELContext context, Object base, Object property) {
		if (base == null) {
			Object resolved = resolveVariable(property.toString());
			if (resolved != null) {
				context.setPropertyResolved(true);
				return true;
			}
		}
		return false;
	}

	public void setValue(ELContext context, Object base, Object property, Object value) {
		if (base == null) {
			Object resolved = resolveVariable(property.toString());
			if (resolved != null) {
				context.setPropertyResolved(true);
				throw new PropertyNotWritableException("The implicit flow variable " + property
						+ " cannot be overwritten.");
			}
		}
	}

	private Object resolveVariable(String variable) {
		try {
			ImplicitFlowVariable flowVariable = (ImplicitFlowVariable) StaticLabeledEnumResolver.instance()
					.getLabeledEnumByLabel(ImplicitFlowVariable.class, variable);
			return flowVariable.resolve();
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}

	public abstract static class ImplicitFlowVariable extends StaticLabeledEnum {

		private ImplicitFlowVariable(int code, String label) {
			super(code, label);
		}

		public static final ImplicitFlowVariable FLASH_SCOPE = new ImplicitFlowVariable(1, "flashScope") {
			public Object doResolve(RequestContext context) {
				return context.getFlashScope();
			}
		};

		public static final ImplicitFlowVariable FLOW_SCOPE = new ImplicitFlowVariable(2, "flowScope") {
			public Object doResolve(RequestContext context) {
				return context.getFlowScope();
			}
		};

		public static final ImplicitFlowVariable CONVERSATION_SCOPE = new ImplicitFlowVariable(3, "conversationScope") {
			public Object doResolve(RequestContext context) {
				return context.getConversationScope();
			}
		};

		public Object resolve() {
			RequestContext context = RequestContextHolder.getRequestContext();
			if (context != null) {
				return doResolve(context);
			} else {
				throw new PropertyNotFoundException("Implicit flow variable " + this.getLabel()
						+ " could not be resolved.  No active flow found.");
			}
		}

		public abstract Object doResolve(RequestContext context);
	}

}
