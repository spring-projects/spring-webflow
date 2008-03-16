package org.springframework.binding.mapping;

import java.util.List;

public interface MappingResults {

	public Object getSource();

	public Object getTarget();

	public List getAllResults();

	public boolean hasErrorResults();

	public List getErrorResults();

	public List getResults(MappingResultsCriteria criteria);

}