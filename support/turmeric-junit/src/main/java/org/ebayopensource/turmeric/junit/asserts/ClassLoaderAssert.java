package org.ebayopensource.turmeric.junit.asserts;

import java.net.URL;

import org.junit.Assert;

public class ClassLoaderAssert {
	public static void assertResourcePresent(String msg, String resourceName) {
		URL url = Thread.currentThread().getContextClassLoader()
				.getResource(resourceName);
		Assert.assertNotNull(msg + ": resource not present in classloader: "
				+ resourceName, url);
	}

	public static void assertClassPresent(String fqclassname) throws ClassNotFoundException {
		Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(fqclassname);
		Assert.assertNotNull("Should have found class in current ClassLoader: " + fqclassname, clazz);
	}
}
