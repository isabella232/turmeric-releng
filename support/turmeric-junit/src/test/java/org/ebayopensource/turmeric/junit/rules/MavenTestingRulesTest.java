package org.ebayopensource.turmeric.junit.rules;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.ebayopensource.turmeric.junit.logging.SimpleConsoleHandler;
import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

public class MavenTestingRulesTest {
	@BeforeClass
	public static void initLogging() {
		SimpleConsoleHandler.init();
		
		dumpEnv();
		dumpSysProperties();
	}
	
	@SuppressWarnings("unchecked")
	private static void dumpSysProperties() {
		Properties sysprops = System.getProperties();
		List<String> sortedkeys = new ArrayList<String>();
		sortedkeys.addAll((Collection<String>) Collections.list(sysprops.propertyNames()));
		Collections.sort(sortedkeys);
		for(String key: sortedkeys) {
			System.out.printf("System.getProperty(\"%s\") = %s%n", key, sysprops.getProperty(key));
		}
	}

	private static void dumpEnv() {
		Map<String,String> env = System.getenv();
		List<String> sortedkeys = new ArrayList<String>();
		sortedkeys.addAll(env.keySet());
		Collections.sort(sortedkeys);
		for(String key: sortedkeys) {
			System.out.printf("ENV[\"%s\"]: %s%n", key, env.get(key));
		}
	}

	@Rule
	public MavenTestingRules mavenrules = new MavenTestingRules();

	@Test
	public void testReadList() {
		mavenrules.dumpReadPrefixes();
	}
	
	@Test
	public void testSafeReadFromTemp() throws IOException {
		mavenrules.setStrictReadPaths(true);
		mavenrules.setFailOnViolation(true);
		
		String javaiotmpdir = System.getProperty("java.io.tmpdir");
		File f = new File(javaiotmpdir + File.separator + "bogus.txt");
		if(f.exists()) {
			f.delete();
		}
		f.createNewFile();
	}
	
	@Test
	public void testSafeReadWriteFromFileURI() throws IOException, URISyntaxException {
		mavenrules.setStrictReadPaths(true);
		mavenrules.setFailOnViolation(true);
		
		File testdir = MavenTestingUtils.getTargetTestingDir();
		MavenTestingUtils.ensureEmpty(testdir);
		URI uri = new File(testdir, "bogus.txt").toURI();
		
		String url = uri.toASCIIString();
		Assert.assertTrue("URL should start with \"file:\": " + url, url.startsWith("file:"));
		
		File f = new File(new URI(url));
		if(f.exists()) {
			f.delete();
		}
		FileUtils.writeStringToFile(f, "Sample Content");
	}
	
	@Test
	public void testSafeRead() throws IOException {
		mavenrules.setStrictReadPaths(true);
		mavenrules.setFailOnViolation(true);
		
		File testdir = MavenTestingUtils.getTargetTestingDir();
		MavenTestingUtils.ensureEmpty(testdir);
		
		File f = new File(testdir, "testRead.txt");
		if(f.exists()) {
			f.delete();
		}
		f.createNewFile();
	}
	
	@Test
	public void testSafeWrite() throws IOException {
		mavenrules.setStrictWritePaths(true);
		mavenrules.setFailOnViolation(true);
		
		File testdir = MavenTestingUtils.getTargetTestingDir();
		MavenTestingUtils.ensureEmpty(testdir);
		
		File f = new File(testdir, "testWrite.txt");
		FileUtils.writeStringToFile(f, "Some data");
	}
	
	@Test
	public void testSafeReflection() throws Exception {
		Class.forName("org.apache.commons.lang.Entities", false, this.getClass().getClassLoader());
	}
	
	@Test
	public void testBadRead() {
		File basedir = MavenTestingUtils.getBasedir();
		File parent = basedir.getParentFile();
		File list[] = parent.listFiles();
		Assert.assertNotNull(list);
	}
}
