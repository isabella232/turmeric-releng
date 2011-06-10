package org.ebayopensource.turmeric.junit.asserts;

import java.io.StringReader;
import java.io.StringWriter;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.Assert;

public final class XmlAssert {
	private XmlAssert() {
		/* do nothing */
	}

	/**
	 * Compare two XML strings, report failure in a human readable form.
	 * 
	 * @param expected
	 *            the control to check against.
	 * @param actual
	 *            the actual xml to check.
	 * @throws Exception
	 *             if XML in expected or actual is invalid.
	 */
	public static void assertEquals(String expected, String actual)
			throws Exception {
		// Speedy check first.
		if (expected.equals(actual)) {
			// It's the same.
			return;
		}

		Assert.assertEquals(formatted(expected), formatted(actual));
	}

	/**
	 * Format a raw xml into one that's human readable.
	 * 
	 * @param rawxml
	 *            the raw xml
	 * @return the human readable xml
	 * @throws Exception
	 *             if raw XML is invalid.
	 */
	public static String formatted(String rawxml) throws Exception {
		SAXBuilder builder = new SAXBuilder(false);
		Document doc = builder.build(new StringReader(rawxml));

		StringWriter writer = new StringWriter(rawxml.length());
		XMLOutputter serializer = new XMLOutputter();
		serializer.setFormat(Format.getPrettyFormat());
		serializer.output(doc, writer);

		return writer.toString();
	}
}
