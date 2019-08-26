package org.springframework.binding.message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.Serializable;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticMessageSource;

public class DefaultMessageContextTests {
	private DefaultMessageContext context;

	@BeforeEach
	public void setUp() {
		StaticMessageSource messageSource = new StaticMessageSource();
		messageSource.addMessage("message", Locale.getDefault(), "Hello world resolved!");
		messageSource.addMessage("argmessage", Locale.getDefault(), "Hello world {0}!");
		context = new DefaultMessageContext(messageSource);
	}

	@Test
	public void testCreateMessageContext() {
		context.addMessage(new MessageBuilder().defaultText("Hello world!").build());
		Message[] messages = context.getAllMessages();
		assertEquals(1, messages.length);
		assertEquals("Hello world!", messages[0].getText());
		assertEquals(Severity.INFO, messages[0].getSeverity());
		assertEquals(null, messages[0].getSource());
	}

	@Test
	public void testResolveMessage() {
		context.addMessage(new MessageBuilder().warning().source(this).code("message").build());
		Message[] messages = context.getMessagesBySource(this);
		assertEquals(1, messages.length);
		assertEquals("Hello world resolved!", messages[0].getText());
		assertEquals(Severity.WARNING, messages[0].getSeverity());
		assertEquals(this, messages[0].getSource());
	}

	@Test
	public void testResolveMessageDefaultText() {
		context.addMessage(new MessageBuilder().error().code("bogus").defaultText("Hello world fallback!").build());
		Message[] messages = context.getAllMessages();
		assertEquals(1, messages.length);
		assertEquals("Hello world fallback!", messages[0].getText());
		assertEquals(Severity.ERROR, messages[0].getSeverity());
		assertEquals(null, messages[0].getSource());
	}

	@Test
	public void testResolveMessageWithArgs() {
		context.addMessage(new MessageBuilder().error().source(this).code("argmessage").arg("Keith")
				.defaultText("Hello world fallback!").build());
		Message[] messages = context.getAllMessages();
		assertEquals(1, messages.length);
		assertEquals("Hello world Keith!", messages[0].getText());
		assertEquals(Severity.ERROR, messages[0].getSeverity());
		assertEquals(this, messages[0].getSource());
	}

	@Test
	public void testResolveMessageWithMultipleCodes() {
		context.addMessage(new MessageBuilder().error().source(this).code("bogus").code("argmessage").arg("Keith")
				.defaultText("Hello world fallback!").build());
		Message[] messages = context.getMessagesBySource(this);
		assertEquals(1, messages.length);
		assertEquals("Hello world Keith!", messages[0].getText());
		assertEquals(Severity.ERROR, messages[0].getSeverity());
		assertEquals(this, messages[0].getSource());
	}

	@Test
	public void testSaveRestoreMessages() {
		context.addMessage(new MessageBuilder().defaultText("Info").build());
		context.addMessage(new MessageBuilder().error().defaultText("Error").build());
		context.addMessage(new MessageBuilder().warning().source(this).code("message").build());
		assertEquals(2, context.getMessagesBySource(null).length);
		assertEquals(1, context.getMessagesBySource(this).length);
		StateManageableMessageContext manageable = context;
		Serializable messages = manageable.createMessagesMemento();
		context = new DefaultMessageContext(context.getMessageSource());
		assertEquals(0, context.getAllMessages().length);
		manageable = context;
		manageable.restoreMessages(messages);
		assertEquals(2, context.getMessagesBySource(null).length);
		assertEquals(1, context.getMessagesBySource(this).length);
	}

	@Test
	public void testMessageSequencing() {
		context.addMessage(new MessageBuilder().defaultText("Info").build());
		context.addMessage(new MessageBuilder().warning().source(this).code("message").build());
		context.addMessage(new MessageBuilder().error().defaultText("Error").build());
		Message[] messages = context.getAllMessages();
		assertEquals("Info", messages[0].getText());
		assertEquals("Error", messages[1].getText());
		assertEquals("Hello world resolved!", messages[2].getText());
	}
}
