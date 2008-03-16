package org.springframework.binding.mapping.impl;

import java.util.ArrayList;
import java.util.List;

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

class DefaultMappingContext implements MappingContext {

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

	public void setCurrentMapping(Mapping mapping) {
		if (this.currentMapping != null) {
			throw new IllegalStateException("The current mapping has not finished yet");
		}
		this.currentMapping = mapping;
	}

	public void setSuccessResult(Object originalValue, Object mappedValue) {
		mappingResults.add(new MappingResult(currentMapping, new Success(originalValue, mappedValue)));
		currentMapping = null;
	}

	public void setRequiredErrorResult(Object originalValue) {
		mappingResults.add(new MappingResult(currentMapping, new RequiredError(originalValue)));
		this.currentMapping = null;
	}

	public void setTypeConversionErrorResult(Object originalValue, Class targetType) {
		mappingResults.add(new MappingResult(currentMapping, new TypeConversionError(originalValue, targetType)));
		this.currentMapping = null;
	}

	public void setSourceAccessError(EvaluationException error) {
		mappingResults.add(new MappingResult(currentMapping, new SourceAccessError(error)));
		this.currentMapping = null;
	}

	public void setTargetAccessError(Object originalValue, EvaluationException error) {
		mappingResults.add(new MappingResult(currentMapping, new TargetAccessError(originalValue, error)));
		this.currentMapping = null;
	}

	public MappingResults toResult() {
		return new DefaultMappingResults(source, target, mappingResults);
	}
}
