/*
 * Copyright 2004-2005 the original author or authors.
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
package org.springframework.binding.convert.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.util.Assert;

/**
 * Registers all 'from string' converters known to a conversion service with a Spring bean factory.
 * <p>
 * Acts as bean factory post processor, registering property editor adapters for each supported conversion with a
 * <code>java.lang.String sourceClass</code>. This makes for very convenient use with the Spring container.
 * 
 * @author Keith Donald
 */
public class CustomConverterConfigurer implements BeanFactoryPostProcessor, InitializingBean {

	private ConversionService conversionService;

	/**
	 * Set the conversion service.
	 * @param conversionService the conversion service to take converters from
	 */
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(conversionService, "The conversion service is required");
	}

	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		final ConversionExecutor[] executors = conversionService.getConversionExecutorsForSource(String.class);
		PropertyEditorRegistrar registrar = new PropertyEditorRegistrar() {
			public void registerCustomEditors(PropertyEditorRegistry registry) {
				for (int i = 0; i < executors.length; i++) {
					ConverterPropertyEditorAdapter editor = new ConverterPropertyEditorAdapter(executors[i]);
					registry.registerCustomEditor(editor.getTargetClass(), editor);
				}
			}
		};
		beanFactory.addPropertyEditorRegistrar(registrar);
	}
}