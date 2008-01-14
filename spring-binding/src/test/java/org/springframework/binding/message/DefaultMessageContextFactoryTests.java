package org.springframework.binding.message;

import java.io.Serializable;
import java.util.Locale;

import junit.framework.TestCase;

import org.springframework.context.support.StaticMessageSource;

public class DefaultMessageContextFactoryTests extends TestCase {
	private DefaultMessageContextFactory factory;

	protected void setUp() {
		StaticMessageSource messageSource = new StaticMessageSource();
		factory = new DefaultMessageContextFactory(messageSource);
		messageSource.addMessage("message", Locale.getDefault(), "Hello world resolved!");
		messageSource.addMessage("argmessage", Locale.getDefault(), "Hello world {0}!");
	}

	public void testCreateMessageContext() {
		MessageContext context = factory.createMessageContext();
		context.addMessage(new MessageBuilder().defaultText("Hello world!").build());
		Message[] messages = context.getMessages();
		assertEquals(1, messages.length);
		assertEquals("Hello world!", messages[0].getText());
		assertEquals(Severity.INFO, messages[0].getSeverity());
		assertEquals(null, messages[0].getSource());
	}

	public void testResolveMessage() {
		MessageContext context = factory.createMessageContext();
		context.addMessage(new MessageBuilder().warning().source(this).code("message").build());
		Message[] messages = context.getMessages(this);
		assertEquals(1, messages.length);
		assertEquals("Hello world resolved!", messages[0].getText());
		assertEquals(Severity.WARNING, messages[0].getSeverity());
		assertEquals(this, messages[0].getSource());
	}

	public void testResolveMessageDefaultText() {
		MessageContext context = factory.createMessageContext();
		context.addMessage(new MessageBuilder().error().code("bogus").defaultText("Hello world fallback!").build());
		Message[] messages = context.getMessages(null);
		assertEquals(1, messages.length);
		assertEquals("Hello world fallback!", messages[0].getText());
		assertEquals(Severity.ERROR, messages[0].getSeverity());
		assertEquals(null, messages[0].getSource());
		assertTrue(context instanceof StateManageableMessageContext);
	}

	public void testResolveMessageWithArgs() {
		MessageContext context = factory.createMessageContext();
		context.addMessage(new MessageBuilder().error().source(this).code("argmessage").arg("Keith").defaultText(
				"Hello world fallback!").build());
		Message[] messages = context.getMessages(this);
		assertEquals(1, messages.length);
		assertEquals("Hello world Keith!", messages[0].getText());
		assertEquals(Severity.ERROR, messages[0].getSeverity());
		assertEquals(this, messages[0].getSource());
		assertTrue(context instanceof StateManageableMessageContext);
	}

	public void testResolveMessageWithMultipleCodes() {
		MessageContext context = factory.createMessageContext();
		context.addMessage(new MessageBuilder().error().source(this).codes(new String[] { "bogus", "argmessage" })
				.args(new Object[] { "Keith" }).defaultText("Hello world fallback!").build());
		Message[] messages = context.getMessages(this);
		assertEquals(1, messages.length);
		assertEquals("Hello world Keith!", messages[0].getText());
		assertEquals(Severity.ERROR, messages[0].getSeverity());
		assertEquals(this, messages[0].getSource());
		assertTrue(context instanceof StateManageableMessageContext);
	}

	public void testSaveRestoreMessages() {
		MessageContext context = factory.createMessageContext();
		context.addMessage(new MessageBuilder().defaultText("Info").build());
		context.addMessage(new MessageBuilder().error().defaultText("Error").build());
		context.addMessage(new MessageBuilder().warning().source(this).code("message").build());
		assertEquals(2, context.getMessages(null).length);
		assertEquals(1, context.getMessages(this).length);
		assertTrue(context instanceof StateManageableMessageContext);
		StateManageableMessageContext manageable = (StateManageableMessageContext) context;
		Serializable messages = manageable.createMessagesMemento();
		context = factory.createMessageContext();
		assertEquals(0, context.getMessages().length);
		manageable = (StateManageableMessageContext) context;
		manageable.restoreMessages(messages);
		assertEquals(2, context.getMessages(null).length);
		assertEquals(1, context.getMessages(this).length);
	}
}
