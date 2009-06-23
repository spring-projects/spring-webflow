package org.springframework.faces.ui;

import junit.framework.TestCase;

public class EscapeQuotesTests extends TestCase {

	public final void testEscapeQuotesInLink() {

		String linkText = "<a id=\"mainForm:findHotels\" class=\"progressiveLink\" href=\"#\" name=\"mainForm:findHotels\"\\>";
		String expectedText = "<a id=\\\"mainForm:findHotels\\\" class=\\\"progressiveLink\\\" href=\\\"#\\\" name=\\\"mainForm:findHotels\\\"\\>";
		String result = linkText.replaceAll("\"", "\\\\\"");
		System.out.println(linkText);
		System.out.println(result);
		assertEquals(expectedText, result);
	}
}
