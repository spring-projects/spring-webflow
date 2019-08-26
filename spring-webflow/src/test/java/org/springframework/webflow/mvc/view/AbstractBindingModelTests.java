package org.springframework.webflow.mvc.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.binding.convert.converters.StringToObject;
import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.binding.mapping.Mapping;
import org.springframework.binding.mapping.MappingResult;
import org.springframework.binding.mapping.impl.DefaultMappingResults;
import org.springframework.binding.mapping.results.TypeConversionError;
import org.springframework.binding.message.DefaultMessageContext;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.validation.FieldError;
import org.springframework.webflow.TestBean;
import org.springframework.webflow.engine.builder.BinderConfiguration;
import org.springframework.webflow.engine.builder.BinderConfiguration.Binding;

public abstract class AbstractBindingModelTests {

	BindingModel model;
	DefaultMessageContext messages;
	DefaultConversionService conversionService;
	TestBean testBean;
	ExpressionParser expressionParser;

	@BeforeEach
	public void setUp() {
		testBean = new TestBean();
		messages = new DefaultMessageContext();
		conversionService = new DefaultConversionService();
		expressionParser = getExpressionParser();
		model = new BindingModel("testBean", testBean, expressionParser, conversionService, messages);
	}

	protected abstract ExpressionParser getExpressionParser();

	@Test
	public void testInitialState() {
		assertEquals(0, model.getErrorCount());
		assertEquals(0, model.getFieldErrorCount());
		assertEquals(0, model.getFieldErrorCount("datum1"));
		assertEquals(0, model.getGlobalErrorCount());
		assertEquals(0, model.getAllErrors().size());
		assertEquals(0, model.getFieldErrors().size());
		assertNull(model.getFieldError("datum1"));
		assertEquals(String.class, model.getFieldType("datum1"));
	}

	@Test
	public void testGetValue() {
		testBean.datum1 = "test";
		assertEquals("test", model.getFieldValue("datum1"));
	}

	@Test
	public void testGetConvertedValue() {
		testBean.datum2 = 3;
		assertEquals("3", model.getFieldValue("datum2"));
	}

	@Test
	public void testGetRawValue() {
		testBean.datum2 = 3;
		assertEquals(3, model.getRawFieldValue("datum2"));
	}

	@Test
	public void testGetFieldValueConvertedWithCustomConverter() {
		testBean.datum2 = 3;
		conversionService.addConverter("customConverter", new StringToObject(Integer.class) {
			protected Object toObject(String string, Class<?> targetClass) throws Exception {
				return Integer.valueOf(string);
			}

			protected String toString(Object object) throws Exception {
				return "$" + object;
			}
		});
		BinderConfiguration binder = new BinderConfiguration();
		binder.addBinding(new Binding("datum2", "customConverter", true));
		model.setBinderConfiguration(binder);
		assertEquals("$3", model.getFieldValue("datum2"));
	}

	@Test
	public void testGetFieldValueError() {
		Map<String, String> source = new HashMap<>();
		source.put("datum2", "bogus");
		List<MappingResult> mappingResults = new ArrayList<>();
		Mapping mapping = new Mapping() {
			public Expression getSourceExpression() {
				return expressionParser.parseExpression("datum2", null);
			}

			public Expression getTargetExpression() {
				return expressionParser.parseExpression("datum2", null);
			}

			public boolean isRequired() {
				return true;
			}
		};
		mappingResults.add(new TypeConversionError(mapping, "bogus", null));
		DefaultMappingResults results = new DefaultMappingResults(source, testBean, mappingResults);
		model.setMappingResults(results);
		assertEquals("bogus", model.getFieldValue("datum2"));
		// not offically an error until an actual error message is associated with field
		assertEquals(0, model.getErrorCount());
		assertEquals(0, model.getFieldErrorCount());
	}

	@Test
	public void testGetFieldError() {
		messages.addMessage(new MessageBuilder().source("datum2").error().defaultText("Error").build());
		assertEquals(1, model.getErrorCount());
		assertEquals(1, model.getFieldErrorCount());
		assertEquals(0, model.getGlobalErrorCount());

		FieldError error = model.getFieldError("datum2");
		assertEquals(null, error.getCode());
		assertEquals(null, error.getCodes());
		assertEquals(null, error.getArguments());
		assertEquals("Error", error.getDefaultMessage());
		// we dont track this
		assertEquals(null, error.getRejectedValue());
		assertTrue(!error.isBindingFailure());

		FieldError error2 = model.getFieldErrors().get(0);
		assertEquals(error, error2);
	}

	@Test
	public void testGetFieldErrorsWildcard() {
		messages.addMessage(new MessageBuilder().source("datum2").error().defaultText("Error").build());
		assertEquals(1, model.getFieldErrorCount("da*"));
		FieldError error = model.getFieldError("da*");
		assertEquals(null, error.getCode());
		assertEquals(null, error.getCodes());
		assertEquals(null, error.getArguments());
		assertEquals("Error", error.getDefaultMessage());
	}

	@Test
	public void testFindPropertyEditor() {
		PropertyEditor editor = model.findEditor("datum2", Integer.class);
		assertNotNull(editor);
		editor.setAsText((String) model.getFieldValue("datum2"));
		assertEquals("0", editor.getAsText());
	}

	@Test
	public void testNestedPath() {
		model = new BindingModel("nestedPathBean", new NestedPathBean(), expressionParser, conversionService, messages);
		model.pushNestedPath("nestedBean");
		assertEquals("test", model.getFieldValue("datum1"));
		assertEquals("0", model.getFieldValue("datum2"));
		Class<?> clazz = model.getFieldType("datum2");
		assertTrue(int.class.equals(clazz) || Integer.class.equals(clazz));

		messages.addMessage(new MessageBuilder().source("nestedBean.datum2").error().defaultText("Error").build());
		assertNotNull(model.getFieldErrors("datum2").get(0));
		model.popNestedPath();
		assertEquals("", model.getFieldValue("datum1"));
	}

	public static class NestedPathBean {
		private String datum1 = "";

		private NestedBean nestedBean = new NestedBean();

		public String getDatum1() {
			return datum1;
		}

		public void setDatum1(String datum1) {
			this.datum1 = datum1;
		}

		public NestedBean getNestedBean() {
			return nestedBean;
		}

		public void setNestedBean(NestedBean nestedBean) {
			this.nestedBean = nestedBean;
		}

		public static class NestedBean {
			private String datum1 = "test";
			private int datum2;

			public int getDatum2() {
				return datum2;
			}

			public void setDatum2(int datum2) {
				this.datum2 = datum2;
			}

			public String getDatum1() {
				return datum1;
			}

			public void setDatum1(String datum1) {
				this.datum1 = datum1;
			}
		}
	}
}
