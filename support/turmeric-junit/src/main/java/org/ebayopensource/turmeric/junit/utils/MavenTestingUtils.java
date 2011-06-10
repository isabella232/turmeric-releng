package org.ebayopensource.turmeric.junit.utils;

import static org.hamcrest.Matchers.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.ebayopensource.turmeric.junit.asserts.PathAssert;
import org.ebayopensource.turmeric.junit.rules.TestingDir;
import org.junit.Assert;
import org.junit.rules.TestName;

public class MavenTestingUtils {
    private static File basedir;
    private static File mainResourcesDir;
    private static File testResourcesDir;
    private static File targetDir;

    // private static Boolean surefireRunning;

    public static File getBasedir()
    {
        if (basedir == null)
        {
            String cwd = System.getProperty("basedir");

            if (cwd == null)
            {
                cwd = System.getProperty("user.dir");
            }

            basedir = new File(cwd);
        }

        return basedir;
    }

    /**
     * Get the directory to the /target directory for this project.
     * 
     * @return the directory path to the target directory.
     */
    public static File getTargetDir()
    {
        if (targetDir == null)
        {
            targetDir = new File(getBasedir(),"target");
            PathAssert.assertDirExists("Target Dir",targetDir);
        }
        return targetDir;
    }

    /**
     * Create a {@link File} object for a path in the /target directory.
     * 
     * @param path
     *            the path desired, no validation of existence is performed.
     * @return the File to the path.
     */
    public static File getTargetFile(String path)
    {
        return new File(getTargetDir(),path.replace("/",File.separator));
    }

    /**
     * Using test id lookup {{@link #getTestID()} create a new
     * test specific testing directory to use for the specific test
     * 
     * @return the test specific testing dir
     */
    public static File getTargetTestingDir()
    {
    	File testsDir = new File(getTargetDir(), "tests");
        File dir = new File(testsDir,getTestIDAsPath());
        if (!dir.exists())
        {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * Get a dir in /target/ that uses the JUnit 3.x {@link TestCase#getName()} to make itself unique.
     * 
     * @param test
     *            the junit 3.x testcase to base this new directory on.
     * @return the File path to the testcase specific testing directory underneath the 
     *            <code>${basedir}/target</code> sub directory
     */
    public static File getTargetTestingDir(TestCase test)
    {
        return getTargetTestingDir(test.getName());
    }

    /**
     * Get a dir in /target/ that uses the an arbitrary name.
     * 
     * @param testname
     *            the testname to create directory against.
     * @return the File path to the testname sepecific testing directory underneath the
     *            <code>${basedir}/target</code> sub directory
     */
    public static File getTargetTestingDir(String testname)
    {
		File testsDir = new File(getTargetDir(), "tests");
		File dir = new File(testsDir, "test-" + testname);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
    }
    
    /**
     * Get a dir in /target/ that uses the an arbitrary name.
     * 
     * @param testname
     *            the testname to create directory against.
     * @return the File path to the testname sepecific testing directory underneath the
     *            <code>${basedir}/target</code> sub directory
     */
    public static File getTargetTestingDir(TestName testname)
    {
    	return getTargetTestingDir(testname.getMethodName());
    }

	/**
	 * Get a dir in /target/ that uses the an arbitrary name.
	 * 
	 * @param testcase
	 *            the object holding the test case.
	 * @param testname
	 *            the testname to create directory against.
	 * @return the File path to the testname sepecific testing directory underneath the <code>${basedir}/target</code>
	 *         sub directory
	 */
    public static File getTargetTestingDir(Object testcase, TestName testname)
    {
		return getTargetTestingDir(testcase.getClass(),
				testname.getMethodName());
    }

	/**
	 * Get a dir in /target/ that uses the an arbitrary name.
	 * <p>
	 * Best if used with {@link TestingDir} junit rule.
	 * <pre>
	 *   @Rule public TestingDir testdir = new TestingDir();
	 *   
	 *   @Test
	 *   public void testFoo() {
	 *     Assert.assertTrue("Testing dir exists", testdir.getDir().exists());
	 *   }
	 * </pre>
	 * 
	 * @param testclass
	 *            the class for the test case
	 * @param testmethodname
	 *            the test method name
	 * @return the File path to the testname sepecific testing directory underneath the <code>${basedir}/target</code>
	 *         sub directory
	 */
    public static File getTargetTestingDir(final Class<?> testclass, 
    		final String testmethodname)
    {
    	String classname = testclass.getName();
    	String methodname = testmethodname;
    	
    	classname = condensePackageString(classname);

		// Be friendly when the developer is on Windows.
		if(SystemUtils.IS_OS_WINDOWS) {
			/* Condense the directory names to make them
			 * more friendly for the limitations that exist
			 * on windows.
			 */ 
			methodname = maxStringLength(30,methodname);
		}
		
		File testsDir = new File(getTargetDir(), "tests");
		File dir = new File(testsDir, classname + File.separatorChar
				+ methodname);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
    }

    /**
     * Get a dir from the src/test/resource directory.
     * 
     * @param name
     *            the name of the path to get (it must exist as a dir)
     * @return the dir in src/test/resource
     */
    public static File getTestResourceDir(String name)
    {
        File dir = new File(getTestResourcesDir(),FilenameUtils.separatorsToSystem(name));
        PathAssert.assertDirExists("Test Resource Dir",dir);
        return dir;
    }

    /**
     * Get a file from the src/test/resource directory.
     * 
     * @param name
     *            the name of the path to get (it must exist as a file)
     * @return the file in src/test/resource
     */
    public static File getTestResourceFile(String name)
    {
        File file = new File(getTestResourcesDir(),FilenameUtils.separatorsToSystem(name));
        PathAssert.assertFileExists("Test Resource File",file);
        return file;
    }

    /**
     * Get a path resource (File or Dir) from the src/test/resource directory.
     * 
     * @param name
     *            the name of the path to get (it must exist)
     * @return the path in src/test/resource
     */
    public static File getTestResourcePath(String name)
    {
        File path = new File(getTestResourcesDir(),FilenameUtils.separatorsToSystem(name));
        PathAssert.assertExists("Test Resource Path",path);
        return path;
    }

    /**
     * Get the directory to the src/test/resource directory
     * 
     * @return the directory {@link File} to the src/test/resources directory
     */
    public static File getTestResourcesDir()
    {
        if (testResourcesDir == null)
        {
        	String path = FilenameUtils.separatorsToSystem("src/test/resources");
            testResourcesDir = new File(basedir,path);
        }
        PathAssert.assertDirExists("Test Resources Dir",testResourcesDir);
        return testResourcesDir;
    }
    
    public static boolean hasTestResourcesDir() {
    	if (testResourcesDir == null)
        {
        	String path = FilenameUtils.separatorsToSystem("src/test/resources");
            testResourcesDir = new File(basedir,path);
        }
        return testResourcesDir.exists();
    }
    
    /**
     * Get a file from the src/main/resource directory.
     * 
     * @param name
     *            the name of the path to get (it must exist as a file)
     * @return the file in src/main/resource
     */
    public static File getMainResourceFile(String name)
    {
        File file = new File(getMainResourcesDir(),FilenameUtils.separatorsToSystem(name));
        PathAssert.assertFileExists("Main Resource File",file);
        return file;
    }

    /**
     * Get the directory to the src/main/resource directory
     * 
     * @return the directory {@link File} to the src/main/resources directory
     */
    public static File getMainResourcesDir()
    {
        if (mainResourcesDir == null)
        {
        	String path = FilenameUtils.separatorsToSystem("src/main/resources");
        	mainResourcesDir = new File(basedir,path);
            PathAssert.assertDirExists("Main Resources Dir",mainResourcesDir);
        }
        return mainResourcesDir;
    }

    /**
     * Read the contents of a file into a String and return it.
     * 
     * @param file
     *            the file to read.
     * @return the contents of the file.
     * @throws IOException
     *             if unable to read the file.
     */
    public static String readToString(File file) throws IOException
    {
        FileReader reader = null;
        try
        {
            reader = new FileReader(file);
            return IOUtils.toString(reader);
        }
        finally
        {
            IOUtils.closeQuietly(reader);
        }
    }
    
    public static class TestID
    {
    	public String classname;
    	public String methodname;
    }
    
    public static String getTestIDAsPath()
    {
    	TestID id = getTestID();
		
    	id.classname = condensePackageString(id.classname);

		// Be friendly when the developer is on Windows.
		if(SystemUtils.IS_OS_WINDOWS) {
			/* Condense the directory names to make them
			 * more friendly for the limitations that exist
			 * on windows.
			 */ 
			id.methodname = maxStringLength(30,id.methodname);
		}
		
		return id.classname + File.separatorChar + id.methodname;
    }

    public static TestID getTestID()
    {
        StackTraceElement stacked[] = new Throwable().getStackTrace();
        
		for (StackTraceElement stack : stacked) {
			if (stack.getClassName().endsWith("Test")
					|| stack.getClassName().endsWith("Tests")) {
				if (stack.getMethodName().startsWith("test")) {
					TestID testid = new TestID();
					testid.classname = stack.getClassName();
					testid.methodname = stack.getMethodName();

					return testid;
				}
			}
		}

		// If we have reached this point, we have failed to find the test id
		StringBuilder err = new StringBuilder();
    	err.append("Unable to find a TestID from a testcase that ");
    	err.append("doesn't follow the standard naming rules.");
    	err.append(SystemUtils.LINE_SEPARATOR);
    	err.append("Test class name must end in \"*Test\" or \"*Tests\".");
    	err.append(SystemUtils.LINE_SEPARATOR);
    	err.append("Test method name must start in \"test*\".");
    	err.append(SystemUtils.LINE_SEPARATOR);
    	err.append("Call to ").append(MavenTestingUtils.class.getSimpleName());
    	err.append(".getTestID(), must occur from within stack frame of ");
    	err.append("test method, not @Before, @After, @BeforeClass, ");
    	err.append("@AfterClass, or Constructors of test case.");
    	Assert.fail(err.toString());
    	return null;
    }
    
	public static String condensePackageString(String classname) {
		String parts[] = StringUtils.split(classname, '.');
		StringBuilder dense = new StringBuilder();
		for (int i = 0; i < (parts.length - 1); i++) {
			dense.append(parts[i].charAt(0));
		}
		dense.append('.').append(parts[parts.length - 1]);
		return dense.toString();
	}
    
    public static String maxStringLength(int max, String raw) {
    	int length = raw.length();
    	if(length <= max) {
    		return raw;
    	}
    	
    	return raw.substring(0,3) + "..." + raw.substring((length-max)+6);
    }

    /**
     * Ensure the provided directory exists, and contains no content (empty)
     * 
     * @param dir the dir to check.
     * @throws IOException
     */
	public static void ensureEmpty(File dir) throws IOException {
		if(dir.exists()) {
			FileUtils.cleanDirectory(dir);
		} else {
			Assert.assertTrue("Creating dir: " + dir, dir.mkdirs());
		}
	}
	
	/**
     * Ensure the provided directory exists, and contains no content (empty)
     * 
     * @param testingdir the dir to check.
     * @throws IOException
     */
	public static void ensureEmpty(TestingDir testingdir) throws IOException {
		ensureEmpty(testingdir.getDir());
	}

	/**
	 * Ensure the provided directory does not exist, delete it if present
	 * 
	 * @param dir
	 * @throws IOException
	 */
	public static void ensureDeleted(File dir) throws IOException {
		File targetDir = getTargetDir();
		Assert.assertThat("Can only delete content within the target directory", dir.getAbsolutePath(), startsWith(targetDir.getAbsolutePath()));
		if (dir.exists()) {
			FileUtils.deleteDirectory(dir);
		}
	}

	/**
	 * Ensure that directory exists, create it if not present.
	 * Leave it alone if already there.
	 * 
	 * @param dir the dir to check.
	 */
	public static void ensureDirExists(File dir) {
		if(!dir.exists()) {
			Assert.assertTrue("Creating dir: " + dir, dir.mkdirs());
		}
	}

	public static File getProjectFile(String path) {
		File file = new File(getBasedir(), FilenameUtils.separatorsToSystem(path));
		PathAssert.assertFileExists("Project File", file);
		return file;
	}

	public static File getProjectDir(String path) {
		File dir = new File(getBasedir(), FilenameUtils.separatorsToSystem(path));
		PathAssert.assertDirExists("Project Dir", dir);
		return dir;
	}

}
