package org.springframework.webflow.samples.booking;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

public class HotelsController {
    private BookingService bookingService;

    public HotelsController(BookingService bookingService) {
	this.bookingService = bookingService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ModelAttribute("bookings")
    public List<Booking> index(SearchCriteria searchCriteria, Principal currentUser) {
	if (currentUser != null) {
	    return bookingService.findBookings(currentUser.getName());
	} else {
	    return null;
	}
    }

    @RequestMapping
    @ModelAttribute("hotels")
    public List<Hotel> search(SearchCriteria criteria) {
	return bookingService.findHotels(criteria);
    }

    @RequestMapping(method = RequestMethod.GET)
    public Hotel show(@RequestParam("id") Long id) {
	return bookingService.findHotelById(id);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String deleteBooking(@RequestParam("id") Long id) {
	bookingService.cancelBooking(id);
	return "redirect:index";
    }

}
