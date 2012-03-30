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

import java.security.Principal;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;

import junit.framework.TestCase;

import org.springframework.binding.convert.ConversionException;
import org.springframework.binding.convert.ConversionExecutionException;
import org.springframework.binding.convert.ConversionExecutor;
import org.springframework.binding.convert.ConversionExecutorNotFoundException;
import org.springframework.binding.convert.converters.Converter;
import org.springframework.binding.convert.converters.FormattedStringToNumber;
import org.springframework.binding.convert.converters.StringToBoolean;
import org.springframework.binding.convert.converters.StringToObject;
import org.springframework.binding.convert.converters.TwoWayConverter;
import org.springframework.binding.format.DefaultNumberFormatFactory;

/**
 * Test case for the default conversion service.
 * 
 * @author Keith Donald
 */
@SuppressWarnings("deprecation")
public class DefaultConversionServiceTests extends TestCase {

	public void testConvertCompatibleTypes() {
		DefaultConversionService service = new DefaultConversionService();
		List<Object> lst = new ArrayList<Object>();
		assertSame(lst, service.getConversionExecutor(ArrayList.class, List.class).execute(lst));
	}

	public void testOverrideConverter() {
		Converter customConverter = new StringToBoolean("ja", "nee");
		DefaultConversionService service = new DefaultConversionService();
		StaticConversionExecutor executor = (StaticConversionExecutor) service.getConversionExecutor(String.class,
				Boolean.class);
		try {
			executor.execute("ja");
			fail();
		} catch (ConversionExecutionException e) {
			// expected
		}
		service.addConverter(customConverter);
		executor = (StaticConversionExecutor) service.getConversionExecutor(String.class, Boolean.class);
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

	public void testRegisterCustomConverterForSameType() {
		DefaultConversionService service = new DefaultConversionService();
		service.addConverter("trimmer", new Trimmer());
		ConversionExecutor executor = service.getConversionExecutor("trimmer", String.class, String.class);
		assertEquals("a string", executor.execute("a string   "));
	}

	public void testRegisterCustomConverterForSameTypeNotCompatibleSource() {
		DefaultConversionService service = new DefaultConversionService();
		service.addConverter("trimmer", new Trimmer());
		try {
			service.getConversionExecutor("trimmer", Object.class, String.class);
		} catch (ConversionException e) {

		}
	}

	public void testRegisterCustomConverterForSameTypeNotCompatibleTarget() {
		DefaultConversionService service = new DefaultConversionService();
		service.addConverter("trimmer", new Trimmer());
		try {
			service.getConversionExecutor("trimmer", String.class, Object.class);
		} catch (ConversionException e) {

		}
	}

	public void testRegisterCustomConverterReverseComparsion() {
		DefaultConversionService service = new DefaultConversionService();
		service.addConverter("princy", new CustomTwoWayConverter());
		ConversionExecutor executor = service.getConversionExecutor("princy", Principal.class, String.class);
		assertEquals("name", executor.execute(new Principal() {
			public String getName() {
				return "name";
			}
		}));
	}

	public void testRegisterCustomConverterReverseNotCompatibleSource() {
		DefaultConversionService service = new DefaultConversionService();
		service.addConverter("princy", new CustomTwoWayConverter());
		try {
			service.getConversionExecutor("trimmer", Principal.class, Object.class);
		} catch (ConversionException e) {

		}
	}

	public void testRegisterCustomConverterArrayToArray() {
		DefaultConversionService service = new DefaultConversionService();
		service.addConverter("princy", new CustomTwoWayConverter());
		ConversionExecutor executor = service.getConversionExecutor("princy", String[].class, Principal[].class);
		Principal[] p = (Principal[]) executor.execute(new String[] { "princy1", "princy2" });
		assertEquals("princy1", p[0].getName());
		assertEquals("princy2", p[1].getName());
	}

	public void testRegisterCustomConverterArrayToArrayReverse() {
		DefaultConversionService service = new DefaultConversionService();
		service.addConverter("princy", new CustomTwoWayConverter());
		ConversionExecutor executor = service.getConversionExecutor("princy", Principal[].class, String[].class);
		final Principal princy1 = new Principal() {
			public String getName() {
				return "princy1";
			}
		};
		final Principal princy2 = new Principal() {
			public String getName() {
				return "princy2";
			}
		};
		String[] p = (String[]) executor.execute(new Principal[] { princy1, princy2 });
		assertEquals("princy1", p[0]);
		assertEquals("princy2", p[1]);
	}

	public void testRegisterCustomConverterArrayToArrayBogus() {
		DefaultConversionService service = new DefaultConversionService();
		service.addConverter("princy", new CustomTwoWayConverter());
		try {
			service.getConversionExecutor("princy", Integer[].class, Principal[].class);
			fail("Should have failed");
		} catch (ConversionExecutorNotFoundException e) {
		}
	}

	@SuppressWarnings("unchecked")
	public void testRegisterCustomConverterArrayToList() {
		DefaultConversionService service = new DefaultConversionService();
		service.addConverter("princy", new CustomTwoWayConverter());
		ConversionExecutor executor = service.getConversionExecutor("princy", String[].class, List.class);
		List<Principal> list = (List<Principal>) executor.execute(new String[] { "princy1", "princy2" });
		assertEquals("princy1", (list.get(0)).getName());
		assertEquals("princy2", (list.get(1)).getName());
	}

	@SuppressWarnings("unchecked")
	public void testRegisterCustomConverterArrayToListReverse() {
		DefaultConversionService service = new DefaultConversionService();
		service.addConverter("princy", new CustomTwoWayConverter());
		ConversionExecutor executor = service.getConversionExecutor("princy", Principal[].class, List.class);
		final Principal princy1 = new Principal() {
			public String getName() {
				return "princy1";
			}
		};
		final Principal princy2 = new Principal() {
			public String getName() {
				return "princy2";
			}
		};
		List<String> p = (List<String>) executor.execute(new Principal[] { princy1, princy2 });
		assertEquals("princy1", p.get(0));
		assertEquals("princy2", p.get(1));
	}

	public void testRegisterCustomConverterArrayToListBogus() {
		DefaultConversionService service = new DefaultConversionService();
		service.addConverter("princy", new CustomTwoWayConverter());
		try {
			service.getConversionExecutor("princy", Integer[].class, List.class);
			fail("Should have failed");
		} catch (ConversionExecutorNotFoundException e) {

		}
	}

	public void testRegisterCustomConverterListToArray() {
		DefaultConversionService service = new DefaultConversionService();
		service.addConverter("princy", new CustomTwoWayConverter());
		ConversionExecutor executor = service.getConversionExecutor("princy", List.class, Principal[].class);
		List<String> princyList = new ArrayList<String>();
		princyList.add("princy1");
		princyList.add("princy2");
		Principal[] p = (Principal[]) executor.execute(princyList);
		assertEquals("princy1", p[0].getName());
		assertEquals("princy2", p[1].getName());
	}

	public void testRegisterCustomConverterListToArrayReverse() {
		DefaultConversionService service = new DefaultConversionService();
		service.addConverter("princy", new CustomTwoWayConverter());
		ConversionExecutor executor = service.getConversionExecutor("princy", List.class, String[].class);
		final Principal princy1 = new Principal() {
			public String getName() {
				return "princy1";
			}
		};
		final Principal princy2 = new Principal() {
			public String getName() {
				return "princy2";
			}
		};
		List<Principal> princyList = new ArrayList<Principal>();
		princyList.add(princy1);
		princyList.add(princy2);
		String[] p = (String[]) executor.execute(princyList);
		assertEquals("princy1", p[0]);
		assertEquals("princy2", p[1]);
	}

	public void testRegisterCustomConverterListToArrayBogus() {
		DefaultConversionService service = new DefaultConversionService();
		service.addConverter("princy", new CustomTwoWayConverter());
		try {
			service.getConversionExecutor("princy", List.class, Integer[].class);
			fail("Should have failed");
		} catch (ConversionExecutorNotFoundException e) {

		}
	}

	public void testRegisterCustomConverterObjectToArray() {
		DefaultConversionService service = new DefaultConversionService();
		service.addConverter("princy", new CustomTwoWayConverter());
		ConversionExecutor executor = service.getConversionExecutor("princy", String.class, Principal[].class);
		Principal[] p = (Principal[]) executor.execute("princy1");
		assertEquals("princy1", p[0].getName());
	}

	public void testRegisterCustomConverterObjectToArrayReverse() {
		DefaultConversionService service = new DefaultConversionService();
		service.addConverter("princy", new CustomTwoWayConverter());
		ConversionExecutor executor = service.getConversionExecutor("princy", Principal.class, String[].class);
		final Principal princy1 = new Principal() {
			public String getName() {
				return "princy1";
			}
		};
		String[] p = (String[]) executor.execute(princy1);
		assertEquals("princy1", p[0]);
	}

	public void testRegisterCustomConverterObjectToArrayBogus() {
		DefaultConversionService service = new DefaultConversionService();
		service.addConverter("princy", new CustomTwoWayConverter());
		try {
			service.getConversionExecutor("princy", Integer.class, Principal[].class);
			fail("Should have failed");
		} catch (ConversionExecutorNotFoundException e) {

		}
	}

	@SuppressWarnings("unchecked")
	public void testRegisterCustomConverterObjectToList() {
		DefaultConversionService service = new DefaultConversionService();
		service.addConverter("princy", new CustomTwoWayConverter());
		ConversionExecutor executor = service.getConversionExecutor("princy", String.class, List.class);
		List<Principal> list = (List<Principal>) executor.execute("princy1");
		assertEquals("princy1", list.get(0).getName());
	}

	@SuppressWarnings("unchecked")
	public void testRegisterCustomConverterCsvStringToList() {
		DefaultConversionService service = new DefaultConversionService();
		service.addConverter("princy", new PrincipalCsvStringToListConverter());
		ConversionExecutor executor = service.getConversionExecutor("princy", String.class, List.class);
		List<Principal> list = (List<Principal>) executor.execute("princy1,princy2");
		assertEquals("princy1", list.get(0).getName());
		assertEquals("princy2", list.get(1).getName());
	}

	public void testRegisterCustomConverterObjectToListBogus() {
		DefaultConversionService service = new DefaultConversionService();
		service.addConverter("princy", new CustomTwoWayConverter());
		ConversionExecutor executor = service.getConversionExecutor("princy", Integer.class, List.class);
		try {
			executor.execute(new Integer(1));
			fail("Should have failed");
		} catch (ConversionExecutionException e) {

		}
	}

	@SuppressWarnings("unchecked")
	public void testRegisterCustomConverterObjectToListReverse() {
		DefaultConversionService service = new DefaultConversionService();
		service.addConverter("princy", new CustomTwoWayConverter());
		ConversionExecutor executor = service.getConversionExecutor("princy", Principal.class, List.class);
		final Principal princy1 = new Principal() {
			public String getName() {
				return "princy1";
			}
		};
		List<String> list = (List<String>) executor.execute(princy1);
		assertEquals("princy1", list.get(0));
	}

	@SuppressWarnings("unchecked")
	public void testRegisterCustomConverterListToList() {
		DefaultConversionService service = new DefaultConversionService();
		service.addConverter("princy", new CustomTwoWayConverter());
		ConversionExecutor executor = service.getConversionExecutor("princy", List.class, List.class);
		List<String> princyList = new ArrayList<String>();
		princyList.add("princy1");
		princyList.add("princy2");
		List<Principal> list = (List<Principal>) executor.execute(princyList);
		assertEquals("princy1", list.get(0).getName());
		assertEquals("princy2", list.get(1).getName());
	}

	@SuppressWarnings("unchecked")
	public void testRegisterCustomConverterListToListReverse() {
		DefaultConversionService service = new DefaultConversionService();
		service.addConverter("princy", new CustomTwoWayConverter());
		ConversionExecutor executor = service.getConversionExecutor("princy", List.class, List.class);
		final Principal princy1 = new Principal() {
			public String getName() {
				return "princy1";
			}
		};
		final Principal princy2 = new Principal() {
			public String getName() {
				return "princy2";
			}
		};
		List<Principal> princyList = new ArrayList<Principal>();
		princyList.add(princy1);
		princyList.add(princy2);
		List<String> list = (List<String>) executor.execute(princyList);
		assertEquals("princy1", list.get(0));
		assertEquals("princy2", list.get(1));
	}

	public void testRegisterCustomConverterListToListBogus() {
		DefaultConversionService service = new DefaultConversionService();
		service.addConverter("princy", new CustomTwoWayConverter());
		ConversionExecutor executor = service.getConversionExecutor("princy", List.class, List.class);
		List<Integer> princyList = new ArrayList<Integer>();
		princyList.add(1);
		try {
			executor.execute(princyList);
			fail("Should have failed");
		} catch (ConversionExecutionException e) {

		}
	}

	public void testConversionPrimitive() {
		DefaultConversionService service = new DefaultConversionService();
		ConversionExecutor executor = service.getConversionExecutor(String.class, int.class);
		Integer three = (Integer) executor.execute("3");
		assertEquals(3, three.intValue());
	}

	public void testArrayToArrayConversion() {
		DefaultConversionService service = new DefaultConversionService();
		ConversionExecutor executor = service.getConversionExecutor(String[].class, Integer[].class);
		Integer[] result = (Integer[]) executor.execute(new String[] { "1", "2", "3" });
		assertEquals(new Integer(1), result[0]);
		assertEquals(new Integer(2), result[1]);
		assertEquals(new Integer(3), result[2]);
	}

	public void testArrayToArrayPrimitiveConversion() {
		DefaultConversionService service = new DefaultConversionService();
		ConversionExecutor executor = service.getConversionExecutor(String[].class, int[].class);
		int[] result = (int[]) executor.execute(new String[] { "1", "2", "3" });
		assertEquals(1, result[0]);
		assertEquals(2, result[1]);
		assertEquals(3, result[2]);
	}

	@SuppressWarnings("unchecked")
	public void testArrayToListConversion() {
		DefaultConversionService service = new DefaultConversionService();
		ConversionExecutor executor = service.getConversionExecutor(String[].class, List.class);
		List<String> result = (List<String>) executor.execute(new String[] { "1", "2", "3" });
		assertEquals("1", result.get(0));
		assertEquals("2", result.get(1));
		assertEquals("3", result.get(2));
	}

	public void testListToArrayConversion() {
		DefaultConversionService service = new DefaultConversionService();
		ConversionExecutor executor = service.getConversionExecutor(Collection.class, String[].class);
		List<String> list = new ArrayList<String>();
		list.add("1");
		list.add("2");
		list.add("3");
		String[] result = (String[]) executor.execute(list);
		assertEquals("1", result[0]);
		assertEquals("2", result[1]);
		assertEquals("3", result[2]);
	}

	@SuppressWarnings("unchecked")
	public void testSetToListConversion() {
		DefaultConversionService service = new DefaultConversionService();
		ConversionExecutor executor = service.getConversionExecutor(Set.class, List.class);
		Set<String> set = new LinkedHashSet<String>();
		set.add("1");
		set.add("2");
		set.add("3");
		List<String> result = (List<String>) executor.execute(set);
		assertEquals("1", result.get(0));
		assertEquals("2", result.get(1));
		assertEquals("3", result.get(2));
	}

	public void testListToArrayConversionWithComponentConversion() {
		try {
			DefaultConversionService service = new DefaultConversionService();
			service.getConversionExecutor(Collection.class, Integer[].class);

			// This test case is no longer supported:
			// https://jira.springframework.org/browse/SPR-7496

			// List list = new ArrayList();
			// list.add("1");
			// list.add("2");
			// list.add("3");
			// Integer[] result = (Integer[]) executor.execute(list);
			// assertEquals(new Integer(1), result[0]);
			// assertEquals(new Integer(2), result[1]);
			// assertEquals(new Integer(3), result[2]);

		} catch (ConversionExecutorNotFoundException e) {
			// This is expected
		}
	}

	@SuppressWarnings("unchecked")
	public void testArrayToLinkedListConversion() {
		DefaultConversionService service = new DefaultConversionService();
		ConversionExecutor executor = service.getConversionExecutor(String[].class, LinkedList.class);
		LinkedList<String> result = (LinkedList<String>) executor.execute(new String[] { "1", "2", "3" });
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

	public void testStringToArrayConversion() {
		DefaultConversionService service = new DefaultConversionService();
		ConversionExecutor executor = service.getConversionExecutor(String.class, String[].class);
		String[] result = (String[]) executor.execute("1,2,3");
		assertEquals(3, result.length);
		assertEquals("1", result[0]);
		assertEquals("2", result[1]);
		assertEquals("3", result[2]);
	}

	@SuppressWarnings("unchecked")
	public void testStringToListConversion() {
		DefaultConversionService service = new DefaultConversionService();
		ConversionExecutor executor = service.getConversionExecutor(String.class, List.class);
		List<String> result = (List<String>) executor.execute("1,2,3");
		assertEquals(3, result.size());
		assertEquals("1", result.get(0));
		assertEquals("2", result.get(1));
		assertEquals("3", result.get(2));
	}

	public void testStringToArrayConversionWithElementConversion() {
		DefaultConversionService service = new DefaultConversionService();
		ConversionExecutor executor = service.getConversionExecutor(String.class, Integer[].class);
		Integer[] result = (Integer[]) executor.execute("123");
		assertEquals(1, result.length);
		assertEquals(new Integer(123), result[0]);
	}

	private static class CustomConverter implements Converter {

		public Object convertSourceToTargetClass(final Object source, Class<?> targetClass) throws Exception {
			return new Principal() {
				public String getName() {
					return (String) source;
				}
			};
		}

		public Class<?> getSourceClass() {
			return String.class;
		}

		public Class<?> getTargetClass() {
			return Principal.class;
		}

	}

	private static class CustomTwoWayConverter extends CustomConverter implements TwoWayConverter {
		public Object convertTargetToSourceClass(Object target, Class<?> sourceClass) throws Exception {
			return ((Principal) target).getName();
		}
	}

	private static class Trimmer implements Converter {

		public Object convertSourceToTargetClass(Object source, Class<?> targetClass) throws Exception {
			return ((String) source).trim();
		}

		public Class<?> getSourceClass() {
			return String.class;
		}

		public Class<?> getTargetClass() {
			return String.class;
		}

	}

	private static class PrincipalCsvStringToListConverter extends StringToObject {

		public PrincipalCsvStringToListConverter() {
			super(List.class);
		}

		protected Object toObject(String string, Class<?> targetClass) throws Exception {
			List<Principal> principals = new ArrayList<Principal>();
			StringTokenizer tokenizer = new StringTokenizer(string, ",");
			while (tokenizer.hasMoreTokens()) {
				final String name = tokenizer.nextToken();
				principals.add(new Principal() {
					public String getName() {
						return name;
					}
				});
			}
			return principals;
		}

		protected String toString(Object object) throws Exception {
			throw new UnsupportedOperationException("No implemented");
		}

	}

}