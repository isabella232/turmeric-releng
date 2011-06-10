package org.ebayopensource.turmeric.junit.verifiers;

import static org.hamcrest.Matchers.*;

import java.io.File;

import org.apache.commons.lang.SystemUtils;
import org.ebayopensource.turmeric.junit.AbstractTurmericTestCase;
import org.ebayopensource.turmeric.junit.logging.UKernelLoggingUtils;
import org.ebayopensource.turmeric.junit.rules.TestingDir;
import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;
import org.junit.Assert;
import org.junit.rules.Verifier;

/**
 * Verify that the output does not go into the root of the project.
 * Any output produced by the test cases should go into the target
 * directory.
 * <p>
 * Use the @Rule {@link TestingDir#getDir()} to work with test
 * specific temp directories.
 */
public class BadOutputVerifier extends Verifier {
	@Override
	protected void verify() throws Throwable {
		File basedir = MavenTestingUtils.getBasedir();
		// Test for known bad output from codegen tools
		String msg = "Do not generate content into the project root,"
				+ " Use the @Rule " + TestingDir.class.getName()
				+ ".getDir() to" + " work with test specific temp directories";
		assertShouldNotExist(msg, basedir, "gen-meta-src");
		assertShouldNotExist(msg, basedir, "bin");
		assertShouldNotExist(msg, basedir, "common-project-root");
		
		// Test for known bad output from logging
		msg = "Do not let logs be generated in the project root," + " use "
				+ UKernelLoggingUtils.class.getName()
				+ ".setSOALoggingToProjectOutputDirectory()"
				+ " or extend from " + AbstractTurmericTestCase.class.getName();
		assertShouldNotExist(msg, basedir, "DiffBasedSOAMetrics-client.log");
		assertShouldNotExist(msg, basedir, "DiffBasedSOAMetrics.log");
		assertShouldNotExist(msg, basedir, "ebay.log");
		
		// Test for obvious bad output
		for(File path: basedir.listFiles()) {
			if(SystemUtils.IS_OS_UNIX) {
				Assert.assertThat("Windows style paths detected: " +
						path.getName(), not(containsString("\\")));
			}
		}
		super.verify();
	}
	
	protected void assertShouldNotExist(String msg, File basedir, String name)
	{
		String fulltest = "${project.basedir}" + File.separator + name;
		File path = new File(basedir, name);
		Assert.assertFalse(msg + ": " + fulltest, path.exists());
	}
}
