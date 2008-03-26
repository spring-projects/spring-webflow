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
public class DefaultMappingContext {

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

	/**
	 * The object being mapped from.
	 */
	public Object getSource() {
		return source;
	}

	/**
	 * The object being mapped to.
	 */
	public Object getTarget() {
		return target;
	}

	/**
	 * Returns the conversion service that can be used to perform type conversions during the mapping process. May be
	 * null if no externally managed conversion service is provided.
	 */
	public ConversionService getConversionService() {
		return conversionService;
	}

	/**
	 * Returns the current mapping.
	 * @return the current mapping
	 */
	public Mapping getCurrentMapping() {
		return currentMapping;
	}

	/**
	 * Sets the current mapping. Called when a single mapping operation is about to begin. This updates progress of the
	 * overall mapping transaction.
	 * @param mapping the mapping to make the current mapping
	 */
	public void setCurrentMapping(Mapping mapping) {
		if (this.currentMapping != null) {
			throw new IllegalStateException("The current mapping has not finished yet");
		}
		this.currentMapping = mapping;
	}

	/**
	 * Indicates the current mapping completed successfully.
	 * @param originalValue the original value from the source of the mapping
	 * @param mappedValue the successfully mapped value, which may be different from the original if a type conversion
	 * was performed
	 */
	public void setSuccessResult(Object originalValue, Object mappedValue) {
		add(new MappingResult(currentMapping, new Success(mappedValue, originalValue)));
	}

	/**
	 * Indicates the current mapping ended with a 'required' error. This means the value obtained from the source was
	 * empty, and the mapping could not be completed as a result.
	 * @param originalValue the original source value that is empty (null or an empty string, typically)
	 */
	public void setRequiredErrorResult(Object originalValue) {
		add(new MappingResult(currentMapping, new RequiredError(originalValue)));
	}

	/**
	 * Indicates the current mapping ended with a 'type conversion' error. This means the value obtained from the source
	 * could not be converted to a type that could be assigned to the target expression.
	 * @param originalValue the original source value that could not be converted during the mapping attempt
	 * @param targetType the desired target type to which conversion could not be performed
	 */
	public void setTypeConversionErrorResult(Object originalValue, Class targetType) {
		add(new MappingResult(currentMapping, new TypeConversionError(originalValue, targetType)));
	}

	/**
	 * Indicates a error occurred accessing the source mapping expression.
	 * @param error the error that occurred
	 */
	public void setSourceAccessError(EvaluationException error) {
		add(new MappingResult(currentMapping, new SourceAccessError(error)));
	}

	/**
	 * Indicates a error occurred accessing the target mapping expression.
	 * @param error the error that occurred
	 */
	public void setTargetAccessError(Object originalValue, EvaluationException error) {
		add(new MappingResult(currentMapping, new TargetAccessError(originalValue, error)));
	}

	public MappingResults toResult() {
		return new DefaultMappingResults(source, target, mappingResults);
	}

	// internal helpers

	private void add(MappingResult result) {
		if (logger.isDebugEnabled()) {
			logger.debug("Adding " + result);
		}
		this.mappingResults.add(result);
		this.currentMapping = null;
	}

}
