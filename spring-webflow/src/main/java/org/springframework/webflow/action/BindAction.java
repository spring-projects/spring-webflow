package org.springframework.webflow.action;

import java.util.Iterator;

import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.support.RuntimeBindingConversionExecutor;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.support.ParserContextImpl;
import org.springframework.binding.mapping.AttributeMappingException;
import org.springframework.binding.mapping.DefaultAttributeMapper;
import org.springframework.binding.mapping.Mapping;
import org.springframework.binding.mapping.MappingContextImpl;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

public class BindAction extends AbstractAction {

	private Expression target;

	private ExpressionParser expressionParser;

	private ConversionService conversionService;

	public BindAction(Expression target, ExpressionParser expressionParser, ConversionService conversionService) {
		this.target = target;
		this.expressionParser = expressionParser;
		this.conversionService = conversionService;
	}

	protected Event doExecute(final RequestContext context) throws Exception {
		Object target = this.target.getValue(context);
		if (target == null) {
			throw new IllegalStateException(
					"The bind target cannot be null - check your expression.  Bind target expression = " + target);
		}
		DefaultAttributeMapper mapper = new DefaultAttributeMapper();
		AttributeMap eventAttributes = context.getLastEvent().getAttributes();
		for (Iterator it = eventAttributes.asMap().keySet().iterator(); it.hasNext();) {
			String name = (String) it.next();
			Expression sourceAttribute = expressionParser.parseExpression(name, new ParserContextImpl()
					.eval(AttributeMap.class));
			Expression targetAttribute = expressionParser.parseExpression(name, new ParserContextImpl().eval(target
					.getClass()));
			Class targetType = targetAttribute.getValueType(target);
			mapper.addMapping(new Mapping(sourceAttribute, targetAttribute, new RuntimeBindingConversionExecutor(
					targetType, conversionService), false));
		}
		try {
			mapper.map(context.getLastEvent().getAttributes(), target, new MappingContextImpl(context
					.getMessageContext()));
			return success();
		} catch (AttributeMappingException e) {
			return error();
		}
	}
}
