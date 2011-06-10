package org.ebayopensource.turmeric.junit.rules;

import static org.hamcrest.Matchers.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.ebayopensource.turmeric.junit.asserts.PathAssert;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 * Simple tests for the {@link TestingDir} junit {@link Rule}
 */
public class TestingDirTest {
	@Rule
	public TestingDir testingdir = new TestingDir();

	@Test
	public void testDir() {
		File dir = testingdir.getDir();
		Assert.assertThat("Dir", dir, notNullValue());
		PathAssert.assertDirExists("Dir", dir);

		String expected = File.separator + "target" + File.separator + "tests" + File.separator;
		Assert.assertThat("Dir.name", dir.getAbsolutePath(), containsString(expected));
	}

	@Test
	public void testEmptyDir() throws IOException {
		File dir = testingdir.getEmptyDir();
		Assert.assertThat("Dir", dir, notNullValue());
		PathAssert.assertDirExists("Dir", dir);
		PathAssert.assertDirEmpty("Dir", dir);

		String expected = File.separator + "target" + File.separator + "tests" + File.separator;
		Assert.assertThat("Dir.name", dir.getAbsolutePath(), containsString(expected));

		// Create some content.
		String blather = "Eatagramovabits";
		FileUtils.writeStringToFile(new File(dir, "something.txt"), blather);

		PathAssert.assertDirNotEmpty("Dir", dir);

		// Get the dir again.
		File dir2 = testingdir.getEmptyDir();
		Assert.assertThat("Dir (again)", dir2, notNullValue());
		PathAssert.assertDirExists("Dir (again)", dir2);
		PathAssert.assertDirEmpty("Dir (again)", dir2);
	}
}
