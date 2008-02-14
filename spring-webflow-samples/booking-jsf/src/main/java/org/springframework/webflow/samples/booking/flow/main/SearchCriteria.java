package org.springframework.webflow.samples.booking.flow.main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.springframework.util.StringUtils;
import org.springframework.webflow.samples.booking.app.BookingService;

/**
 * A backing bean for the main hotel search form. Encapsulates the criteria needed to perform a hotel search.
 * 
 * It is expected a future milestone of Spring Web Flow 2.0 will allow flow-scoped beans like this one to hold
 * references to transient services that are restored automatically when the flow is resumed on subsequent requests.
 * This would allow this SearchCriteria object to delegate to the {@link BookingService} directly, for example,
 * eliminating the need for the actions in {@link MainActions}.
 */
public class SearchCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The user-provided search criteria for finding Hotels.
     */
    private String searchString;

    /**
     * The maximum page size of the Hotel result list
     */
    private int pageSize = 5;

    /**
     * The current page of the Hotel result list.
     */
    private int page;

    /**
     * The available page size options.
     */
    private List pageSizeOptions;

    /**
     * Increase the current page
     */
    public void nextPage() {
	page++;
    }

    /**
     * Decrease the current page
     */
    public void prevPage() {
	page--;
    }

    /**
     * Signal that a find is about to occur and reset the page size to 0.
     */
    public void findHotelsListener(ActionEvent event) {
	page = 0;
    }

    public String getSearchString() {
	return searchString;
    }

    public void setSearchString(String searchString) {
	this.searchString = searchString;
    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }

    public int getPage() {
	return page;
    }

    public void setPage(int page) {
	this.page = page;
    }

    public List getPageSizeOptions() {
	if (pageSizeOptions == null) {
	    pageSizeOptions = new ArrayList();
	    pageSizeOptions.add(new SelectItem(new Integer(5), "5"));
	    pageSizeOptions.add(new SelectItem(new Integer(10), "10"));
	    pageSizeOptions.add(new SelectItem(new Integer(20), "20"));
	}
	return pageSizeOptions;
    }

    public String toString() {
	StringBuffer criteria = new StringBuffer();
	if (StringUtils.hasText(searchString)) {
	    criteria.append("Text: " + searchString + ", ");
	}
	criteria.append("Page Size: " + pageSize);
	return criteria.toString();
    }

}
