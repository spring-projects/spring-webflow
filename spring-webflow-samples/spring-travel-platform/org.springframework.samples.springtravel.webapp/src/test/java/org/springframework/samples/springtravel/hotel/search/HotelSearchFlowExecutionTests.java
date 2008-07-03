package org.springframework.samples.springtravel.hotel.search;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.DataModel;

import org.easymock.EasyMock;
import org.springframework.binding.mapping.Mapper;
import org.springframework.binding.mapping.MappingResults;
import org.springframework.faces.model.OneSelectionTrackingListDataModel;
import org.springframework.faces.model.converter.FacesConversionService;
import org.springframework.samples.springtravel.hotel.booking.HotelBooking;
import org.springframework.samples.springtravel.hotel.booking.HotelBookingAgent;
import org.springframework.samples.springtravel.hotel.booking.User;
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.engine.EndState;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;
import org.springframework.webflow.test.execution.AbstractXmlFlowExecutionTests;

public class HotelSearchFlowExecutionTests extends AbstractXmlFlowExecutionTests {
	
	private HotelSearchAgent hotelSearchAgent;
	private HotelBookingAgent hotelBookingAgent;
	
	protected void setUp() {
		hotelSearchAgent = EasyMock.createMock(HotelSearchAgent.class);
		hotelBookingAgent = EasyMock.createMock(HotelBookingAgent.class);
	}
	
	@Override
	protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
		return resourceFactory.createFileResource("src/main/webapp/WEB-INF/hotel/search/search.xml");
	}
	
	@Override
	protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
		builderContext.registerBean("hotelSearchAgent", hotelSearchAgent);
		builderContext.registerBean("hotelBookingAgent", hotelBookingAgent);
		builderContext.getFlowBuilderServices().setConversionService(new FacesConversionService());
	}
	
	public void testStartMainFlow() {
		List<HotelBooking> bookings = new ArrayList<HotelBooking>();
		bookings.add(new HotelBooking(new Hotel(), new User("keith", "password", "Keith Donald")));
		EasyMock.expect(hotelBookingAgent.findBookings("keith")).andReturn(bookings);
		EasyMock.replay(hotelBookingAgent);
		
		MockExternalContext context = new MockExternalContext();
		context.setCurrentUser("keith");
		startFlow(context);
		assertCurrentStateEquals("enterSearchCriteria");
		assertResponseWrittenEquals("enterSearchCriteria", context);
		assertTrue(getRequiredFlowAttribute("searchCriteria") instanceof SearchCriteria);
		assertTrue(getRequiredViewAttribute("bookings") instanceof DataModel);
		
		EasyMock.verify(hotelBookingAgent);
	}

	public void testSearchHotels() {
		setCurrentState("enterSearchCriteria");
		
		SearchCriteria criteria = new SearchCriteria();
		criteria.setSearchString("Jameson");
		getFlowScope().put("searchCriteria", criteria);
		
		List<Hotel> hotels = new ArrayList<Hotel>();
		hotels.add(new Hotel());
		EasyMock.expect(hotelSearchAgent.findHotels(criteria)).andReturn(hotels);
		EasyMock.replay(hotelSearchAgent);
		
		MockExternalContext context = new MockExternalContext();
		context.setEventId("search");
		resumeFlow(context);
		
		EasyMock.verify(hotelSearchAgent);
		
		assertCurrentStateEquals("reviewHotels");
		assertResponseWrittenEquals("reviewHotels", context);
		assertTrue(getRequiredViewAttribute("hotels") instanceof DataModel);
	}

	public void testSelectHotel() {
		setCurrentState("reviewHotels");
		
		List<Hotel> hotels = new ArrayList<Hotel>();
		Hotel hotel = new Hotel();
		hotel.setId(1L);
		hotel.setName("Jameson Inn");
		hotels.add(hotel);
		OneSelectionTrackingListDataModel dataModel = new OneSelectionTrackingListDataModel(hotels);
		dataModel.select(hotel);
		getViewScope().put("hotels", dataModel);
		
		MockExternalContext context = new MockExternalContext();
		context.setEventId("select");
		resumeFlow(context);
		
		assertCurrentStateEquals("reviewHotel");
		assertNull(getFlowAttribute("hotels"));
		assertSame(hotel, getFlowAttribute("hotel"));
	}

	public void testBookHotel() {
		setCurrentState("reviewHotel");
		
		Hotel hotel = new Hotel();
		hotel.setId(1L);
		hotel.setName("Jameson Inn");
		getFlowScope().put("hotel", hotel);
		
		Flow mockBookingFlow = new Flow("booking");
		mockBookingFlow.setInputMapper(new Mapper() {
			public MappingResults map(Object source, Object target) {
				assertEquals(new Long(1), ((AttributeMap) source).get("hotelId"));
				return null;
			}
		});
		new EndState(mockBookingFlow, "bookingConfirmed");
		getFlowDefinitionRegistry().registerFlowDefinition(mockBookingFlow);
		
		MockExternalContext context = new MockExternalContext();
		context.setEventId("book");
		resumeFlow(context);
		
		assertFlowExecutionEnded();
		assertFlowExecutionOutcomeEquals("finish");
    }
    
}
