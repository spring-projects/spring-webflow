package org.springframework.faces.ui;

import java.util.Calendar;
import java.util.Locale;

import javax.faces.FacesException;
import javax.faces.component.UIInput;
import javax.faces.convert.DateTimeConverter;

import junit.framework.TestCase;

import org.springframework.faces.webflow.JSFMockHelper;

public class DojoDecorationRendererTests extends TestCase {

	JSFMockHelper jsf = new JSFMockHelper();

	public void setUp() throws Exception {
		jsf.setUp();
	}

	public void tearDown() throws Exception {
		jsf.tearDown();
	}

	public void testGetValueAsString() {
		UIInput childComponent = new UIInput();
		childComponent.setValue("foo");
		DojoDecorationRenderer renderer = new DojoDecorationRenderer();
		String convertedValue = renderer.getValueAsString(jsf.facesContext(), childComponent);
		assertEquals("foo", convertedValue);
	}

	public void testGetValueAsString_LocalConverter() {
		UIInput childComponent = new UIInput();
		childComponent.setValue(new TestValue());
		childComponent.setConverter(new TestConverter());
		DojoDecorationRenderer renderer = new DojoDecorationRenderer();
		String convertedValue = renderer.getValueAsString(jsf.facesContext(), childComponent);
		assertEquals("foo", convertedValue);
	}

	public void testGetValueAsString_NoConverter() {
		UIInput childComponent = new UIInput();
		childComponent.setValue(new TestValue());
		DojoDecorationRenderer renderer = new DojoDecorationRenderer();
		try {
			renderer.getValueAsString(jsf.facesContext(), childComponent);
			fail("getValueAsString should throw exception if no converter is found");
		} catch (FacesException ex) {
			// expected
		}
	}

	public void testGetValueAsString_GlobalConverter() throws Exception {
		UIInput childComponent = new UIInput();
		childComponent.setValue(new TestValue());
		jsf.facesContext().getApplication().addConverter(TestValue.class, TestConverter.class.getName());
		DojoDecorationRenderer renderer = new DojoDecorationRenderer();
		String convertedValue = renderer.getValueAsString(jsf.facesContext(), childComponent);
		assertEquals("foo", convertedValue);
	}

	public void testGetNodeAttributesAsString() {
		String expectedAttributes = "name : 'foo', value : 'foo'";
		UIInput childComponent = new UIInput();
		childComponent.setId("foo");
		childComponent.setValue("foo");
		DojoDecorationRenderer renderer = new DojoDecorationRenderer();
		String nodeAttributes = renderer.getNodeAttributesAsString(jsf.facesContext(), childComponent);
		assertEquals(expectedAttributes, nodeAttributes);
	}

	public void testGetNodeAttributesAsString_DateValue() {
		String expectedAttributes = "name : 'foo', value : dojo.date.locale.parse('Nov 21, 1977', "
				+ "{selector : 'date', datePattern : 'yyyy-MM-dd'})";
		UIInput childComponent = new UIInput();
		DateTimeConverter converter = new DateTimeConverter();
		converter.setLocale(Locale.US);
		childComponent.setConverter(converter);
		childComponent.setId("foo");
		Calendar cal = Calendar.getInstance(Locale.US);
		cal.set(1977, Calendar.NOVEMBER, 21, 12, 0);
		childComponent.setValue(cal.getTime());
		DojoDecorationRenderer renderer = new DojoDecorationRenderer();
		String nodeAttributes = renderer.getNodeAttributesAsString(jsf.facesContext(), childComponent);
		assertEquals(expectedAttributes, nodeAttributes);
	}
}
