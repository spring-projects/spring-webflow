package org.springframework.binding.mapping.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.expression.EvaluationException;
import org.springframework.binding.mapping.Mapping;
import org.springframework.binding.mapping.MappingResult;
import org.springframework.binding.mapping.MappingResults;
import org.springframework.binding.mapping.results.RequiredError;
import org.springframework.binding.mapping.results.SourceAccessError;
import org.springframework.binding.mapping.results.Success;
import org.springframework.binding.mapping.results.TargetAccessError;
import org.springframework.binding.mapping.results.TypeConversionError;

/**
 * Default mapping context implementation.
 * @author Keith Donald
 */
class DefaultMappingContext implements MappingContext {

	private static final Log logger = LogFactory.getLog(DefaultMapping.class);

	private Object source;

	private Object target;

	private Mapping currentMapping;

	private List mappingResults;

	private ConversionService conversionService;

	public DefaultMappingContext(Object source, Object target, ConversionService conversionService) {
		this.source = source;
		this.target = target;
		this.conversionService = conversionService;
		this.mappingResults = new ArrayList();
	}

	public Object getSource() {
		return source;
	}

	public Object getTarget() {
		return target;
	}

	public ConversionService getConversionService() {
		return conversionService;
	}

	public Mapping getCurrentMapping() {
		return currentMapping;
	}

	public void setCurrentMapping(Mapping mapping) {
		if (this.currentMapping != null) {
			throw new IllegalStateException("The current mapping has not finished yet");
		}
		this.currentMapping = mapping;
	}

	public void setSuccessResult(Object originalValue, Object mappedValue) {
		add(new MappingResult(currentMapping, new Success(mappedValue, originalValue)));
	}

	public void setRequiredErrorResult(Object originalValue) {
		add(new MappingResult(currentMapping, new RequiredError(originalValue)));
	}

	public void setTypeConversionErrorResult(Object originalValue, Class targetType) {
		add(new MappingResult(currentMapping, new TypeConversionError(originalValue, targetType)));
	}

	public void setSourceAccessError(EvaluationException error) {
		add(new MappingResult(currentMapping, new SourceAccessError(error)));
	}

	public void setTargetAccessError(Object originalValue, EvaluationException error) {
		add(new MappingResult(currentMapping, new TargetAccessError(originalValue, error)));
	}

	private void add(MappingResult result) {
		if (logger.isDebugEnabled()) {
			logger.debug("Adding " + result);
		}
		this.mappingResults.add(result);
		this.currentMapping = null;
	}

	public MappingResults toResult() {
		return new DefaultMappingResults(source, target, mappingResults);
	}
}
