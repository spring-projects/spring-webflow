package org.springframework.faces.webflow;

import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.springframework.binding.message.DefaultMessageContext;
import org.springframework.binding.message.Message;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.message.MessageCriteria;
import org.springframework.binding.message.MessageResolver;
import org.springframework.binding.message.Severity;
import org.springframework.webflow.execution.RequestContext;

public class FlowFacesContextTests extends TestCase {

	JSFMockHelper jsf = new JSFMockHelper();

	FacesContext facesContext;

	RequestContext requestContext = (RequestContext) EasyMock.createMock(RequestContext.class);

	MessageContext messageContext;

	MessageContext prepopulatedMessageContext;

	protected void setUp() throws Exception {
		jsf.setUp();
		facesContext = new FlowFacesContext(requestContext, jsf.facesContext());

		setupMessageContext();
	}

	protected void tearDown() throws Exception {
		jsf.tearDown();
	}

	public final void testCurrentInstance() {
		assertSame(FacesContext.getCurrentInstance(), facesContext);
	}

	public final void testAddMessage() {
		messageContext = new DefaultMessageContext();
		EasyMock.expect(requestContext.getMessageContext()).andStubReturn(messageContext);
		EasyMock.replay(new Object[] { requestContext });

		facesContext.addMessage("foo", new FacesMessage(FacesMessage.SEVERITY_INFO, "foo", "bar"));

		assertEquals("Message count is incorrect", 2, messageContext.getAllMessages().length);
		Message summaryMessage = messageContext.getMessagesBySource("foo_summary")[0];
		assertEquals("foo", summaryMessage.getText());
		Message detailMessage = messageContext.getMessagesBySource("foo_detail")[0];
		assertEquals("bar", detailMessage.getText());

	}

	public final void testAddMessages_MultipleNullIds() {
		messageContext = new DefaultMessageContext();
		EasyMock.expect(requestContext.getMessageContext()).andStubReturn(messageContext);
		EasyMock.replay(new Object[] { requestContext });

		facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "foo", "bar"));
		facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "zoo", "zar"));

		assertEquals("Message count is incorrect", 4, messageContext.getAllMessages().length);
		Message summaryMessage1 = messageContext.getMessagesBySource("null_summary")[0];
		assertEquals("foo", summaryMessage1.getText());
		Message detailMessage1 = messageContext.getMessagesBySource("null_detail")[0];
		assertEquals("bar", detailMessage1.getText());
		Message summaryMessage2 = messageContext.getMessagesBySource("null_summary")[1];
		assertEquals("zoo", summaryMessage2.getText());
		Message detailMessage2 = messageContext.getMessagesBySource("null_detail")[1];
		assertEquals("zar", detailMessage2.getText());
	}

	public final void testGetMessages() {
		messageContext = prepopulatedMessageContext;
		EasyMock.expect(requestContext.getMessageContext()).andStubReturn(messageContext);
		EasyMock.replay(new Object[] { requestContext });

		int iterationCount = 0;
		Iterator i = facesContext.getMessages();
		while (i.hasNext()) {
			assertNotNull(i.next());
			iterationCount++;
		}
		assertEquals("There should be 5 messages to iterate", 5, iterationCount);
	}

	public final void testGetMessagesByClientId_ForComponent() {
		messageContext = prepopulatedMessageContext;
		EasyMock.expect(requestContext.getMessageContext()).andStubReturn(messageContext);
		EasyMock.replay(new Object[] { requestContext });

		int iterationCount = 0;
		Iterator i = facesContext.getMessages("componentId");
		while (i.hasNext()) {
			FacesMessage message = (FacesMessage) i.next();
			assertNotNull(message);
			assertEquals("componentId_summary", message.getSummary());
			assertEquals("componentId_detail", message.getDetail());
			iterationCount++;
		}
		assertEquals(1, iterationCount);
	}

	public final void testGetMessagesByClientId_ForUserMessage() {
		messageContext = prepopulatedMessageContext;
		EasyMock.expect(requestContext.getMessageContext()).andStubReturn(messageContext);
		EasyMock.replay(new Object[] { requestContext });

		int iterationCount = 0;
		Iterator i = facesContext.getMessages("userMessage");
		while (i.hasNext()) {
			FacesMessage message = (FacesMessage) i.next();
			assertNotNull(message);
			assertEquals("userMessage", message.getSummary());
			assertEquals("userMessage", message.getDetail());
			iterationCount++;
		}
		assertEquals(1, iterationCount);
	}

	public final void testgetMessagesByClientId_InvalidId() {
		messageContext = prepopulatedMessageContext;
		EasyMock.expect(requestContext.getMessageContext()).andStubReturn(messageContext);
		EasyMock.replay(new Object[] { requestContext });

		int iterationCount = 0;
		Iterator i = facesContext.getMessages("unknown");
		while (i.hasNext()) {
			iterationCount++;
		}
		assertEquals(0, iterationCount);
	}

	public final void testGetClientIdsWithMessages() {
		messageContext = prepopulatedMessageContext;
		EasyMock.expect(requestContext.getMessageContext()).andStubReturn(messageContext);
		EasyMock.replay(new Object[] { requestContext });

		int iterationCount = 0;
		Iterator i = facesContext.getClientIdsWithMessages();
		while (i.hasNext()) {
			i.next();
			iterationCount++;
		}
		assertEquals(2, iterationCount);
	}

	public final void testGetMaximumSeverity() {
		messageContext = prepopulatedMessageContext;
		EasyMock.expect(requestContext.getMessageContext()).andStubReturn(messageContext);
		EasyMock.replay(new Object[] { requestContext });

		assertEquals(FacesMessage.SEVERITY_FATAL, facesContext.getMaximumSeverity());
	}

	private void setupMessageContext() {
		prepopulatedMessageContext = new DefaultMessageContext();
		prepopulatedMessageContext.addMessage(new MessageBuilder().source("null_summary").defaultText("foo").info()
				.build());
		prepopulatedMessageContext.addMessage(new MessageBuilder().source("null_detail").defaultText("foo").info()
				.build());
		prepopulatedMessageContext.addMessage(new MessageBuilder().source("componentId_summary").defaultText(
				"componentId_summary").warning().build());
		prepopulatedMessageContext.addMessage(new MessageBuilder().source("componentId_detail").defaultText(
				"componentId_detail").warning().build());
		prepopulatedMessageContext.addMessage(new MessageBuilder().source("userMessage").defaultText("userMessage")
				.info().build());
		prepopulatedMessageContext.addMessage(new MessageBuilder().source("null_summary").defaultText("baz").error()
				.build());
		prepopulatedMessageContext.addMessage(new MessageBuilder().source("null_detail").defaultText("baz").error()
				.build());
		prepopulatedMessageContext.addMessage(new MessageBuilder().defaultText("Subzero Wins - Fatality").fatal()
				.build());
	}

	private class TestGetMessagesContext implements MessageContext {

		Message[] messages;

		TestGetMessagesContext() {
			messages = new Message[7];
			messages[0] = new Message("null_summary", "foo", Severity.INFO);
			messages[1] = new Message("null_detail", "foo", Severity.INFO);
			messages[2] = new Message("componentId_summary", "bar", Severity.WARNING);
			messages[3] = new Message("componentId_detail", "bar", Severity.WARNING);
			messages[4] = new Message("userMessage", "userMessage", Severity.INFO);
			messages[5] = new Message("null_summary", "baz", Severity.ERROR);
			messages[6] = new Message("null_detail", "baz", Severity.ERROR);
		}

		public void addMessage(MessageResolver messageResolver) {

		}

		public Message[] getAllMessages() {
			return messages;
		}

		public Message[] getMessagesBySource(Object source) {
			return new Message[] { messages[1] };
		}

		public Message[] getMessagesByCriteria(MessageCriteria criteria) {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public boolean hasErrorMessages() {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public void clearMessages() {
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

	}

}
