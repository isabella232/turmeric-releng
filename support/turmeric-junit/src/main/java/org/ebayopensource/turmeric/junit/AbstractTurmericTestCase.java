package org.ebayopensource.turmeric.junit;

import org.ebayopensource.turmeric.junit.logging.SimpleConsoleHandler;
import org.ebayopensource.turmeric.junit.logging.UKernelLoggingUtils;
import org.ebayopensource.turmeric.junit.rules.MavenTestingRules;
import org.ebayopensource.turmeric.junit.rules.TestTracer;
import org.junit.BeforeClass;
import org.junit.Rule;

/**
 * Top level test case for all test within Turmeric.
 */
public abstract class AbstractTurmericTestCase {
	/**
	 * Ensure logging is sane during tests.
	 */
	@BeforeClass
	public static void initLogging() {
		SimpleConsoleHandler.init();
		UKernelLoggingUtils.initTesting();
	}
	
	/**
	 * Capture and trace all of the test cases.
	 */
	@Rule
	public TestTracer tracer = new TestTracer();
	
	/**
	 * Ensure we are following the rules with junit testing on maven.
	 */
	@Rule
	public MavenTestingRules mavenTestingRules = new MavenTestingRules();
}
