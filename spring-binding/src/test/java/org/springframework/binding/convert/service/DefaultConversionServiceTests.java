/*
 * Copyright 2004-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.binding.convert.service;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import junit.framework.TestCase;

import org.springframework.binding.convert.ConversionExecutionException;
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.ConversionExecutorNotFoundException;
import org.springframework.binding.convert.converters.Converter;
import org.springframework.binding.convert.converters.FormattedStringToNumber;
import org.springframework.binding.convert.converters.StringToBoolean;
import org.springframework.binding.format.DefaultNumberFormatFactory;

/**
 * Test case for the default conversion service.
 * 
 * @author Keith Donald
 */
public class DefaultConversionServiceTests extends TestCase {

	public void testConvertCompatibleTypes() {
		DefaultConversionService service = new DefaultConversionService();
		List lst = new ArrayList();
		assertSame(lst, service.getConversionExecutor(ArrayList.class, List.class).execute(lst));
	}

	public void testOverrideConverter() {
		Converter customConverter = new StringToBoolean("ja", "nee");
		DefaultConversionService service = new DefaultConversionService();
		StaticConversionExecutor executor = (StaticConversionExecutor) service.getConversionExecutor(String.class,
				Boolean.class);
		assertNotSame(customConverter, executor.getConverter());
		try {
			executor.execute("ja");
			fail();
		} catch (ConversionExecutionException e) {
			// expected
		}
		service.addConverter(customConverter);
		executor = (StaticConversionExecutor) service.getConversionExecutor(String.class, Boolean.class);
		assertSame(customConverter, executor.getConverter());
		assertTrue(((Boolean) executor.execute("ja")).booleanValue());
	}

	public void testTargetClassNotSupported() {
		DefaultConversionService service = new DefaultConversionService();
		try {
			service.getConversionExecutor(String.class, HashMap.class);
			fail("Should have thrown an exception");
		} catch (ConversionExecutorNotFoundException e) {
		}
	}

	public void testValidConversion() {
		DefaultConversionService service = new DefaultConversionService();
		ConversionExecutor executor = service.getConversionExecutor(String.class, Integer.class);
		Integer three = (Integer) executor.execute("3");
		assertEquals(3, three.intValue());

		ConversionExecutor executor2 = service.getConversionExecutor(Integer.class, String.class);
		String threeString = (String) executor2.execute(new Integer(3));
		assertEquals("3", threeString);
	}

	public void testRegisterConverter() {
		GenericConversionService service = new GenericConversionService();
		FormattedStringToNumber converter = new FormattedStringToNumber();
		DefaultNumberFormatFactory numberFormatFactory = new DefaultNumberFormatFactory();
		numberFormatFactory.setLocale(Locale.US);
		converter.setNumberFormatFactory(numberFormatFactory);
		service.addConverter(converter);
		ConversionExecutor executor = service.getConversionExecutor(String.class, Integer.class);
		Integer three = (Integer) executor.execute("3,000");
		assertEquals(3000, three.intValue());
		ConversionExecutor executor2 = service.getConversionExecutor(Integer.class, String.class);
		String string = (String) executor2.execute(new Integer(3000));
		assertEquals("3,000", string);
	}

	public void testRegisterCustomConverter() {
		DefaultConversionService service = new DefaultConversionService();
		FormattedStringToNumber converter = new FormattedStringToNumber();
		DefaultNumberFormatFactory numberFormatFactory = new DefaultNumberFormatFactory();
		numberFormatFactory.setLocale(Locale.US);
		converter.setNumberFormatFactory(numberFormatFactory);
		service.addConverter("usaNumber", converter);
		ConversionExecutor executor = service.getConversionExecutor("usaNumber", String.class, Integer.class);
		Integer three = (Integer) executor.execute("3,000");
		assertEquals(3000, three.intValue());
		ConversionExecutor executor2 = service.getConversionExecutor("usaNumber", Integer.class, String.class);
		String string = (String) executor2.execute(new Integer(3000));
		assertEquals("3,000", string);
	}

	public void testConversionPrimitive() {
		DefaultConversionService service = new DefaultConversionService();
		ConversionExecutor executor = service.getConversionExecutor(String.class, int.class);
		Integer three = (Integer) executor.execute("3");
		assertEquals(3, three.intValue());
	}

	public void testArrayConversion() {
		DefaultConversionService service = new DefaultConversionService();
		ConversionExecutor executor = service.getConversionExecutor(String[].class, Integer[].class);
		Integer[] result = (Integer[]) executor.execute(new String[] { "1", "2", "3" });
		assertEquals(new Integer(1), result[0]);
		assertEquals(new Integer(2), result[1]);
		assertEquals(new Integer(3), result[2]);
	}

	public void testPrimitiveArrayConversion() {
		DefaultConversionService service = new DefaultConversionService();
		ConversionExecutor executor = service.getConversionExecutor(String[].class, int[].class);
		int[] result = (int[]) executor.execute(new String[] { "1", "2", "3" });
		assertEquals(1, result[0]);
		assertEquals(2, result[1]);
		assertEquals(3, result[2]);
	}

	public void testArrayListConversion() {
		DefaultConversionService service = new DefaultConversionService();
		ConversionExecutor executor = service.getConversionExecutor(String[].class, List.class);
		List result = (List) executor.execute(new String[] { "1", "2", "3" });
		assertEquals("1", result.get(0));
		assertEquals("2", result.get(1));
		assertEquals("3", result.get(2));
	}

	public void testListArrayConversion() {
		DefaultConversionService service = new DefaultConversionService();
		ConversionExecutor executor = service.getConversionExecutor(Collection.class, String[].class);
		List list = new ArrayList();
		list.add("1");
		list.add("2");
		list.add("3");
		String[] result = (String[]) executor.execute(list);
		assertEquals("1", result[0]);
		assertEquals("2", result[1]);
		assertEquals("3", result[2]);
	}

	public void testListArrayConversionWithComponentConversion() {
		DefaultConversionService service = new DefaultConversionService();
		ConversionExecutor executor = service.getConversionExecutor(Collection.class, Integer[].class);
		List list = new ArrayList();
		list.add("1");
		list.add("2");
		list.add("3");
		Integer[] result = (Integer[]) executor.execute(list);
		assertEquals(new Integer(1), result[0]);
		assertEquals(new Integer(2), result[1]);
		assertEquals(new Integer(3), result[2]);
	}

	public void testArrayLinkedListConversion() {
		DefaultConversionService service = new DefaultConversionService();
		ConversionExecutor executor = service.getConversionExecutor(String[].class, LinkedList.class);
		LinkedList result = (LinkedList) executor.execute(new String[] { "1", "2", "3" });
		assertEquals("1", result.get(0));
		assertEquals("2", result.get(1));
		assertEquals("3", result.get(2));
	}

	public void testArrayAbstractListConversion() {
		DefaultConversionService service = new DefaultConversionService();
		try {
			service.getConversionExecutor(String[].class, AbstractList.class);
		} catch (IllegalArgumentException e) {

		}
	}

	public void testToArrayConversion() {
		DefaultConversionService service = new DefaultConversionService();
		ConversionExecutor executor = service.getConversionExecutor(String.class, String[].class);
		String[] result = (String[]) executor.execute("1,2,3");
		assertEquals(1, result.length);
		assertEquals("1,2,3", result[0]);
	}

	public void testToListConversion() {
		DefaultConversionService service = new DefaultConversionService();
		ConversionExecutor executor = service.getConversionExecutor(String.class, List.class);
		List result = (List) executor.execute("1,2,3");
		assertEquals(1, result.size());
		assertEquals("1,2,3", result.get(0));
	}

	public void testToArrayConversionWithElementConversion() {
		DefaultConversionService service = new DefaultConversionService();
		ConversionExecutor executor = service.getConversionExecutor(String.class, Integer[].class);
		Integer[] result = (Integer[]) executor.execute("123");
		assertEquals(1, result.length);
		assertEquals(new Integer(123), result[0]);
	}

	public void testGetConversionExecutorsForSource() {
		DefaultConversionService service1 = new DefaultConversionService();
		service1.addConverter(new CustomConverter());
		GenericConversionService service2 = new GenericConversionService();
		FormattedStringToNumber formatterConverter = new FormattedStringToNumber(BigDecimal.class);
		service2.addConverter(formatterConverter);
		service2.setParent(service1);
		Set converters = service2.getConversionExecutors(String.class);
		Iterator it = converters.iterator();
		while (it.hasNext()) {
			ConversionExecutor executor = (ConversionExecutor) it.next();
			if (executor.getTargetClass().equals(BigDecimal.class)) {
				StaticConversionExecutor se = (StaticConversionExecutor) executor;
				assertSame(formatterConverter, se.getConverter());
			}
		}
		assertEquals(14, converters.size());
	}

	private static class CustomConverter implements Converter {

		public Object convertSourceToTargetClass(Object source, Class targetClass) throws Exception {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Auto-generated method stub");
		}

		public Class getSourceClass() {
			return String.class;
		}

		public Class getTargetClass() {
			return Principal.class;
		}

	}

	// public void testGenericTypeConversionOGNL() {
	// DefaultConversionService service = new DefaultConversionService();
	// Map map = new HashMap();
	// map.put("stringArray", new String[] { "1", "2", "3" });
	// map.put("string", "1");
	// TestBean bean = new TestBean();
	// OgnlExpressionParser parser = new OgnlExpressionParser();
	// Expression stringArray = parser.parseExpression("stringArray", null);
	// Expression integerList = parser.parseExpression("integerList", null);
	// DefaultMappingContext context = new DefaultMappingContext(map, bean, service);
	// DefaultMapping mapping = new DefaultMapping(stringArray, integerList);
	// mapping.map(context);
	// assertEquals(new Integer(1), bean.getIntegerList().get(0));
	// assertEquals(new Integer(2), bean.getIntegerList().get(1));
	// assertEquals(new Integer(3), bean.getIntegerList().get(2));
	// }
	//
	// public void testGenericTypeConversionBeanWrapper() {
	// DefaultConversionService service = new DefaultConversionService();
	// Map map = new HashMap();
	// map.put("stringArray", new String[] { "1", "2", "3" });
	// map.put("string", "1");
	// CollectionWrapperBean wrapper = new CollectionWrapperBean(map);
	// TestBean bean = new TestBean();
	// BeanWrapperExpressionParser parser = new BeanWrapperExpressionParser();
	// Expression stringArray = parser.parseExpression("map[stringArray]", null);
	// Expression integerList = parser.parseExpression("integerList", null);
	// DefaultMappingContext context = new DefaultMappingContext(wrapper, bean, service);
	// DefaultMapping mapping = new DefaultMapping(stringArray, integerList);
	// mapping.map(context);
	// assertEquals(new Integer(1), bean.getIntegerList().get(0));
	// assertEquals(new Integer(2), bean.getIntegerList().get(1));
	// assertEquals(new Integer(3), bean.getIntegerList().get(2));
	// }
	//
	// public void testGenericTypeConversionBeanWrapperWithCustomConversion() {
	// DefaultConversionService service = new DefaultConversionService();
	// service.addConverter(new StringToAuthority());
	// Map map = new HashMap();
	// map.put("authorityArray", new String[] { "keith", "keri", "annabelle" });
	// CollectionWrapperBean wrapper = new CollectionWrapperBean(map);
	// TestBean bean = new TestBean();
	// BeanWrapperExpressionParser parser = new BeanWrapperExpressionParser();
	// parser.setConversionService(service);
	// Expression authorityArray = parser.parseExpression("map[authorityArray]", null);
	// Expression authorityList = parser.parseExpression("authorityList", null);
	// DefaultMappingContext context = new DefaultMappingContext(wrapper, bean, service);
	// DefaultMapping mapping = new DefaultMapping(authorityArray, authorityList);
	// mapping.map(context);
	// assertEquals(new Authority("keith"), bean.getAuthorityList().get(0));
	// assertEquals(new Authority("keri"), bean.getAuthorityList().get(1));
	// assertEquals(new Authority("annabelle"), bean.getAuthorityList().get(2));
	// }
	//
	// public void testGenericTypeConversionBeanWrapperWithNestedCustomConversion() {
	// DefaultConversionService service = new DefaultConversionService();
	// service.addConverter(new StringToAuthority());
	// Map map = new HashMap();
	// map.put("foo", "keith");
	// CollectionWrapperBean wrapper = new CollectionWrapperBean(map);
	// TestBean bean = new TestBean();
	// BeanWrapperExpressionParser parser = new BeanWrapperExpressionParser();
	// parser.setConversionService(service);
	// Expression keith = parser.parseExpression("map[foo]", null);
	// Expression nestedMapEntry = parser.parseExpression("nested[0][0]", null);
	// DefaultMappingContext context = new DefaultMappingContext(wrapper, bean, service);
	// DefaultMapping mapping = new DefaultMapping(keith, nestedMapEntry);
	// mapping.map(context);
	// assertEquals(new Authority("keith"), bean.getNested().get(0).get(0));
	// }
	//
	// public void testGenericTypeConversionEL() {
	// DefaultConversionService service = new DefaultConversionService();
	// Map map = new HashMap();
	// map.put("stringArray", new String[] { "1", "2", "3" });
	// map.put("string", "1");
	// TestBean bean = new TestBean();
	// ELExpressionParser parser = new ELExpressionParser(new ExpressionFactoryImpl());
	// Expression stringArray = parser.parseExpression("stringArray", null);
	// Expression integerList = parser.parseExpression("integerList", null);
	// DefaultMappingContext context = new DefaultMappingContext(map, bean, service);
	// DefaultMapping mapping = new DefaultMapping(stringArray, integerList);
	// mapping.map(context);
	// assertEquals(new Integer(1), bean.getIntegerList().get(0));
	// assertEquals(new Integer(2), bean.getIntegerList().get(1));
	// assertEquals(new Integer(3), bean.getIntegerList().get(2));
	// }
	//
	// public void testArrayConversionOGNL() {
	// DefaultConversionService service = new DefaultConversionService();
	// Map map = new HashMap();
	// map.put("integerArray", new Integer[] { 1, 2, 3 });
	// map.put("string", "1");
	// TestBean bean = new TestBean();
	// OgnlExpressionParser parser = new OgnlExpressionParser();
	// Expression stringArray = parser.parseExpression("integerArray", null);
	// Expression integerList = parser.parseExpression("primitiveArray", null);
	// DefaultMappingContext context = new DefaultMappingContext(map, bean, service);
	// DefaultMapping mapping = new DefaultMapping(stringArray, integerList);
	// mapping.map(context);
	// }
	//
	// public void testArrayConversionEL() {
	// DefaultConversionService service = new DefaultConversionService();
	// Map map = new HashMap();
	// map.put("stringArray", new String[] { "1", "2", "3" });
	// map.put("string", "1");
	// TestBean bean = new TestBean();
	// ELExpressionParser parser = new ELExpressionParser(new ExpressionFactoryImpl());
	// Expression stringArray = parser.parseExpression("stringArray", null);
	// Expression integerList = parser.parseExpression("primitiveArray", null);
	// DefaultMappingContext context = new DefaultMappingContext(map, bean, service);
	// DefaultMapping mapping = new DefaultMapping(stringArray, integerList);
	// mapping.map(context);
	// }
	//
	// public static class CollectionWrapperBean {
	//
	// private Collection collection;
	//
	// private Map map;
	//
	// public CollectionWrapperBean(Map map) {
	// this.map = map;
	// }
	//
	// public CollectionWrapperBean(Collection collection) {
	// this.collection = collection;
	// }
	//
	// public Collection getCollection() {
	// return collection;
	// }
	//
	// public Map getMap() {
	// return map;
	// }
	// }
	//
	// public static class TestBean {
	//
	// private List<Integer> integerList;
	//
	// private int primitive;
	//
	// private int[] primitiveArray;
	//
	// private List<Authority> authorityList;
	//
	// private List<Map<Integer, Authority>> nested;
	//
	// public TestBean() {
	// nested = new ArrayList<Map<Integer, Authority>>();
	// nested.add(new HashMap<Integer, Authority>());
	// nested.get(0).put(0, new Authority("bubba"));
	// }
	//
	// public List<Integer> getIntegerList() {
	// return integerList;
	// }
	//
	// public void setIntegerList(List<Integer> integerList) {
	// this.integerList = integerList;
	// }
	//
	// public int getPrimitive() {
	// return primitive;
	// }
	//
	// public void setPrimitive(int primitive) {
	// this.primitive = primitive;
	// }
	//
	// public int[] getPrimitiveArray() {
	// return primitiveArray;
	// }
	//
	// public void setPrimitiveArray(int[] primitiveArray) {
	// this.primitiveArray = primitiveArray;
	// }
	//
	// public List<Authority> getAuthorityList() {
	// return authorityList;
	// }
	//
	// public void setAuthorityList(List<Authority> authorityList) {
	// this.authorityList = authorityList;
	// }
	//
	// // nested[0][1]
	// public List<Map<Integer, Authority>> getNested() {
	// return nested;
	// }
	//
	// public void setNested(List<Map<Integer, Authority>> nested) {
	// this.nested = nested;
	// }
	//
	// }
	//
	// public static class Authority {
	// private String name;
	//
	// public Authority(String name) {
	// this.name = name;
	// }
	//
	// public String getName() {
	// return name;
	// }
	//
	// public boolean equals(Object o) {
	// if (!(o instanceof Authority)) {
	// return false;
	// }
	// Authority auth = (Authority) o;
	// return name.equals(auth.name);
	// }
	//
	// public int hashCode() {
	// return name.hashCode();
	// }
	//
	// public String toString() {
	// return name;
	// }
	// }
	//
	// public static class StringToAuthority extends StringToObject {
	//
	// public StringToAuthority() {
	// super(Authority.class);
	// }
	//
	// protected Object toObject(String string, Class targetClass) throws Exception {
	// return new Authority(string);
	// }
	//
	// protected String toString(Object object) throws Exception {
	// return object.toString();
	// }
	//
	// }

	// public void testArrayListConversionWithElementConversion() throws Exception {
	// DefaultConversionService service = new DefaultConversionService();
	// ConversionExecutor executor = service.getConversionExecutor(String[].class, IntegerArrayList.class);
	// List result = (List) executor.execute(new String[] { "1", "2", "3" });
	// assertEquals(new Integer(1), result.get(0));
	// assertEquals(new Integer(2), result.get(1));
	// assertEquals(new Integer(3), result.get(2));
	// }
	//
	// public static class IntegerArrayList implements List<Integer> {
	//
	// private ArrayList realList = new ArrayList();
	//
	// public IntegerArrayList() {
	// }
	//
	// public void add(int index, Integer element) {
	// throw new UnsupportedOperationException("Auto-generated method stub");
	// }
	//
	// public boolean add(Integer o) {
	// return realList.add(o);
	// }
	//
	// public boolean addAll(Collection<? extends Integer> c) {
	// throw new UnsupportedOperationException("Auto-generated method stub");
	// }
	//
	// public boolean addAll(int index, Collection<? extends Integer> c) {
	// throw new UnsupportedOperationException("Auto-generated method stub");
	// }
	//
	// public void clear() {
	// throw new UnsupportedOperationException("Auto-generated method stub");
	// }
	//
	// public boolean contains(Object o) {
	// throw new UnsupportedOperationException("Auto-generated method stub");
	// }
	//
	// public boolean containsAll(Collection<?> c) {
	// throw new UnsupportedOperationException("Auto-generated method stub");
	// }
	//
	// public Integer get(int index) {
	// return (Integer) realList.get(index);
	// }
	//
	// public int indexOf(Object o) {
	// throw new UnsupportedOperationException("Auto-generated method stub");
	// }
	//
	// public boolean isEmpty() {
	// throw new UnsupportedOperationException("Auto-generated method stub");
	// }
	//
	// public Iterator<Integer> iterator() {
	// throw new UnsupportedOperationException("Auto-generated method stub");
	// }
	//
	// public int lastIndexOf(Object o) {
	// throw new UnsupportedOperationException("Auto-generated method stub");
	// }
	//
	// public ListIterator<Integer> listIterator() {
	// throw new UnsupportedOperationException("Auto-generated method stub");
	// }
	//
	// public ListIterator<Integer> listIterator(int index) {
	// throw new UnsupportedOperationException("Auto-generated method stub");
	// }
	//
	// public Integer remove(int index) {
	// throw new UnsupportedOperationException("Auto-generated method stub");
	// }
	//
	// public boolean remove(Object o) {
	// throw new UnsupportedOperationException("Auto-generated method stub");
	// }
	//
	// public boolean removeAll(Collection<?> c) {
	// throw new UnsupportedOperationException("Auto-generated method stub");
	// }
	//
	// public boolean retainAll(Collection<?> c) {
	// throw new UnsupportedOperationException("Auto-generated method stub");
	// }
	//
	// public Integer set(int index, Integer element) {
	// throw new UnsupportedOperationException("Auto-generated method stub");
	// }
	//
	// public int size() { // TODO Auto-generated method stu
	// throw new UnsupportedOperationException("Auto-generatedmethod stub");
	// }
	//
	// public List<Integer> subList(int fromIndex, int toIndex) { // TODO Auto-generated method stub throw new
	// throw new UnsupportedOperationException("Auto-generated method stub");
	// }
	//
	// public Object[] toArray() { // TODO Auto-generated method stub throw new
	// throw new UnsupportedOperationException("Auto-generated method stub");
	// }
	//
	// public <T> T[] toArray(T[] a) { // TODO Auto-generated method stub throw new
	// throw new UnsupportedOperationException("Auto-generated method stub");
	// }
	// }

}