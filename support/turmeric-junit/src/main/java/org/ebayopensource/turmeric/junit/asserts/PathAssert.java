package org.ebayopensource.turmeric.junit.asserts;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.SystemUtils;
import org.junit.Assert;

public class PathAssert {
	public static void assertDirEmpty(String msg, File dir) {
		assertDirExists(msg, dir);

		File[] content = dir.listFiles();
		if (content == null) {
			return;
		}
		if (content.length == 0) {
			return;
		}

		StringBuilder buf = new StringBuilder();
		buf.append(msg).append(" directory is not empty.");
		for (File path : content) {
			buf.append(SystemUtils.LINE_SEPARATOR);
			buf.append("  ").append(path.getName());
		}
		Assert.fail(buf.toString());
	}

	public static void assertDirExists(File path) {
		assertDirExists("Path", path);
	}

	public static void assertDirExists(String msg, File path) {
		assertExists(msg, path);
		Assert.assertTrue(
				msg + " path should be a Dir : " + path.getAbsolutePath(),
				path.isDirectory());
	}

	public static void assertDirNotEmpty(String msg, File dir) {
		assertDirExists(msg, dir);

		File[] content = dir.listFiles();
		Assert.assertTrue(msg + " dir is empty, but should contain content",
				((content != null) && (content.length > 0)));
	}

	public static void assertExists(String msg, File path) {
		Assert.assertTrue(
				msg + " path should exist: " + path.getAbsolutePath(),
				path.exists());
	}

	public static void assertFileCount(String msg, int expectedCount, File dir,
			String extension) {
		int actualCount = countFiles(dir, extension);
		Assert.assertEquals(msg, expectedCount, actualCount);
	}

	public static void assertFileExists(File path) {
		Assert.assertTrue("Path should exist: " + path, path.exists());
		Assert.assertTrue("Path is not a file: " + path, path.isFile());
	}

	public static File assertFileExists(File basedir, String filename) {
		File file = new File(basedir, FilenameUtils.separatorsToSystem(filename));
		assertFileExists(file);
		return file;
	}

	public static void assertFileExists(String msg, File path) {
		assertExists(msg, path);
		Assert.assertTrue(
				msg + " path should be a File : " + path.getAbsolutePath(),
				path.isFile());
	}

	public static void assertSubdirExists(File basedir, String subdirname) {
		File path = new File(basedir, subdirname);
		Assert.assertTrue("Path should exist: " + path, path.exists());
		Assert.assertTrue("Path is not a directory: " + path,
				path.isDirectory());
	}

	public static void assertSubdirNotExists(File basedir, String subdirname) {
		File path = new File(basedir, subdirname);
		Assert.assertFalse("Directory should NOT EXIST: " + path, path.exists());
	}

	public static int countFiles(File dir, String extension) {
		return recurseCountFiles(0, dir, extension);
	}

	public static int recurseCountFiles(int startcount, File dir,
			String extension) {
		int count = startcount;
		for (File path : dir.listFiles()) {
			if (path.isDirectory()) {
				count = recurseCountFiles(count, path, extension);
			} else if (path.isFile()) {
				if (path.getName().endsWith(extension)) {
					count++;
				}
			}
		}
		return count;
	}

}
