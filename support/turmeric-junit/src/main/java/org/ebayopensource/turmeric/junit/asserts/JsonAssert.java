package org.ebayopensource.turmeric.junit.asserts;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;

public final class JsonAssert {
	private JsonAssert() {
		/* prevent instantiation */
	}

	/**
	 * Compare two JSON strings, as a JSON Object,
	 * report failure in a human readable form.
	 * <p>
	 * Will see past minor formatting and key order scenarios
	 * that might crop up when comparing two json strings.
	 * <p>
	 * Key order is irrelevant.
	 * 
	 * @param expected
	 *            the control to check against.
	 * @param actual
	 *            the actual JSON to check.
	 * @throws Exception
	 *             if JSON in expected or actual is invalid.
	 */
	public static void assertJsonObjectEquals(String expected, String actual)
			throws Exception {
		// Speedy check first.
		if (expected.equals(actual)) {
			// It's the same.
			return;
		}
		
		// Use json.org JSONObject, as it sorts by key.
		JSONObject expectedJson = new JSONObject(expected);
		JSONObject actualJson = new JSONObject(actual);

		Assert.assertEquals(expectedJson.toString(2), actualJson.toString(2));
	}
	
	/**
	 * Compare two JSON strings, as JSON Arrays, 
	 * report failure in a human readable form.
	 * 
	 * @param expected
	 *            the control to check against.
	 * @param actual
	 *            the actual JSON to check.
	 * @throws Exception
	 *             if JSON in expected or actual is invalid.
	 */
	public static void assertJsonArrayEquals(String expected, String actual)
			throws Exception {
		// Speedy check first.
		if (expected.equals(actual)) {
			// It's the same.
			return;
		}
		
		// Use json.org JSONArray to be consistent
		// There is no sorting with in a list, order is preserved
		JSONArray expectedJson = new JSONArray(expected);
		JSONArray actualJson = new JSONArray(actual);

		Assert.assertEquals(expectedJson.toString(2), actualJson.toString(2));
	}
}
