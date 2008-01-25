package org.springframework.faces.model.converter;

import java.lang.reflect.Constructor;
import java.util.List;

import javax.faces.model.DataModel;

import org.springframework.binding.convert.ConversionContext;
import org.springframework.binding.convert.support.AbstractConverter;
import org.springframework.util.ClassUtils;

public class DataModelConverter extends AbstractConverter {

	protected Object doConvert(Object source, Class targetClass, ConversionContext context) throws Exception {
		Constructor emptyConstructor = ClassUtils.getConstructorIfAvailable(targetClass, new Class[] {});
		DataModel model = (DataModel) emptyConstructor.newInstance(new Object[] {});
		model.setWrappedData(source);
		return model;
	}

	public Class[] getSourceClasses() {
		return new Class[] { Object[].class, List.class, Object.class };
	}

	public Class[] getTargetClasses() {
		return new Class[] { DataModel.class };
	}

}
