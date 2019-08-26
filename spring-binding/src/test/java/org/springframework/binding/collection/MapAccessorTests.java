package org.springframework.binding.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MapAccessorTests {
	private MapAccessor<String, Object> accessor;

	@BeforeEach
	public void setUp() {
		Map<String, Object> map = new HashMap<>();
		map.put("string", "hello");
		map.put("integer", 9);
		map.put("null", null);
		this.accessor = new MapAccessor<>(map);
	}

	@Test
	public void testAccessNullAttribute() {
		assertEquals(null, accessor.get("null"));
		assertEquals(null, accessor.get("null", "something else"));
		assertEquals(null, accessor.getRequired("null"));
		assertEquals(null, accessor.getString("null"));
		assertEquals(null, accessor.getRequiredString("null"));
		assertEquals(null, accessor.getInteger("null"));
		assertEquals(null, accessor.getRequiredInteger("null"));
		assertEquals(null, accessor.getCollection("null"));
		assertEquals(null, accessor.getRequiredCollection("null"));
	}

	@Test
	public void testGetString() {
		assertEquals("hello", accessor.getString("string"));
		assertEquals("hello", accessor.getRequiredString("string"));
	}

	@Test
	public void testGetInteger() {
		assertEquals(new Integer(9), accessor.getInteger("integer"));
		assertEquals(new Integer(9), accessor.getRequiredInteger("integer"));
	}

	@Test
	public void testGetRequiredMissingKey() {
		try {
			accessor.getRequired("bogus");
		} catch (IllegalArgumentException e) {
		}
	}

}
