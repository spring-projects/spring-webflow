/*
 * Copyright 2004-2008 the original authorimport javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.faces.support;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * {@link PhaseListener} that logs the execution of the individual phases of the JSF lifecycle. Useful during JSF
 * application development in order to detect unreported JSF errors that cause the lifecycle to short-circuit. Turn
 * logging level to DEBUG to see its output.
 * 
 * @author Jeremy Grelle
 */
public class RequestLoggingPhaseListener implements PhaseListener {

	private Log logger = LogFactory.getLog(RequestLoggingPhaseListener.class);

	public void afterPhase(PhaseEvent event) {
		// no-op
	}

	public void beforePhase(PhaseEvent event) {
		if (logger.isDebugEnabled()) {
			logger.debug("Entering JSF Phase: " + event.getPhaseId());
		}
	}

	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}

}
