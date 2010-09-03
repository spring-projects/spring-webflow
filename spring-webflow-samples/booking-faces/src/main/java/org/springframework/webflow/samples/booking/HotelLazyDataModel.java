/**
 * 
 */
package org.springframework.webflow.samples.booking;

import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;

public class HotelLazyDataModel extends LazyDataModel<Hotel> {

    private static final long serialVersionUID = -8832831134966938627L;

    SearchCriteria searchCriteria;

    BookingService bookingService;

    private Hotel selected;

    public HotelLazyDataModel(SearchCriteria searchCriteria, BookingService bookingService) {
	this.searchCriteria = searchCriteria;
	this.bookingService = bookingService;
    }

    @Override
    public List<Hotel> load(int first, int pageSize, String sortField, boolean sortOrder, Map<String, String> filters) {
	searchCriteria.setCurrentPage(first / pageSize + 1);
	return bookingService.findHotels(searchCriteria, first, sortField, sortOrder);
    }

    @Override
    public int getRowCount() {
	return bookingService.getNumberOfHotels(searchCriteria);
    }

    public Hotel getSelected() {
	return selected;
    }

    public void setSelected(Hotel selected) {
	this.selected = selected;
    }

}