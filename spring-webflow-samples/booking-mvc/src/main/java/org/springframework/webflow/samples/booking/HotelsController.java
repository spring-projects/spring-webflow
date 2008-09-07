package org.springframework.webflow.samples.booking;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HotelsController {

    private BookingService bookingService;

    @Autowired
    public HotelsController(BookingService bookingService) {
	this.bookingService = bookingService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public void index(SearchCriteria searchCriteria, Principal currentUser, Model model) {
	if (currentUser != null) {
	    List<Booking> booking = bookingService.findBookings(currentUser.getName());
	    model.addAttribute(booking);
	}
    }

    @RequestMapping(method = RequestMethod.GET)
    public String search(SearchCriteria criteria, Model model) {
	List<Hotel> hotels = bookingService.findHotels(criteria);
	model.addAttribute(hotels);
	return "hotels/search";
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
