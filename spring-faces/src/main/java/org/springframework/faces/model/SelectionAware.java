package org.springframework.faces.model;

import java.util.List;
import javax.faces.model.DataModel;

/**
 * Interface for {@link DataModel} implementations that need to track selected rows.
 * 
 * @author Jeremy Grelle
 */
public interface SelectionAware {

	public boolean isCurrentRowSelected();

	public void setSelected(boolean rowSelected);

	public void setSelections(List selections);

	public List getSelections();

	public void selectAll();

	public void select(Object rowData);
}
