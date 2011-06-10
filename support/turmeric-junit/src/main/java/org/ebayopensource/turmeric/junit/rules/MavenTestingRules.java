package org.ebayopensource.turmeric.junit.rules;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;
import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * Junit {@link Rule} to ensure that various filesystem rules with regards to unit testing under maven are followed.
 */
public class MavenTestingRules implements MethodRule {
	@SuppressWarnings("serial")
	protected static class MavenTestingException extends SecurityException {
		public MavenTestingException() {
			super();
		}

		public MavenTestingException(String s) {
			super(s);
		}

		public MavenTestingException(String message, Throwable cause) {
			super(message, cause);
		}

		public MavenTestingException(Throwable cause) {
			super(cause);
		}
	}

	private static class MavenTestingSecurityManager extends SecurityManager {
		private String testName;
		private List<String> allowedFileReadPrefixes;
		private String[] allowedFileWritePrefixes;
		private String tmpDir;
		private boolean allowBadWrites = true;
		private boolean allowBadReads = true;
		private List<String> violations = new ArrayList<String>();
		private char illegalSeparator = 0;

		public MavenTestingSecurityManager() {
			if (SystemUtils.IS_OS_UNIX) {
				illegalSeparator = '\\';
			}
			tmpDir = SystemUtils.getJavaIoTmpDir().getAbsolutePath();
		}
		
		public boolean hasViolations() {
			return violations.size() > 0;
		}
		
		public List<String> getViolations() {
			return violations;
		}

		@Override
		public void checkExit(int status) {
			super.checkExit(status);
			throw new MavenTestingException("Do not call System.exit()!");
		}

		private void checkFileSeparator(String file) {
			if (illegalSeparator != 0) {
				int idx = file.indexOf(illegalSeparator);
				if (idx >= 0) {
					MavenTestingException stack = getStackTrace(
							"%s - Forbidden use of illegal file"
							+ " separator found in character %d of \"%s\"",
							testName, idx, file);
					
					LOG.log(Level.WARNING, "MavenTestingRules Violation", stack);
					violations.add("ILLEGAL_SEP: " + file);
				}
			}
		}

		@Override
		public void checkPermission(Permission perm) {
			// Allow everything
		}

		@Override
		public void checkPermission(Permission perm, Object context) {
			// Allow everything
		}

		@Override
		public void checkRead(String file) {
			checkRead(file, null);
		}
		
		private boolean isTempDir(String filename) {
			if (filename.startsWith(tmpDir)) {
				return true; // allowed to always read from java.io.tmpdir
			}
			

			if(SystemUtils.IS_OS_MAC_OSX) {
				if(filename.startsWith("/private" + tmpDir)) {
					return true; // private temp used in OSX
				}
			}
			
			return false;
		}

		@Override
		public void checkRead(String file, Object context) {
			checkFileSeparator(file);
			
			if (".".equals(file) || "./".equals(file)) {
				// Special case
				return; // allowed
			}
			
			/* Checking for web access.
			 * Not using the full "://" syntax as some legitimate
			 * systems use a single slash ":/" during their execution.  
			 */
			String filelower = file.toLowerCase();
			if (filelower.startsWith("http:/") || 
			    filelower.startsWith("https:/")) {
				return; // allowed to always read from web url.
			}

			String filename = file;
			
			if(file.startsWith("file:")) {
				try {
					filename = new File(new URI(file)).getAbsolutePath();
				} catch (URISyntaxException e) {
					LOG.log(Level.WARNING, "MavenTestingRules Error: Invalid URI: " + file);
					return;
				}
			}
			
			if (isTempDir(filename)) {
				return; // always allowed to read from java.io.tmpdir
			}
			
			for (String prefix : allowedFileReadPrefixes) {
				if (filename.startsWith(prefix)) {
					return; // allowable prefix
				}
			}

			MavenTestingException stack = null;
			
			if (context != null) {
				stack = getStackTrace(
						"%s - Forbidden to read from \"%s\" (context:%s)"
								+ " (Outside of allowed filesystem testing paths)",
						testName, file, context);
			} else {
				stack = getStackTrace("%s - Forbidden to read from \"%s\""
						+ " (Outside of allowed filesystem testing paths)",
						testName, file);
			}
			
			if(stackContainsObject(stack, "com.sun.tools.javac.Main") ||
			   stackContainsObject(stack, "java.lang.ClassLoader")) {
				// Allowed
				return;
			}
			
			violations.add("FORBIDDEN_READ: " + file);
			
			if(allowBadReads) {
				LOG.log(Level.WARNING, "MavenTestingRules Violation", stack);
				return;
			}
			
			LOG.log(Level.SEVERE, "MavenTestingRules Violation", stack);
			throw new SecurityException(stack.getMessage());
		}

		@Override
		public void checkWrite(String file) {
			checkFileSeparator(file);
			
			String filename = file;
			
			if(file.startsWith("file:")) {
				try {
					filename = new File(new URI(file)).getAbsolutePath();
				} catch (URISyntaxException e) {
					LOG.log(Level.WARNING, "MavenTestingRules Error: Invalid URI: " + file);
					return;
				}
			}

			if (isTempDir(filename)) {
				return; // always allowed to read from java.io.tmpdir
			}

			for (String prefix : allowedFileWritePrefixes) {
				if (filename.startsWith(prefix)) {
					return; // allowable prefix
				}
			}

			MavenTestingException stack = getStackTrace(
					"%s - Forbidden to write to \"%s\""
					+ " (Outside of allowed filesystem testing paths)",
					testName, file);

			violations.add("FORBIDDEN_WRITE: " + file);

			if(allowBadWrites) {
				LOG.log(Level.WARNING, "MavenTestingRules Violation", stack);
				return;
			}
			
			LOG.log(Level.SEVERE, "MavenTestingRules Violation", stack);
			throw new SecurityException(stack.getMessage());
		}

		private boolean stackContainsObject(Throwable t, String clazzname) {
			for (StackTraceElement elem : t.getStackTrace()) {
				if (elem.getClassName().equals(clazzname)) {
					return true;
				}
			}
			return false;
		}
		
		private MavenTestingException getStackTrace(String format,
				Object ... args) {
			try {
				throw new MavenTestingException(String.format(format, args));
			} catch (MavenTestingException e) {
				return e;
			}
		}

		public void setAllowedFileReadPrefixes(List<String> prefixes) {
			this.allowedFileReadPrefixes = prefixes;
		}

		public void setAllowedFileWritePrefixes(
				String[] allowedFileWritePrefixes) {
			this.allowedFileWritePrefixes = allowedFileWritePrefixes;
		}
		
		public void setAllowBadWrites(boolean allowBadWrites) {
			this.allowBadWrites = allowBadWrites;
		}
		
		public void setAllowBadReads(boolean allowBadReads) {
			this.allowBadReads = allowBadReads;
		}

		public void setTestName(Object target, FrameworkMethod method) {
			testName = String.format("%s#%s", target.getClass().getName(),
					method.getName());
		}
	}

	private static final Logger LOG = Logger
			.getLogger("test.maven.testing.rules");
	private List<String> fileReadPrefixes;
	private String[] fileWritePrefixes;
	private boolean strictReadPaths = false;
	private boolean strictWritePaths = false;
	private boolean failOnViolation = false;

	public MavenTestingRules() {
		/* The only places allowed to read from on the filesystem */
		fileReadPrefixes = new ArrayList<String>();

		// Full path to ${project.basedir}/target
		addReadPrefix(MavenTestingUtils.getBasedir());

		addReadPrefix("target" + File.separator);
		addReadPrefix("." + File.separator + "target" + File.separator);
		
		// default for maven files (settings / repository)
		File m2Home = new File(SystemUtils.getUserHome(), ".m2");
		addReadPrefix(m2Home);
		
		// Test for hudson specifics
		String workspace = System.getenv("WORKSPACE");
		if(StringUtils.isNotBlank(workspace)) {
			File workspaceDir = new File(workspace);
			File hudsonRepo = new File(workspaceDir, ".repository");
			addReadPrefix(hudsonRepo);
		}

		// Add java home
		File javaHome = new File(System.getProperty("java.home"));
		if(javaHome.getName().equalsIgnoreCase("jre")) {
			// Running with JRE (use parent?)
			addReadPrefix(javaHome.getParentFile());
		} else {
			addReadPrefix(javaHome);
		}
		
		String javaHome2 = System.getenv("JAVA_HOME");
		if(StringUtils.isNotBlank(javaHome2)) {
			addReadPrefix(new File(javaHome2));
		}
		
		// Add other java class / lib / ext paths
		addPathReadEntries(System.getProperty("java.class.path"));
		addPathReadEntries(System.getProperty("java.endorsed.dirs"));
		addPathReadEntries(System.getProperty("java.ext.dirs"));
		addPathReadEntries(System.getProperty("java.library.path"));
		addPathReadEntries(System.getProperty("sun.boot.library.path"));
		addPathReadEntries(System.getProperty("sun.boot.class.path"));
		addPathReadEntries(System.getProperty("surefire.real.class.path"));
		addPathReadEntries(System.getProperty("surefire.test.class.path"));
		
		if(SystemUtils.IS_OS_MAC_OSX) {
			// Add hardcoded libexec dir used internally by OSX JVM.
			addReadPrefix("/usr/libexec/");
		}
		
		if(SystemUtils.IS_OS_UNIX) {
			// Add dev folders
			addReadPrefix("/dev/"); // random seed devices
			addReadPrefix("/etc/"); // global java settings
		}
		
		/* The only places allowed to write to on the filesystem. */
		fileWritePrefixes = new String[3];
		// Full path to ${project.basedir}/target
		fileWritePrefixes[0] = MavenTestingUtils.getTargetDir()
				.getAbsolutePath();
		// Relative references to target directory
		fileWritePrefixes[1] = "target" + File.separator;
		fileWritePrefixes[2] = "." + File.separator + "target" + File.separator;
	}
	
	private final void addReadPrefix(File path) {
		if(!path.exists()) {
			addReadPrefix(path.getAbsolutePath());
			return;
		}
		
		if(!path.isDirectory()) {
			addReadPrefix(path.getParentFile());
			return;
		}
		
		addReadPrefix(path.getAbsolutePath());
	}
	
	private final void addReadPrefix(String prefix) {
		if(StringUtils.isBlank(prefix)) {
			return;
		}
		
		if(fileReadPrefixes.contains(prefix)) {
			return;
		}
		
		for(String path: fileReadPrefixes) {
			if(prefix.startsWith(path)) {
				// Found shorter.
				return;
			}
		}
		
		fileReadPrefixes.add(prefix);
	}
	
	public void dumpReadPrefixes() {
		for(String prefix: fileReadPrefixes) {
			System.out.printf("READ: %s%n", prefix);
		}
	}

	private void addPathReadEntries(String syspath) {
		if(StringUtils.isBlank(syspath)) {
			return;
		}
		
		StringTokenizer tok = new StringTokenizer(syspath, File.pathSeparator);
		while(tok.hasMoreTokens()) {
			String name = tok.nextToken();
			addReadPrefix(new File(FilenameUtils.normalize(name)));
		}
	}

	/**
	 * @deprecated enabled by default
	 */
	@Deprecated
	public void allowTempDirectory() {
		fileReadPrefixes.add(SystemUtils.getJavaIoTmpDir().getAbsolutePath());
	}
	
	/**
	 * @deprecated use {@link #setStrictWritePaths(boolean)} instead.
	 */
	@Deprecated
	public void allowBadWrites(boolean allow) {
		setStrictWritePaths(!allow);
	}
	
	/**
	 * @deprecated use {@link #setStrictReadPaths(boolean)} instead.
	 */
	@Deprecated
	public void allowBadReads(boolean allow) {
		setStrictReadPaths(!allow);
	}

	@Override
	public Statement apply(final Statement statement,
			final FrameworkMethod method, final Object target) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				SecurityManager originalSecMgr = System.getSecurityManager();
				MavenTestingSecurityManager mgr = new MavenTestingSecurityManager();
				mgr.setAllowedFileReadPrefixes(fileReadPrefixes);
				mgr.setAllowedFileWritePrefixes(fileWritePrefixes);
				mgr.setAllowBadWrites(!strictReadPaths);
				mgr.setAllowBadReads(!strictWritePaths);
				try {
					mgr.setTestName(target, method);
					System.setSecurityManager(mgr);
					// Execute the test
					statement.evaluate();
					if(failOnViolation && mgr.hasViolations()) {
						StringBuilder v = new StringBuilder();
						List<String> violations = mgr.getViolations();
						v.append(Integer.toString(violations.size()));
						v.append(" violations(s) in MavenTestingRules.");
						v.append(SystemUtils.LINE_SEPARATOR);
						v.append("Test: ").append(target.getClass().getName());
						v.append(".").append(method.getName()).append("();");
						v.append(" /* see console for details and stack trace for each violation */");
						for(String viol: violations) {
							v.append(SystemUtils.LINE_SEPARATOR);
							v.append("  ").append(viol);
						}
						Assert.fail(v.toString());
					}
				} finally {
					System.setSecurityManager(originalSecMgr);
				}
			}
		};
	}
	
	public void setStrictReadPaths(boolean b) {
		this.strictReadPaths = b;
	}

	public void setStrictWritePaths(boolean b) {
		this.strictWritePaths = b;
	}

	public void setFailOnViolation(boolean b) {
		this.failOnViolation = b;
	}
}
