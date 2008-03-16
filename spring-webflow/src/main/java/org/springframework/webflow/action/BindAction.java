package org.springframework.webflow.action;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.expression.support.ParserContextImpl;
import org.springframework.binding.mapping.MappingResult;
import org.springframework.binding.mapping.MappingResults;
import org.springframework.binding.mapping.MappingResultsCriteria;
import org.springframework.binding.mapping.impl.DefaultMapper;
import org.springframework.binding.mapping.impl.DefaultMapping;
import org.springframework.binding.mapping.results.TargetAccessError;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

public class BindAction extends AbstractAction {

	private static final Log logger = LogFactory.getLog(BindAction.class);

	private static final MappingResultsCriteria PROPERTY_NOT_FOUND_ERRORS = new PropertyNotFoundErrors();

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
		DefaultMapper mapper = new DefaultMapper();
		mapper.setConversionService(conversionService);
		AttributeMap eventAttributes = context.getLastEvent().getAttributes();
		if (logger.isDebugEnabled()) {
			logger.debug("Binding event '" + context.getLastEvent().getId() + "' attributes " + eventAttributes
					+ " to target " + target);
		}
		for (Iterator it = eventAttributes.asMap().keySet().iterator(); it.hasNext();) {
			String name = (String) it.next();
			Expression sourceAttribute = expressionParser.parseExpression(name, new ParserContextImpl()
					.eval(AttributeMap.class));
			Expression targetAttribute = expressionParser.parseExpression(name, new ParserContextImpl().eval(target
					.getClass()));
			mapper.addMapping(new DefaultMapping(sourceAttribute, targetAttribute));
		}
		MappingResults results = mapper.map(context.getLastEvent().getAttributes(), target);
		if (!results.hasErrorResults()) {
			return success();
		} else {
			if (results.getResults(PROPERTY_NOT_FOUND_ERRORS).size() == results.getErrorResults().size()) {
				// all errors are 'property not found' -- acceptable
				return success();
			} else {
				return error();
			}
		}
	}

	private static class PropertyNotFoundErrors implements MappingResultsCriteria {
		public boolean test(MappingResult result) {
			return result.getResult() instanceof TargetAccessError
					&& result.getResult().getErrorCode().equals("propertyNotFound");
		}
	}
}
