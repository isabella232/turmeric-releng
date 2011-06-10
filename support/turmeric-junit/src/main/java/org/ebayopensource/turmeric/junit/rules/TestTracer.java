package org.ebayopensource.turmeric.junit.rules;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class TestTracer implements MethodRule {
	public static final String LOG_NAME = "test.tracer";
	private static final Logger LOG = Logger.getLogger(LOG_NAME);
	private boolean trackResourceUsage = false;
	private boolean failOnMultiResourceEntry = false;
	
	public boolean isTrackResourceUsage() {
		return trackResourceUsage;
	}

	public TestTracer setTrackResourceUsage(boolean trackResourceUsage) {
		this.trackResourceUsage = trackResourceUsage;
		return this;
	}

	public boolean isFailOnMultiResourceEntry() {
		return failOnMultiResourceEntry;
	}

	public TestTracer setFailOnMultiResourceEntry(boolean fail) {
		this.failOnMultiResourceEntry = fail;
		return this;
	}

	@Override
	public Statement apply(final Statement statement, final FrameworkMethod method,
			final Object target) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				String className = target.getClass().getName();
				String methodName = method.getName();
				LOG.info(String.format("### %s / %s ::: Starting", className,
						methodName));

				try {
					if(trackResourceUsage) {
						evaluateWithTracking(statement, className, methodName);
					} else {
						evaluateWithoutTracking(statement, className, methodName);
					}
				} finally {
					LOG.info(String.format("### %s / %s ::: Completed", className,
							methodName));
				}
			}

			protected void evaluateWithTracking(final Statement statement,
					String className, String methodName) throws AssertionError,
					Throwable {
				ClassLoader original = Thread.currentThread().getContextClassLoader();
				ConfigTrackingClassloader ctcl = new ConfigTrackingClassloader(original);
				try {
					Thread.currentThread().setContextClassLoader(ctcl);
					statement.evaluate();
				} catch(AssertionError e) {
					// No point dump stack trace on AssertionFailedError
					LOG.log(Level.WARNING, String.format(
							"### %s / %s ::: AssertionError%n%s - %s", className,
							methodName, e.getClass().getName(), e.getMessage()));
					throw e;
				} catch(Throwable t) {
					LOG.log(Level.WARNING, String.format(
							"### %s / %s ::: Tossed Exception", className,
							methodName), t);
					throw t;
				} finally {
					Thread.currentThread().setContextClassLoader(original);
					dumpResourceDetails(ctcl.getMetaResources());
				}
			}
			
			protected void evaluateWithoutTracking(final Statement statement,
					String className, String methodName) throws AssertionError,
					Throwable {
				try {
					statement.evaluate();
				} catch(AssertionError e) {
					// No point dump stack trace on AssertionFailedError
					LOG.log(Level.WARNING, String.format(
							"### %s / %s ::: AssertionError%n%s - %s", className,
							methodName, e.getClass().getName(), e.getMessage()));
					throw e;
				} catch(Throwable t) {
					LOG.log(Level.WARNING, String.format(
							"### %s / %s ::: Tossed Exception", className,
							methodName), t);
					throw t;
				}
			}
		};
	}
	
	protected void dumpResourceDetails(Set<String> metaResources) {
		StringBuilder multi = new StringBuilder();
		ClassLoader cl = this.getClass().getClassLoader();
		StringBuilder rez = new StringBuilder();
		rez.append("Tracked requests for the following resources:");
		List<URL> urls;
		boolean foundMulti;
		for (String name : metaResources) {
			rez.append("\n   ").append(name);
			foundMulti = false;
			try {
				urls = Collections.list(cl.getResources(name));
				if (urls.size() == 0) {
					rez.append(" (NOT FOUND)");
					continue;
				}
				
				if (urls.size() == 1) {
					rez.append(" (only 1)");
				} else {
					rez.append(" (CONFLICT: ").append(urls.size()).append(" ENTRIES FOUND)");
					foundMulti = true;
					multi.append("\nMultiple Hits For ClassLoader Resource: \"").append(name).append("\"");
				}
				
				int num = 0;
				for (URL url : urls) {
					rez.append(String.format("%n    %2d) %s", ++num, url));
					if (foundMulti) {
						multi.append(String.format("%n%2d) %s", num, url));
					}
				}
			} catch (IOException e) {
				rez.append("\n     !! ").append(e.getClass().getName());
				rez.append(": ").append(e.getMessage());
			}
		}
		LOG.info(rez.toString());
		if (failOnMultiResourceEntry && (multi.length() > 0)) {
			Assert.fail(multi.toString());
		}
	}

	class ConfigTrackingClassloader
	extends URLClassLoader
	{
		private Set<String> metaResources = new TreeSet<String>();
		
		public ConfigTrackingClassloader(ClassLoader parent) {
			super(new URL[0], parent);
		}
		
		public Set<String> getMetaResources() {
			return metaResources;
		}
		
		@Override
		public URL getResource(String name) {
			URL url = super.getResource(name);
			
			if (name.startsWith("META-INF/")) {
				metaResources.add(name);
			}

			return url;
		}
		
		@Override
		public Enumeration<URL> getResources(String name) throws IOException {
			if(name.startsWith("META-INF/")) {
				metaResources.add(name);
			}
			return super.getResources(name);
		}
	}
}
