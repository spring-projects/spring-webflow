package org.springframework.binding.mapping;

public class MappingResult {

	private Mapping mapping;

	private Result result;

	public MappingResult(Mapping mapping, Result result) {
		this.mapping = mapping;
		this.result = result;
	}

	public Mapping getMapping() {
		return mapping;
	}

	public Result getResult() {
		return result;
	}

}
