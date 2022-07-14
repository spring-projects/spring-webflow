package org.springframework.binding.expression.el;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import jakarta.el.ELContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.binding.collection.MapAdaptable;

public class MapAdaptableELResolverTests {

	private ELContext context;

	@BeforeEach
	public void setUp() {
		context = new DefaultELContext(new MapAdaptableELResolver(), null, null);
	}

	@Test
	public void testGetType() {
		Class<?> type = context.getELResolver().getType(context, new TestMapAdaptable(), "bar");
		assertTrue(context.isPropertyResolved());
		assertEquals(String.class, type);
	}

	@Test
	public void testGetType_UnknownProperty() {
		Class<?> type = context.getELResolver().getType(context, new TestMapAdaptable(), "foo");
		assertTrue(context.isPropertyResolved());
		assertEquals(null, type);
	}

	@Test
	public void testGetValue() {
		Object value = context.getELResolver().getValue(context, new TestMapAdaptable(), "bar");
		assertTrue(context.isPropertyResolved());
		assertEquals("bar", value);
	}

	@Test
	public void testGetValue_UnknownProperty() {
		Object value = context.getELResolver().getValue(context, new TestMapAdaptable(), "foo");
		assertTrue(context.isPropertyResolved());
		assertEquals(null, value);
	}

	@Test
	public void testSetValue() {
		MapAdaptable<String, String> testMap = new TestMapAdaptable();
		context.getELResolver().setValue(context, testMap, "foo", "foo");
		assertTrue(context.isPropertyResolved());
		assertEquals("foo", testMap.asMap().get("foo"));
	}

	@Test
	public void testSetValue_OverWrite() {
		MapAdaptable<String, String> testMap = new TestMapAdaptable();
		context.getELResolver().setValue(context, testMap, "bar", "foo");
		assertTrue(context.isPropertyResolved());
		assertEquals("foo", testMap.asMap().get("bar"));
	}

	private class TestMapAdaptable implements MapAdaptable<String, String> {
		private Map<String, String> map = new HashMap<>();

		public TestMapAdaptable() {
			map.put("bar", "bar");
		}

		public Map<String, String> asMap() {
			return map;
		}
	}
}
