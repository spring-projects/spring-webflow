package org.springframework.binding.mapping.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.springframework.binding.mapping.MappingResult;
import org.springframework.binding.mapping.MappingResults;
import org.springframework.binding.mapping.MappingResultsCriteria;

public class DefaultMappingResults implements MappingResults {

	private Object source;

	private Object target;

	private List mappingResults;

	public DefaultMappingResults(Object source, Object target, List mappingResults) {
		this.source = source;
		this.target = target;
		this.mappingResults = mappingResults;
	}

	public Object getSource() {
		return source;
	}

	public Object getTarget() {
		return target;
	}

	public List getAllResults() {
		return Collections.unmodifiableList(mappingResults);
	}

	public boolean hasErrorResults() {
		Iterator it = mappingResults.iterator();
		while (it.hasNext()) {
			MappingResult result = (MappingResult) it.next();
			if (result.getResult().isError()) {
				return true;
			}
		}
		return false;
	}

	public List getErrorResults() {
		List errorResults = new ArrayList();
		Iterator it = mappingResults.iterator();
		while (it.hasNext()) {
			MappingResult result = (MappingResult) it.next();
			if (result.getResult().isError()) {
				errorResults.add(result);
			}
		}
		return Collections.unmodifiableList(errorResults);
	}

	public List getResults(MappingResultsCriteria criteria) {
		List results = new ArrayList();
		Iterator it = mappingResults.iterator();
		while (it.hasNext()) {
			MappingResult result = (MappingResult) it.next();
			if (criteria.test(result)) {
				results.add(result);
			}
		}
		return Collections.unmodifiableList(results);
	}

}
