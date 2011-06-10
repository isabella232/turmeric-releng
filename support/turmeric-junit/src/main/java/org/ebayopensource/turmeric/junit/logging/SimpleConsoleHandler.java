package org.ebayopensource.turmeric.junit.logging;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.ebayopensource.turmeric.junit.utils.MavenTestingUtils;

public class SimpleConsoleHandler extends Handler {
	public static class Init {
		public Init() {
			URL url = null;
			
			// Try file first.
			if(MavenTestingUtils.hasTestResourcesDir()) {
				File confFile = new File(MavenTestingUtils.getTestResourcesDir(), "test-logging.properties");
				if(confFile.exists()) {
					try {
						url = confFile.toURI().toURL();
					} catch (MalformedURLException e) {
						e.printStackTrace(System.err);
					}
				}
			}
			
			// Try resource next.
			if (url == null) {
				url = this.getClass().getResource("test-logging.properties");
			}
			
			if (url == null) {
				// Use defaults.
				configureDefaults();
				return;
			} 
			
			// Configurable Setup
			InputStream in = null;
			try {
				in = url.openStream();
				LogManager.getLogManager().readConfiguration(in);
			} catch (IOException e) {
				e.printStackTrace(System.err);
				configureDefaults();
			} finally {
				IOUtils.closeQuietly(in);
			}
		}

		public void configureDefaults() {
			// Default Setup
			LogManager.getLogManager().reset();
			Logger root = Logger.getLogger("");

			Handler handlers[] = root.getHandlers();
			for (Handler handler : handlers) {
				System.out.println("Removing existing logging handler: " + handler);
				root.removeHandler(handler);
			}

			SimpleConsoleHandler logger = new SimpleConsoleHandler();
			System.out.println("Adding our logging handler: " + logger);
			root.addHandler(logger);
		}
	}

	public static void init() {
		System.setProperty("java.util.logging.config.class",
				SimpleConsoleHandler.Init.class.getName());
	}

	public static Handler createWithLevel(Level level) {
		SimpleConsoleHandler handler = new SimpleConsoleHandler();
		handler.setLevel(level);
		return handler;
	}

	@Override
	public void close() throws SecurityException {
		/* nothing to do here */
	}

	@Override
	public void flush() {
		/* nothing to do here */
	}

	@Override
	public void publish(LogRecord record) {
		StringBuilder buf = new StringBuilder();
		buf.append("[").append(record.getLevel().getName());
		buf.append("] ").append(record.getLoggerName());
		buf.append(" (").append(record.getSourceMethodName());
		buf.append("): ").append(record.getMessage());

		System.out.println(buf.toString());
		if (record.getThrown() != null) {
			record.getThrown().printStackTrace(System.out);
		}
	}

}
