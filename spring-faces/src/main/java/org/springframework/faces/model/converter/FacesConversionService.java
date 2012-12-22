/*
 * Copyright 2004-2008 the original author or authimport org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.support.DefaultConversionService;
import org.springframework.faces.model.OneSelectionTrackingListDataModel;
e.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.faces.model.converter;

import java.util.Arrays;
import java.util.List;

import javax.faces.model.DataModel;

import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.ConversionExecutorNotFoundException;
import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.binding.convert.service.SpringBindingConverterAdapter;
import org.springframework.binding.convert.service.StaticConversionExecutor;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.faces.model.OneSelectionTrackingListDataModel;

/**
 * Convenient {@link org.springframework.binding.convert.ConversionService} implementation for JSF that composes
 * JSF-specific converters with the standard Web Flow converters.
 *
 * <p>
 * In addition to the standard Web Flow conversion, this service provide conversion from a list into a
 * {@link OneSelectionTrackingListDataModel} using a "dataModel" alias for the type.
 * </p>
 *
 * @author Jeremy Grelle
 */
public class FacesConversionService extends DefaultConversionService {

	/**
	 * Detects if the version of Spring has issue SPR-10116
	 */
	private static final boolean SPR_10116;
	static {
		org.springframework.core.convert.support.DefaultConversionService conversionService = new org.springframework.core.convert.support.DefaultConversionService();
		conversionService.addConverter(new SpringBindingConverterAdapter(new DataModelConverter()));
		boolean failedConversion = false;
		try {
			conversionService.convert(Arrays.asList(""), DataModel.class);
		} catch (ConversionFailedException e) {
			failedConversion = true;
		}
		SPR_10116 = failedConversion;
	}

	public FacesConversionService() {
		addFacesConverters();
	}

	public FacesConversionService(ConversionService delegateConversionService) {
		super(delegateConversionService);
	}

	protected void addFacesConverters() {
		addConverter(new DataModelConverter());
		addAlias("dataModel", DataModel.class);
	}

	public ConversionExecutor getConversionExecutor(Class<?> sourceClass, Class<?> targetClass)
			throws ConversionExecutorNotFoundException {
		ConversionExecutor executor = super.getConversionExecutor(sourceClass, targetClass);
		if(SPR_10116 && List.class.isAssignableFrom(sourceClass) && (DataModel.class.isAssignableFrom(targetClass))) {
			// Temporary work around for SPR-10116. This will be removed when Spring 3.2.1 is released
			return new StaticConversionExecutor(List.class, DataModel.class, new DataModelConverter());
		}
		return executor;
	}
}
