package org.springframework.faces.webflow;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.springframework.binding.message.DefaultMessageContext;
import org.springframework.binding.message.Message;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.faces.webflow.FlowFacesContext.FacesMessageSource;
import org.springframework.webflow.execution.RequestContext;

public class FlowFacesContextTests extends TestCase {

	JSFMockHelper jsf = new JSFMockHelper();

	FacesContext facesContext;

	RequestContext requestContext;

	MessageContext messageContext;

	MessageContext prepopulatedMessageContext;

	@SuppressWarnings("cast")
	protected void setUp() throws Exception {
		this.jsf.setUp();
		this.requestContext = (RequestContext) EasyMock.createMock(RequestContext.class);
		this.facesContext = new FlowFacesContext(this.requestContext, this.jsf.facesContext());
		setupMessageContext();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		this.jsf.tearDown();
	}

	public final void testCurrentInstance() {
		assertSame(FacesContext.getCurrentInstance(), this.facesContext);
	}

	public final void testAddMessage() {
		this.messageContext = new DefaultMessageContext();
		EasyMock.expect(this.requestContext.getMessageContext()).andStubReturn(this.messageContext);
		EasyMock.replay(this.requestContext);

		this.facesContext.addMessage("foo", new FacesMessage(FacesMessage.SEVERITY_INFO, "foo", "bar"));

		assertEquals("Message count is incorrect", 1, this.messageContext.getAllMessages().length);
		Message message = this.messageContext.getMessagesBySource(new FacesMessageSource("foo"))[0];
		assertEquals("foo : bar", message.getText());
	}

	public final void testGetGlobalMessagesOnly() {
		this.messageContext = new DefaultMessageContext();
		EasyMock.expect(this.requestContext.getMessageContext()).andStubReturn(this.messageContext);
		EasyMock.replay(this.requestContext);

		this.facesContext.addMessage("foo", new FacesMessage(FacesMessage.SEVERITY_INFO, "foo", "bar"));
		this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "FOO", "BAR"));

		int iterationCount = 0;
		Iterator<FacesMessage> i = this.facesContext.getMessages(null);
		while (i.hasNext()) {
			FacesMessage message = i.next();
			assertNotNull(message);
			iterationCount++;
		}
		assertEquals(1, iterationCount);
	}

	public final void testGetAllMessages() {
		this.messageContext = new DefaultMessageContext();
		EasyMock.expect(this.requestContext.getMessageContext()).andStubReturn(this.messageContext);
		EasyMock.replay(this.requestContext);

		this.facesContext.addMessage("foo", new FacesMessage(FacesMessage.SEVERITY_INFO, "foo", "bar"));
		this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "FOO", "BAR"));

		int iterationCount = 0;
		Iterator<FacesMessage> i = this.facesContext.getMessages();
		while (i.hasNext()) {
			FacesMessage message = i.next();
			assertNotNull(message);
			iterationCount++;
		}
		assertEquals(2, iterationCount);
	}

	public final void testAddMessages_MultipleNullIds() {
		this.messageContext = new DefaultMessageContext();
		EasyMock.expect(this.requestContext.getMessageContext()).andStubReturn(this.messageContext);
		EasyMock.replay(this.requestContext);

		this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "foo", "bar"));
		this.facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "zoo", "zar"));

		assertEquals("Message count is incorrect", 2, this.messageContext.getAllMessages().length);
		Message[] messages = this.messageContext.getMessagesBySource(new FacesMessageSource(null));
		assertEquals("foo : bar", messages[0].getText());
		assertEquals("zoo : zar", messages[1].getText());
	}

	public final void testGetMessages() {
		this.messageContext = this.prepopulatedMessageContext;
		EasyMock.expect(this.requestContext.getMessageContext()).andStubReturn(this.messageContext);
		EasyMock.replay(this.requestContext);

		int iterationCount = 0;
		Iterator<FacesMessage> i = this.facesContext.getMessages();
		while (i.hasNext()) {
			assertNotNull(i.next());
			iterationCount++;
		}
		assertEquals("There should be 6 messages to iterate", 6, iterationCount);
	}

	public final void testMutableGetMessages() {
		this.messageContext = this.prepopulatedMessageContext;
		EasyMock.expect(this.requestContext.getMessageContext()).andStubReturn(this.messageContext);
		EasyMock.replay(this.requestContext);

		this.facesContext.addMessage("TESTID", new FacesMessage("summary1"));
		FacesMessage soruceMessage = this.facesContext.getMessages("TESTID").next();
		soruceMessage.setSummary("summary2");

		// check that message sticks around even when the facesContext has been torn down and re-created during the
		// processing of the current request
		FacesContext newFacesContext = new FlowFacesContext(this.requestContext, this.jsf.facesContext());
		assertSame(FacesContext.getCurrentInstance(), newFacesContext);

		FacesMessage gotMessage = newFacesContext.getMessages("TESTID").next();
		assertEquals("summary2", gotMessage.getSummary());
	}

	public final void testGetMessagesByClientId_ForComponent() {
		this.messageContext = this.prepopulatedMessageContext;
		EasyMock.expect(this.requestContext.getMessageContext()).andStubReturn(this.messageContext);
		EasyMock.replay(this.requestContext);

		int iterationCount = 0;
		Iterator<FacesMessage> i = this.facesContext.getMessages("componentId");
		while (i.hasNext()) {
			FacesMessage message = i.next();
			assertNotNull(message);
			assertEquals("componentId_summary" + (iterationCount + 1), message.getSummary());
			assertEquals("componentId_detail" + (iterationCount + 1), message.getDetail());
			iterationCount++;
		}
		assertEquals(2, iterationCount);
	}

	public final void testGetMessagesByClientId_ForUserMessage() {
		this.messageContext = this.prepopulatedMessageContext;
		EasyMock.expect(this.requestContext.getMessageContext()).andStubReturn(this.messageContext);
		EasyMock.replay(this.requestContext);

		int iterationCount = 0;
		Iterator<FacesMessage> i = this.facesContext.getMessages("userMessage");
		while (i.hasNext()) {
			FacesMessage message = i.next();
			assertNotNull(message);
			assertEquals("userMessage", message.getSummary());
			assertEquals("userMessage", message.getDetail());
			iterationCount++;
		}
		assertEquals(1, iterationCount);
	}

	public final void testgetMessagesByClientId_InvalidId() {
		this.messageContext = this.prepopulatedMessageContext;
		EasyMock.expect(this.requestContext.getMessageContext()).andStubReturn(this.messageContext);
		EasyMock.replay(this.requestContext);

		Iterator<FacesMessage> i = this.facesContext.getMessages("unknown");
		assertFalse(i.hasNext());
	}

	public final void testGetClientIdsWithMessages() {
		this.messageContext = this.prepopulatedMessageContext;
		EasyMock.expect(this.requestContext.getMessageContext()).andStubReturn(this.messageContext);
		EasyMock.replay(this.requestContext);

		List<String> expectedOrderedIds = new ArrayList<>();
		expectedOrderedIds.add(null);
		expectedOrderedIds.add("componentId");
		expectedOrderedIds.add("userMessage");

		int iterationCount = 0;
		Iterator<String> i = this.facesContext.getClientIdsWithMessages();
		while (i.hasNext()) {
			String clientId = i.next();
			assertEquals("Client id not expected", expectedOrderedIds.get(iterationCount), clientId);
			iterationCount++;
		}
		assertEquals(3, iterationCount);
	}

	public final void testMessagesAreSerializable() throws Exception {
		DefaultMessageContext messageContext = new DefaultMessageContext();
		EasyMock.expect(this.requestContext.getMessageContext()).andStubReturn(messageContext);
		EasyMock.replay(this.requestContext);

		this.facesContext.addMessage("TESTID", new FacesMessage("summary1"));
		FacesMessage sourceMessage = this.facesContext.getMessages("TESTID").next();
		sourceMessage.setSummary("summary2");
		sourceMessage.setSeverity(FacesMessage.SEVERITY_FATAL);

		Serializable mementoWrite = messageContext.createMessagesMemento();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(mementoWrite);
		oos.flush();
		byte[] byteArray = bos.toByteArray();
		oos.close();

		ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
		ObjectInputStream ois = new ObjectInputStream(bis);
		Serializable mementoRead = (Serializable) ois.readObject();
		ois.close();

		messageContext.restoreMessages(mementoRead);
		EasyMock.reset(this.requestContext);
		EasyMock.expect(this.requestContext.getMessageContext()).andStubReturn(messageContext);
		EasyMock.replay(this.requestContext);

		FacesContext newFacesContext = new FlowFacesContext(this.requestContext, this.jsf.facesContext());
		assertSame(FacesContext.getCurrentInstance(), newFacesContext);
		FacesMessage gotMessage = newFacesContext.getMessages("TESTID").next();
		assertEquals("summary2", gotMessage.getSummary());
		assertEquals(FacesMessage.SEVERITY_FATAL, gotMessage.getSeverity());
	}

	public final void testGetMaximumSeverity() {
		this.messageContext = this.prepopulatedMessageContext;
		EasyMock.expect(this.requestContext.getMessageContext()).andStubReturn(this.messageContext);
		EasyMock.replay(this.requestContext);

		assertEquals(FacesMessage.SEVERITY_FATAL, this.facesContext.getMaximumSeverity());
	}

	public final void testGetELContext() {

		assertNotNull(this.facesContext.getELContext());
		assertSame(this.facesContext, this.facesContext.getELContext().getContext(FacesContext.class));
	}

	public final void testValidationFailed() {
		this.messageContext = new DefaultMessageContext();
		EasyMock.expect(this.requestContext.getMessageContext()).andStubReturn(this.messageContext);
		EasyMock.replay(this.requestContext);

		this.facesContext.addMessage("foo", new FacesMessage(FacesMessage.SEVERITY_ERROR, "foo", "bar"));

		assertEquals(true, this.facesContext.isValidationFailed());
	}

	private void setupMessageContext() {
		this.prepopulatedMessageContext = new DefaultMessageContext();
		this.prepopulatedMessageContext.addMessage(new FlowFacesContext.FlowFacesMessage(new FacesMessageSource(null),
				new FacesMessage("foo")));
		this.prepopulatedMessageContext.addMessage(new FlowFacesContext.FlowFacesMessage(new FacesMessageSource(
				"componentId"), new FacesMessage("componentId_summary1", "componentId_detail1")));
		this.prepopulatedMessageContext.addMessage(new FlowFacesContext.FlowFacesMessage(new FacesMessageSource(
				"componentId"), new FacesMessage("componentId_summary2", "componentId_detail2")));
		this.prepopulatedMessageContext.addMessage(new FlowFacesContext.FlowFacesMessage(new FacesMessageSource(null),
				new FacesMessage("baz")));
		this.prepopulatedMessageContext.addMessage(new MessageBuilder().source("userMessage").defaultText("userMessage")
				.info().build());
		this.prepopulatedMessageContext.addMessage(new MessageBuilder().defaultText("Subzero Wins - Fatality").fatal()
				.build());
	}
}
