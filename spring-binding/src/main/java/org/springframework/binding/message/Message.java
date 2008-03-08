package org.springframework.binding.message;

import java.io.Serializable;

import org.springframework.core.style.ToStringCreator;

/**
 * An object of communication that provides text information from a source. For example, a validation message may inform
 * a web application user a business rule was violated. A messages comes from a source, has text providing the basis for
 * communication, and has severity indicating the priority or intensity of the message for its receiver.
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
	 * Returns the source of this message. The source is the object that sent the message.
	 * @return the source
	 */
	public Object getSource() {
		return source;
	}

	/**
	 * Returns the message text. The text is the message's communication payload.
	 * @return the message text
	 */
	public String getText() {
		return text;
	}

	/**
	 * Returns the severity of this message. The severity indicates the intensity or priority of the communication.
	 * @return the message severity
	 */
	public Severity getSeverity() {
		return severity;
	}

	public String toString() {
		return new ToStringCreator(this).append("source", source).append("severity", severity).append("text", text)
				.toString();
	}

}
