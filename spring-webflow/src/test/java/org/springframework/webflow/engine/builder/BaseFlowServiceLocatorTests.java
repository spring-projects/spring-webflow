/*
 * Copyright 2002-2007 the original author or authors.
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
package org.springframework.webflow.engine.builder;

import org.springframework.binding.convert.support.GenericConversionService;
import org.springframework.binding.convert.support.TextToBoolean;
import org.springframework.webflow.engine.NullViewSelector;
import org.springframework.webflow.engine.ViewSelector;

import junit.framework.TestCase;

/**
 * Test case for the {@link BaseFlowServiceLocator}.
 * 
 * @author Erwin Vervaet
 */
public class BaseFlowServiceLocatorTests extends TestCase {
	
	public void testWithCustomConversionService() {
		BaseFlowServiceLocator serviceLocator = new BaseFlowServiceLocator();
		
		GenericConversionService conversionService = new GenericConversionService();
		conversionService.addConverter(new TextToBoolean("ja", "nee"));
		conversionService.addConverter(new CustomTextToViewSelector(serviceLocator));
		
		serviceLocator.setConversionService(conversionService);
		
		assertEquals(Boolean.TRUE, serviceLocator.getConversionService().getConversionExecutor(
				String.class, Boolean.class).execute("ja"));
		assertSame(NullViewSelector.INSTANCE, serviceLocator.getConversionService().getConversionExecutor(
				String.class, ViewSelector.class).execute("custom:"));
	}
	
	public static class CustomTextToViewSelector extends TextToViewSelector {

		public CustomTextToViewSelector(FlowServiceLocator flowServiceLocator) {
			super(flowServiceLocator);
		}

		protected ViewSelector convertEncodedViewSelector(String encodedView) {
			return NullViewSelector.INSTANCE;
		}
	}
}
