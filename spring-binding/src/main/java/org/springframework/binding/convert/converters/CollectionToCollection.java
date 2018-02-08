package org.springframework.binding.convert.converters;

import java.util.Collection;

import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.ConversionService;
import org.springframework.core.CollectionFactory;
import org.springframework.core.ResolvableType;

/**
 * A converter that can convert from one collection type to another.
 * 
 * @author Keith Donald
 */
public class CollectionToCollection implements Converter {

	private static final int DEFAULT_INITIAL_SIZE = 16;

	private ConversionService conversionService;

	private ConversionExecutor elementConverter;

	/**
	 * Creates a new collection-to-collection converter
	 * @param conversionService the conversion service to use to convert collection elements to add to the target
	 * collection
	 */
	public CollectionToCollection(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	/**
	 * Creates a new collection-to-collection converter
	 * @param elementConverter a specific converter to use to convert collection elements added to the target collection
	 */
	public CollectionToCollection(ConversionExecutor elementConverter) {
		this.elementConverter = elementConverter;
	}

	public Class<?> getSourceClass() {
		return Collection.class;
	}

	public Class<?> getTargetClass() {
		return Collection.class;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object convertSourceToTargetClass(Object source, Class<?> targetClass) {
		if (source == null) {
			return null;
		}
		Collection targetCollection = CollectionFactory.createCollection(targetClass, DEFAULT_INITIAL_SIZE);
		ConversionExecutor elementConverter = getElementConverter(source, (Class<? extends Collection<?>>) targetClass);
		Collection sourceCollection = (Collection) source;
		for (Object value : sourceCollection) {
			if (elementConverter != null) {
				value = elementConverter.execute(value);
			}
			targetCollection.add(value);
		}
		return targetCollection;
	}

	private ConversionExecutor getElementConverter(Object source, Class<? extends Collection<?>> targetClass) {
		if (elementConverter != null) {
			return elementConverter;
		} else {
			Class<?> elementType = ResolvableType.forClass(targetClass).asCollection().resolveGeneric(0);
			if (elementType != null) {
				Class<?> componentType = source.getClass().getComponentType();
				return conversionService.getConversionExecutor(componentType, elementType);
			}
			return null;
		}
	}

}
