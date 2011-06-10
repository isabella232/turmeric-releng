package org.ebayopensource.turmeric.junit.logging;

import java.io.File;

import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;

public class UKernelLoggingUtils {
	
	/**
	 * Simple tweak to set the SOATools / SOAClient / SOAServer logging
	 * done via uKernelCore's logging to always produce file output
	 * in the project's target/ directory.
	 */
	public static final void initTesting()
	{
		File targetDir = MavenTestingUtils.getTargetDir();
		File testLoggingDir = new File(targetDir, "testing-logs");
		MavenTestingUtils.ensureDirExists(testLoggingDir);
		
		initTesting(testLoggingDir);
	}
	
	/**
	 * Simple tweak to set the SOATools / SOAClient / SOAServer logging
	 * done via uKernelCore's logging to produce file output
	 * in the specified dir
	 */
	public static final void initTesting(File dir)
	{
		System.setProperty("com.ebay.log.dir", dir.getAbsolutePath());
	}
}
