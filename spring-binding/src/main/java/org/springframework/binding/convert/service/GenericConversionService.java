/*
 * Copyright 2004-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.binding.convert.service;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.binding.convert.ConversionException;
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.ConversionExecutorNotFoundException;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.converters.ArrayToArray;
import org.springframework.binding.convert.converters.ArrayToCollection;
import org.springframework.binding.convert.converters.CollectionToCollection;
import org.springframework.binding.convert.converters.Converter;
import org.springframework.binding.convert.converters.ObjectToArray;
import org.springframework.binding.convert.converters.ObjectToCollection;
import org.springframework.binding.convert.converters.ReverseConverter;
import org.springframework.binding.convert.converters.SpringConvertingConverterAdapter;
import org.springframework.binding.convert.converters.TwoWayConverter;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.util.Assert;

/**
 * Base implementation of a conversion service. Initially empty, e.g. no converters are registered by default.
 * 
 * @author Keith Donald
 */
public class GenericConversionService implements ConversionService {

	/**
	 * Spring ConversionService where existing custom {@link Converter} types will be registered through an adapter.
	 */
	private org.springframework.core.convert.ConversionService delegate;

	/**
	 * A map of custom converters. Custom converters are assigned a unique identifier that can be used to lookup the
	 * converter. This allows multiple converters for the same source->target class to be registered.
	 */
	private final Map<String, Converter> customConverters = new HashMap<String, Converter>();

	/**
	 * Indexes classes by well-known aliases.
	 */
	private final Map<String, Class<?>> aliasMap = new HashMap<String, Class<?>>();

	/**
	 * An optional parent conversion service.
	 */
	private ConversionService parent;

	/**
	 * Default constructor.
	 */
	public GenericConversionService() {
		FormattingConversionServiceFactoryBean factoryBean = new FormattingConversionServiceFactoryBean();
		factoryBean.afterPropertiesSet();
		this.delegate = factoryBean.getObject();
	}

	/**
	 * Constructor accepting a specific instance of a Spring ConversionService to delegate to.
	 * @param delegateConversionService the conversion service
	 */
	public GenericConversionService(org.springframework.core.convert.ConversionService delegateConversionService) {
		Assert.notNull(delegateConversionService);
		this.delegate = delegateConversionService;
	}

	/**
	 * Returns the parent of this conversion service. Could be null.
	 */
	public ConversionService getParent() {
		return parent;
	}

	/**
	 * Set the parent of this conversion service. This is optional.
	 */
	public void setParent(ConversionService parent) {
		this.parent = parent;
	}

	/**
	 * @return the Spring ConverterRegistry
	 */
	public org.springframework.core.convert.ConversionService getDelegateConversionService() {
		return delegate;
	}

	/**
	 * Registers the given converter with the underlying Spring ConversionService with the help of an adapter. The
	 * adapter allows an existing Spring Binding converter to be invoked within Spring's type conversion system.
	 * 
	 * @param converter the converter
	 * 
	 * @see ConverterRegistry
	 * @see org.springframework.core.convert.ConversionService
	 * @see SpringBindingConverterAdapter
	 */
	public void addConverter(Converter converter) {
		((ConverterRegistry) delegate).addConverter(new SpringBindingConverterAdapter(converter));
		if (converter instanceof TwoWayConverter) {
			TwoWayConverter twoWayConverter = (TwoWayConverter) converter;
			((ConverterRegistry) delegate).addConverter(new SpringBindingConverterAdapter(new ReverseConverter(
					twoWayConverter)));
		}
	}

	/**
	 * Add given custom converter to this conversion service.
	 * 
	 * Note: Converters registered through this method will not be involve the Spring type conversion system, which is
	 * now used the default type conversion mechanism. Spring's type conversion does not support named converters. This
	 * method is provided for backwards compatibility.
	 * 
	 * @param id the id of the custom converter instance
	 * @param converter the converter
	 * 
	 * @deprecated use {@link #addConverter(Converter)} instead or better yet use Spring 3 type conversion and
	 * formatting options (see Spring Documentation).
	 */
	public void addConverter(String id, Converter converter) {
		customConverters.put(id, converter);
	}

	/**
	 * Add an alias for given target type.
	 */
	public void addAlias(String alias, Class<?> targetType) {
		aliasMap.put(alias, targetType);
	}

	public ConversionExecutor getConversionExecutor(Class<?> sourceClass, Class<?> targetClass)
			throws ConversionExecutorNotFoundException {
		Assert.notNull(sourceClass, "The source class to convert from is required");
		Assert.notNull(targetClass, "The target class to convert to is required");
		sourceClass = convertToWrapperClassIfNecessary(sourceClass);
		targetClass = convertToWrapperClassIfNecessary(targetClass);
		if (targetClass.isAssignableFrom(sourceClass)) {
			return new StaticConversionExecutor(sourceClass, targetClass, new NoOpConverter(sourceClass, targetClass));
		}
		if (delegate.canConvert(sourceClass, targetClass)) {
			return new StaticConversionExecutor(sourceClass, targetClass, new SpringConvertingConverterAdapter(
					sourceClass, targetClass, delegate));
		} else if (parent != null) {
			return parent.getConversionExecutor(sourceClass, targetClass);
		} else {
			throw new ConversionExecutorNotFoundException(sourceClass, targetClass,
					"No ConversionExecutor found for converting from sourceClass [" + sourceClass.getName()
							+ "] to target class [" + targetClass.getName() + "]");
		}
	}

	public ConversionExecutor getConversionExecutor(String id, Class<?> sourceClass, Class<?> targetClass)
			throws ConversionExecutorNotFoundException {
		Assert.hasText(id, "The id of the custom converter is required");
		Assert.notNull(sourceClass, "The source class to convert from is required");
		Assert.notNull(targetClass, "The target class to convert to is required");
		Converter converter = customConverters.get(id);
		if (converter == null) {
			if (parent != null) {
				return parent.getConversionExecutor(id, sourceClass, targetClass);
			} else {
				throw new ConversionExecutorNotFoundException(sourceClass, targetClass,
						"No custom ConversionExecutor found with id '" + id + "' for converting from sourceClass ["
								+ sourceClass.getName() + "] to targetClass [" + targetClass.getName() + "]");
			}
		}
		sourceClass = convertToWrapperClassIfNecessary(sourceClass);
		targetClass = convertToWrapperClassIfNecessary(targetClass);
		if (sourceClass.isArray()) {
			Class<?> sourceComponentType = sourceClass.getComponentType();
			if (targetClass.isArray()) {
				Class<?> targetComponentType = targetClass.getComponentType();
				if (converter.getSourceClass().isAssignableFrom(sourceComponentType)) {
					if (!converter.getTargetClass().isAssignableFrom(targetComponentType)) {
						throw new ConversionExecutorNotFoundException(sourceClass, targetClass,
								"Custom ConversionExecutor with id '" + id
										+ "' cannot convert from an array storing elements of type ["
										+ sourceComponentType.getName() + "] to an array of storing elements of type ["
										+ targetComponentType.getName() + "]");
					}
					ConversionExecutor elementConverter = new StaticConversionExecutor(sourceComponentType,
							targetComponentType, converter);
					return new StaticConversionExecutor(sourceClass, targetClass, new ArrayToArray(elementConverter));
				} else if (converter.getTargetClass().isAssignableFrom(sourceComponentType)
						&& converter instanceof TwoWayConverter) {
					TwoWayConverter twoWay = (TwoWayConverter) converter;
					ConversionExecutor elementConverter = new StaticConversionExecutor(sourceComponentType,
							targetComponentType, new ReverseConverter(twoWay));
					return new StaticConversionExecutor(sourceClass, targetClass, new ArrayToArray(elementConverter));
				} else {
					throw new ConversionExecutorNotFoundException(sourceClass, targetClass,
							"Custom ConversionExecutor with id '" + id
									+ "' cannot convert from an array storing elements of type ["
									+ sourceComponentType.getName() + "] to an array storing elements of type ["
									+ targetComponentType.getName() + "]");
				}
			} else if (Collection.class.isAssignableFrom(targetClass)) {
				if (!targetClass.isInterface() && Modifier.isAbstract(targetClass.getModifiers())) {
					throw new IllegalArgumentException("Conversion target class [" + targetClass.getName()
							+ "] is invalid; cannot convert to abstract collection types--"
							+ "request an interface or concrete implementation instead");
				}
				if (converter.getSourceClass().isAssignableFrom(sourceComponentType)) {
					// type erasure has prevented us from getting the concrete type, this is best we can do for now
					ConversionExecutor elementConverter = new StaticConversionExecutor(sourceComponentType,
							converter.getTargetClass(), converter);
					return new StaticConversionExecutor(sourceClass, targetClass, new ArrayToCollection(
							elementConverter));
				} else if (converter.getTargetClass().isAssignableFrom(sourceComponentType)
						&& converter instanceof TwoWayConverter) {
					TwoWayConverter twoWay = (TwoWayConverter) converter;
					ConversionExecutor elementConverter = new StaticConversionExecutor(sourceComponentType,
							converter.getSourceClass(), new ReverseConverter(twoWay));
					return new StaticConversionExecutor(sourceClass, targetClass, new ArrayToCollection(
							elementConverter));
				} else {
					throw new ConversionExecutorNotFoundException(sourceClass, targetClass,
							"Custom ConversionExecutor with id '" + id
									+ "' cannot convert from array an storing elements type ["
									+ sourceComponentType.getName() + "] to a collection of type ["
									+ targetClass.getName() + "]");
				}
			}
		}
		if (targetClass.isArray()) {
			Class<?> targetComponentType = targetClass.getComponentType();
			if (Collection.class.isAssignableFrom(sourceClass)) {
				// type erasure limits us here as well
				if (converter.getTargetClass().isAssignableFrom(targetComponentType)) {
					ConversionExecutor elementConverter = new StaticConversionExecutor(converter.getSourceClass(),
							targetComponentType, converter);
					Converter collectionToArray = new ReverseConverter(new ArrayToCollection(elementConverter));
					return new StaticConversionExecutor(sourceClass, targetClass, collectionToArray);
				} else if (converter.getSourceClass().isAssignableFrom(targetComponentType)
						&& converter instanceof TwoWayConverter) {
					TwoWayConverter twoWay = (TwoWayConverter) converter;
					ConversionExecutor elementConverter = new StaticConversionExecutor(converter.getTargetClass(),
							targetComponentType, new ReverseConverter(twoWay));
					Converter collectionToArray = new ReverseConverter(new ArrayToCollection(elementConverter));
					return new StaticConversionExecutor(sourceClass, targetClass, collectionToArray);
				} else {
					throw new ConversionExecutorNotFoundException(sourceClass, targetClass,
							"Custom ConversionExecutor with id '" + id + "' cannot convert from collection of type ["
									+ sourceClass.getName() + "] to an array storing elements of type ["
									+ targetComponentType.getName() + "]");
				}
			} else {
				if (converter.getSourceClass().isAssignableFrom(sourceClass)) {
					if (!converter.getTargetClass().isAssignableFrom(targetComponentType)) {
						throw new ConversionExecutorNotFoundException(sourceClass, targetClass,
								"Custom ConversionExecutor with id '" + id + "' cannot convert from sourceClass ["
										+ sourceClass.getName() + "] to array holding elements of type ["
										+ targetComponentType.getName() + "]");
					}
					ConversionExecutor elementConverter = new StaticConversionExecutor(sourceClass,
							targetComponentType, converter);
					return new StaticConversionExecutor(sourceClass, targetClass, new ObjectToArray(elementConverter));
				} else if (converter.getTargetClass().isAssignableFrom(sourceClass)
						&& converter instanceof TwoWayConverter) {
					if (!converter.getSourceClass().isAssignableFrom(targetComponentType)) {
						throw new ConversionExecutorNotFoundException(sourceClass, targetClass,
								"Custom ConversionExecutor with id '" + id + "' cannot convert from sourceClass ["
										+ sourceClass.getName() + "] to array holding elements of type ["
										+ targetComponentType.getName() + "]");
					}
					TwoWayConverter twoWay = (TwoWayConverter) converter;
					ConversionExecutor elementConverter = new StaticConversionExecutor(sourceClass,
							targetComponentType, new ReverseConverter(twoWay));
					return new StaticConversionExecutor(sourceClass, targetClass, new ObjectToArray(elementConverter));
				}
			}
		}
		if (Collection.class.isAssignableFrom(targetClass)) {
			if (Collection.class.isAssignableFrom(sourceClass)) {
				ConversionExecutor elementConverter;
				// type erasure forces us to do runtime checks of list elements
				if (converter instanceof TwoWayConverter) {
					elementConverter = new TwoWayCapableConversionExecutor(converter.getSourceClass(),
							converter.getTargetClass(), (TwoWayConverter) converter);
				} else {
					elementConverter = new StaticConversionExecutor(converter.getSourceClass(),
							converter.getTargetClass(), converter);
				}
				return new StaticConversionExecutor(sourceClass, targetClass, new CollectionToCollection(
						elementConverter));
			} else {
				ConversionExecutor elementConverter;
				// type erasure forces us to do runtime checks of list elements
				if (converter instanceof TwoWayConverter) {
					elementConverter = new TwoWayCapableConversionExecutor(sourceClass, converter.getTargetClass(),
							(TwoWayConverter) converter);
				} else {
					elementConverter = new StaticConversionExecutor(sourceClass, converter.getTargetClass(), converter);
				}
				if (!Collection.class.isAssignableFrom(converter.getTargetClass())) {
					elementConverter = new StaticConversionExecutor(sourceClass, targetClass, new ObjectToCollection(
							elementConverter));
				}
				return elementConverter;
			}
		}
		if (converter.getSourceClass().isAssignableFrom(sourceClass)) {
			if (!converter.getTargetClass().isAssignableFrom(targetClass)) {
				throw new ConversionExecutorNotFoundException(sourceClass, targetClass,
						"Custom ConversionExecutor with id '" + id + "' cannot convert from sourceClass ["
								+ sourceClass.getName() + "] to targetClass [" + targetClass.getName() + "]");
			}
			return new StaticConversionExecutor(sourceClass, targetClass, converter);
		} else if (converter.getTargetClass().isAssignableFrom(sourceClass) && converter instanceof TwoWayConverter) {
			if (!converter.getSourceClass().isAssignableFrom(targetClass)) {
				throw new ConversionExecutorNotFoundException(sourceClass, targetClass,
						"Custom ConversionExecutor with id '" + id + "' cannot convert from sourceClass ["
								+ sourceClass.getName() + "] to targetClass [" + targetClass.getName() + "]");
			}
			TwoWayConverter twoWay = (TwoWayConverter) converter;
			return new StaticConversionExecutor(sourceClass, targetClass, new ReverseConverter(twoWay));
		} else {
			throw new ConversionExecutorNotFoundException(sourceClass, targetClass,
					"Custom ConversionExecutor with id '" + id + "' cannot convert from sourceClass ["
							+ sourceClass.getName() + "] to targetClass [" + targetClass.getName() + "]");
		}
	}

	public Object executeConversion(Object source, Class<?> targetClass) throws ConversionException {
		if (source != null) {
			ConversionExecutor conversionExecutor = getConversionExecutor(source.getClass(), targetClass);
			return conversionExecutor.execute(source);
		} else {
			return null;
		}
	}

	public Object executeConversion(String converterId, Object source, Class<?> targetClass) throws ConversionException {
		if (source != null) {
			ConversionExecutor conversionExecutor = getConversionExecutor(converterId, source.getClass(), targetClass);
			return conversionExecutor.execute(source);
		} else {
			return null;
		}
	}

	public Class<?> getClassForAlias(String name) throws IllegalArgumentException {
		Class<?> clazz = aliasMap.get(name);
		if (clazz != null) {
			return clazz;
		} else {
			if (parent != null) {
				return parent.getClassForAlias(name);
			} else {
				return null;
			}
		}
	}

	// internal helpers

	private Class<?> convertToWrapperClassIfNecessary(Class<?> targetType) {
		if (targetType.isPrimitive()) {
			if (targetType.equals(int.class)) {
				return Integer.class;
			} else if (targetType.equals(short.class)) {
				return Short.class;
			} else if (targetType.equals(long.class)) {
				return Long.class;
			} else if (targetType.equals(float.class)) {
				return Float.class;
			} else if (targetType.equals(double.class)) {
				return Double.class;
			} else if (targetType.equals(byte.class)) {
				return Byte.class;
			} else if (targetType.equals(boolean.class)) {
				return Boolean.class;
			} else if (targetType.equals(char.class)) {
				return Character.class;
			} else {
				throw new IllegalStateException("Should never happen - primitive type is not a primitive?");
			}
		} else {
			return targetType;
		}
	}

}
