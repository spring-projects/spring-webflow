package org.springframework.webflow.mvc.view;

import java.util.HashMap;
import java.util.Map;

import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.webflow.expression.spel.WebFlowSpringELExpressionParser;

public class DefaultBindingModelTests extends AbstractBindingModelTests {

	protected ExpressionParser getExpressionParser() {
		return new WebFlowSpringELExpressionParser(new SpelExpressionParser());
	}

	public void testGetFieldValueWithInvalidBeanWrapperExpression() throws Exception {
		// See SWF-1370
		Question question = new Question();
		Map<Integer, Question> map = new HashMap<Integer, Question>();
		map.put(1, question);
		ConversionService theConversionService = new DefaultConversionService();
		((ConverterRegistry) theConversionService.getDelegateConversionService()).addConverter(new QuestionConverter(
				map));
		WebFlowSpringELExpressionParser theParser = new WebFlowSpringELExpressionParser(new SpelExpressionParser(),
				theConversionService);
		model = new BindingModel("theTestBean", new TestBeanWithQuestionResponseMap(question), theParser,
				theConversionService, messages);
		assertEquals("foo", model.getFieldValue("responses[1].value"));
	}

	private class Question {
	};

	private class Response {

		private String value;

		public Response(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	private class TestBeanWithQuestionResponseMap {
		private Map<Question, Response> responses = new HashMap<Question, Response>();

		public TestBeanWithQuestionResponseMap(Question question) {
			responses.put(question, new Response("foo"));
		}

		public Map<Question, Response> getResponses() {
			return responses;
		}

		public void setResponses(Map<Question, Response> responses) {
			this.responses = responses;
		}
	}

	private class QuestionConverter implements org.springframework.core.convert.converter.Converter<Integer, Question> {

		private Map<Integer, Question> map;

		public QuestionConverter(Map<Integer, Question> map) {
			this.map = map;
		}

		public Question convert(Integer source) {
			return map.get(source);
		}
	}

}
