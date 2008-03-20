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

import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.support.DefaultConversionService;
import org.springframework.binding.convert.support.TextToClass;
import org.springframework.faces.model.OneSelectionTrackingListDataModel;

/**
 * Convenient {@link ConversionService} implementation for JSF that composes JSF-specific converters with the standard
 * Web Flow converters.
 * 
 * @author Jeremy Grelle
 */
public class FacesConversionService extends DefaultConversionService {

	public FacesConversionService() {
		addFacesConverters();
	}

	protected void addFacesConverters() {
		addConverter(new DataModelConverter());
		TextToClass classConverter = (TextToClass) getConverter(String.class, Class.class);
		classConverter.addAlias("dataModel", OneSelectionTrackingListDataModel.class);
	}
}
