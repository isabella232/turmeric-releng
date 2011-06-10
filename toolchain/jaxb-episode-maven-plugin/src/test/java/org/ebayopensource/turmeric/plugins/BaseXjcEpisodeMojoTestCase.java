/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.plugins;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.util.FileUtils;
import org.ebayopensource.turmeric.plugins.stubs.DebugEnabledLog;
import org.ebayopensource.turmeric.plugins.stubs.ProjectClassLoader;
import org.ebayopensource.turmeric.plugins.stubs.TestProjectStub;

public abstract class BaseXjcEpisodeMojoTestCase
		extends AbstractMojoTestCase {
	private File projectBaseDir;
	private boolean mojoLoggingDebug = false;

	public abstract String getTestMojoDirName();

	protected void setUp() throws Exception {
		super.setUp();

		projectBaseDir = new File(getBasedir(), "target/tests/"
				+ getTestMojoDirName());
		FileUtils.deleteDirectory(projectBaseDir);

		// copy src/test/resources/${dirname} into target dir for working with it in a test case
		// Don't want to mess up the source tree with accidents and bugs.
		File srcProjectDir = new File(getBasedir(), "src/test/resources/"
				+ getTestMojoDirName());
		FileUtils.copyDirectoryStructure(srcProjectDir, projectBaseDir);
	}
	
	protected void setMojoLoggingDebug(boolean enabled) {
		mojoLoggingDebug = enabled;
	}

	protected void setPlexusLoggingLevel(int threshold) {
		try {
			LoggerManager loggerManager = (LoggerManager) lookup(LoggerManager.ROLE);
			loggerManager.setThreshold(threshold);
		} catch (Exception e) {
			// Not a fatal error
			e.printStackTrace(System.err);
		}
	}

	/**
	 * Create and configure a Mojo from a pom in the src/test/resources tree.
	 * 
	 * @return a Mojo
	 * @exception Exception
	 *                if an error occurs
	 */
	protected XjcEpisodeMojo createMojo() throws Exception {
		File pom = new File(projectBaseDir, "plugin-config.xml");
		XjcEpisodeMojo mojo = (XjcEpisodeMojo) lookupMojo("xjc-episode", pom);
		assertNotNull("Mojo should not be null", mojo);
		
		TestProjectStub stub = new TestProjectStub(getTestMojoDirName());
		setVariableValueToObject(mojo, "project", stub);
		
		if(mojoLoggingDebug) {
			setVariableValueToObject(mojo, "log", new DebugEnabledLog());
		}
		return mojo;
	}
	
	/**
	 * Execute the mojo.
	 * 
	 * @param mojo
	 * @throws Exception
	 */
	protected void executeMojo(XjcEpisodeMojo mojo) throws Exception {
		try {
	        ClassLoader original = Thread.currentThread().getContextClassLoader();
	        try {
	            ProjectClassLoader cl = ProjectClassLoader.create(mojo, super.getClassLoader());
	            Thread.currentThread().setContextClassLoader(cl);
	            mojo.execute();
	        } finally {
	            Thread.currentThread().setContextClassLoader(original);
	        }
		} catch (Exception e) {
			e.printStackTrace(System.err);
			throw e;
		}
	}
}
