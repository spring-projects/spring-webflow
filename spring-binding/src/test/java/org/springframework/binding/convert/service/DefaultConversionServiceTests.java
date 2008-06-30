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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

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
		try {
			service.getConversionExecutor(List.class, ArrayList.class);
			fail();
		} catch (ConversionExecutorNotFoundException e) {
			// expected
		}
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
		FormattedStringToNumber converter = new FormattedStringToNumber(Integer.class);
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
		assertEquals("1", result[0]);
		assertEquals("2", result[1]);
		assertEquals("3", result[2]);
	}

	public void testToArrayConversionWithElementConversion() {
		DefaultConversionService service = new DefaultConversionService();
		ConversionExecutor executor = service.getConversionExecutor(String.class, Integer[].class);
		Integer[] result = (Integer[]) executor.execute("1,2,3");
		assertEquals(new Integer(1), result[0]);
		assertEquals(new Integer(2), result[1]);
		assertEquals(new Integer(3), result[2]);
	}

	/*
	 * 
	 * public void testArrayListConversionWithElementConversion() throws Exception { DefaultConversionService service =
	 * new DefaultConversionService(); ConversionExecutor executor = service.getConversionExecutor(String[].class,
	 * IntegerArrayList.class); List result = (List) executor.execute(new String[] { "1", "2", "3" }); assertEquals(new
	 * Integer(1), result.get(0)); assertEquals(new Integer(2), result.get(1)); assertEquals(new Integer(3),
	 * result.get(2)); }
	 * 
	 * 
	 * public static class IntegerArrayList implements List<Integer> {
	 * 
	 * private ArrayList realList = new ArrayList();
	 * 
	 * public IntegerArrayList() { }
	 * 
	 * public void add(int index, Integer element) { // TODO Auto-generated method stub throw new
	 * UnsupportedOperationException("Auto-generated method stub"); }
	 * 
	 * public boolean add(Integer o) { return realList.add(o); }
	 * 
	 * public boolean addAll(Collection<? extends Integer> c) { // TODO Auto-generated method stub throw new
	 * UnsupportedOperationException("Auto-generated method stub"); }
	 * 
	 * public boolean addAll(int index, Collection<? extends Integer> c) { // TODO Auto-generated method stub throw new
	 * UnsupportedOperationException("Auto-generated method stub"); }
	 * 
	 * public void clear() { // TODO Auto-generated method stub throw new UnsupportedOperationException("Auto-generated
	 * method stub"); }
	 * 
	 * public boolean contains(Object o) { // TODO Auto-generated method stub throw new
	 * UnsupportedOperationException("Auto-generated method stub"); }
	 * 
	 * public boolean containsAll(Collection<?> c) { // TODO Auto-generated method stub throw new
	 * UnsupportedOperationException("Auto-generated method stub"); }
	 * 
	 * public Integer get(int index) { return (Integer) realList.get(index); }
	 * 
	 * public int indexOf(Object o) { // TODO Auto-generated method stub throw new
	 * UnsupportedOperationException("Auto-generated method stub"); }
	 * 
	 * public boolean isEmpty() { // TODO Auto-generated method stub throw new
	 * UnsupportedOperationException("Auto-generated method stub"); }
	 * 
	 * public Iterator<Integer> iterator() { // TODO Auto-generated method stub throw new
	 * UnsupportedOperationException("Auto-generated method stub"); }
	 * 
	 * public int lastIndexOf(Object o) { // TODO Auto-generated method stub throw new
	 * UnsupportedOperationException("Auto-generated method stub"); }
	 * 
	 * public ListIterator<Integer> listIterator() { // TODO Auto-generated method stub throw new
	 * UnsupportedOperationException("Auto-generated method stub"); }
	 * 
	 * public ListIterator<Integer> listIterator(int index) { // TODO Auto-generated method stub throw new
	 * UnsupportedOperationException("Auto-generated method stub"); }
	 * 
	 * public Integer remove(int index) { // TODO Auto-generated method stub throw new
	 * UnsupportedOperationException("Auto-generated method stub"); }
	 * 
	 * public boolean remove(Object o) { // TODO Auto-generated method stub throw new
	 * UnsupportedOperationException("Auto-generated method stub"); }
	 * 
	 * public boolean removeAll(Collection<?> c) { // TODO Auto-generated method stub throw new
	 * UnsupportedOperationException("Auto-generated method stub"); }
	 * 
	 * public boolean retainAll(Collection<?> c) { // TODO Auto-generated method stub throw new
	 * UnsupportedOperationException("Auto-generated method stub"); }
	 * 
	 * public Integer set(int index, Integer element) { // TODO Auto-generated method stub throw new
	 * UnsupportedOperationException("Auto-generated method stub"); }
	 * 
	 * public int size() { // TODO Auto-generated method stub throw new UnsupportedOperationException("Auto-generated
	 * method stub"); }
	 * 
	 * public List<Integer> subList(int fromIndex, int toIndex) { // TODO Auto-generated method stub throw new
	 * UnsupportedOperationException("Auto-generated method stub"); }
	 * 
	 * public Object[] toArray() { // TODO Auto-generated method stub throw new
	 * UnsupportedOperationException("Auto-generated method stub"); }
	 * 
	 * public <T> T[] toArray(T[] a) { // TODO Auto-generated method stub throw new
	 * UnsupportedOperationException("Auto-generated method stub"); } }
	 */

}