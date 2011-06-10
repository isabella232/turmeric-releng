package org.ebayopensource.turmeric.junit.rules;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.apache.commons.io.FilenameUtils;
import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;
import org.junit.rules.MethodRule;
import org.junit.rules.TemporaryFolder;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * Useful for working with testcases that need a temporary testing
 * directory.
 * <p>
 * Differs from {@link TemporaryFolder} junit rule.
 * <p>
 * Creates a directory, when asked (via {@#getDir()} or {@#getEmptyDir()})
 * in the maven project familiar and friendly location:
 * <code>${basedir}/target/tests/${testclass}/${testmethod}</code>.
 * <p>
 * Note: existing facilities within {@link MavenTestingUtils} for keeping
 * the directory name short for the sake of windows users is being used.
 */
public class TestingDir implements MethodRule {
	private File dir;

	public Statement apply(final Statement statement, final FrameworkMethod method,
			final Object target) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				dir = MavenTestingUtils.getTargetTestingDir(target.getClass(),
						method.getName());
				MavenTestingUtils.ensureEmpty(dir);
				statement.evaluate();
			}
		};
	}

	public File getDir() {
		if (dir.exists()) {
			return dir;
		}

		Assert.assertTrue("Creating testing dir", dir.mkdirs());
		return dir;
	}
	
	public File getFile(String name) {
		return new File(dir, FilenameUtils.separatorsToSystem(name));
	}
	
	public void ensureEmpty() throws IOException {
		MavenTestingUtils.ensureEmpty(dir);
	}
	
	public File getEmptyDir() throws IOException {
		if (dir.exists()) {
			MavenTestingUtils.ensureEmpty(dir);
			return dir;
		}

		Assert.assertTrue("Creating testing dir", dir.mkdirs());
		return dir;
	}
}
