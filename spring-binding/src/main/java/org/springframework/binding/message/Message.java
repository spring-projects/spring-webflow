/*
 * Copyright 2004-2008 the original author oimport java.io.Serializable;

import org.springframework.core.style.ToStringCreator;
ou may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.binding.message;

import java.io.Serializable;

import org.springframework.core.style.ToStringCreator;
import org.springframework.util.StringUtils;

/**
 * An object of communication that provides text information. For example, a validation message may inform a web
 * application user a business rule was violated. A message can be associated with a particular source element or
 * component, has text providing the basis for communication, and has severity indicating the priority or intensity of
 * the message for its receiver.
 * 
 * @author Keith Donald
 */
public class Message implements Serializable {

	private Object source;

	private String text;

	private Severity severity;

	/**
	 * Creates a new message.
	 * @param source the source of the message
	 * @param text the message text
	 * @param severity the message severity
	 */
	public Message(Object source, String text, Severity severity) {
		this.source = source;
		this.text = text;
		this.severity = severity;
	}

	/**
	 * A reference to the source element this message is associated with. This could be a field on a form in UI, or null
	 * (or empty "" in the case of global bean validation) if the message is not associated with a any particular
	 * element.
	 * @return the source
	 */
	public Object getSource() {
		return source;
	}

	/**
	 * The message text. The text is the message's communication payload.
	 * @return the message text
	 */
	public String getText() {
		return text;
	}

	/**
	 * The severity of this message. The severity indicates the intensity or priority of the communication.
	 * @return the message severity
	 */
	public Severity getSeverity() {
		return severity;
	}

	/**
	 * Whether the message is associated with a field.
	 * @return {@code true} if the source is a String that has text; {@code false} otherwise.
	 */
	public boolean hasField() {
		if (this.source instanceof String) {
			return StringUtils.hasText((String) this.source);
		} else {
			return false;
		}
	}

	public String toString() {
		return new ToStringCreator(this).append("source", source).append("severity", severity).append("text", text)
				.toString();
	}

}
