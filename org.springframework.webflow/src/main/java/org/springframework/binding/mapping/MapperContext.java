package org.springframework.binding.mapping;

import org.springframework.expression.EvaluationContext;

public interface MapperContext {
	
	EvaluationContext getSource();
	
	EvaluationContext getTarget();
	
}
