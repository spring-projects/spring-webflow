package org.springframework.webflow.samples.booking;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.DataModel;

import org.easymock.EasyMock;
import org.springframework.faces.model.converter.FacesConversionService;
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;
import org.springframework.webflow.test.execution.AbstractXmlFlowExecutionTests;

public class MainFlowExecutionTests extends AbstractXmlFlowExecutionTests {

    private BookingService bookingService;

    protected void setUp() {
	bookingService = EasyMock.createMock(BookingService.class);
    }

    @Override
    protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
	return resourceFactory.createFileResource("src/main/webapp/WEB-INF/flows/main/main.xml");
    }

    @Override
    protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
	builderContext.registerBean("bookingService", bookingService);
	builderContext.getFlowBuilderServices().setConversionService(new FacesConversionService());
    }

    public void testStart() {
	List<Booking> bookings = new ArrayList<Booking>();
	bookings.add(new Booking(new Hotel(), new User("keith", "password", "Keith Donald")));
	EasyMock.expect(bookingService.findBookings("keith")).andReturn(bookings);
	EasyMock.replay(bookingService);

	MockExternalContext context = new MockExternalContext();
	context.setCurrentUser("keith");
	startFlow(context);
	assertCurrentStateEquals("enterSearchCriteria");
	assertTrue(getRequiredFlowAttribute("searchCriteria") instanceof SearchCriteria);
	assertTrue(getRequiredViewAttribute("bookings") instanceof DataModel);

	EasyMock.verify(bookingService);
    }

    // public void testSearch() {
    // setCurrentState("enterSearchCriteria");
    // SearchCriteria criteria = new SearchCriteria();
    // criteria.setSearchString("Jameson");
    // getFlowScope().put("searchCriteria", criteria);
    //
    // List<Hotel> hotels = new ArrayList<Hotel>();
    // hotels.add(new Hotel());
    // EasyMock.expect(bookingService.findHotels(criteria)).andReturn(hotels);
    // EasyMock.replay(bookingService);
    //
    // MockExternalContext context = new MockExternalContext();
    // context.setEventId("search");
    // resumeFlow(context);
    //
    // EasyMock.verify(bookingService);
    //
    // assertCurrentStateEquals("reviewHotels");
    // assertTrue(getRequiredViewAttribute("hotels") instanceof DataModel);
    // }

}
