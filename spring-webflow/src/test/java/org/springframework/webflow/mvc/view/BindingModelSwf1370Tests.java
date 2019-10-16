package org.springframework.webflow.mvc.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.binding.message.DefaultMessageContext;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.webflow.expression.spel.WebFlowSpringELExpressionParser;

/**
 * Test harness based on: https://jira.springframework.org/browse/SWF-1370
 * 
 */
public class BindingModelSwf1370Tests {

	private ConversionService conversionService;
	private ConverterRegistry converterRegistry;
	private WebFlowSpringELExpressionParser expressionParser;

	@BeforeEach
	public void setUp() {
		conversionService = new DefaultConversionService();
		expressionParser = new WebFlowSpringELExpressionParser(new SpelExpressionParser(), conversionService);
		converterRegistry = (ConverterRegistry) conversionService.getDelegateConversionService();
	}

	@Test
	public void testGetFieldValueWithInvalidBeanWrapperExpression() throws Exception {
		Question question = new Question();
		converterRegistry.addConverter(new QuestionConverter(question));
		BindingModel model = new BindingModel("theTestBean", new TestBeanWithQuestionResponseMap(question),
				expressionParser, conversionService, new DefaultMessageContext());
		assertEquals("111", model.getFieldValue("responses[1].value"));
	}

	public static class Question {
	};

	public static class Response {

		private int value;

		public Response(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}
	}

	public static class TestBeanWithQuestionResponseMap {
		private Map<Question, Response> responses = new HashMap<>();

		public TestBeanWithQuestionResponseMap(Question question) {
			responses.put(question, new Response(111));
		}

		public Map<Question, Response> getResponses() {
			return responses;
		}

		public void setResponses(Map<Question, Response> responses) {
			this.responses = responses;
		}
	}

	public static class QuestionConverter implements
			org.springframework.core.convert.converter.Converter<Integer, Question> {

		private Map<Integer, Question> map = new HashMap<>();

		public QuestionConverter(Question... questions) {
			for (int i = 0; i < questions.length; i++) {
				map.put(i + 1, questions[i]);
			}
		}

		public Question convert(Integer source) {
			return map.get(source);
		}
	}

}
