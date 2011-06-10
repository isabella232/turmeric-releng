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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.interpolation.EnvarBasedValueSource;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.Interpolator;
import org.codehaus.plexus.interpolation.PrefixedObjectValueSource;
import org.codehaus.plexus.interpolation.PropertiesBasedValueSource;
import org.codehaus.plexus.interpolation.RegexBasedInterpolator;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;
import org.ebayopensource.turmeric.plugins.catalog.CatalogConfiguration;

/**
 * Goal which processes the ServiceCodeGen.xsd and generates various types of xml, and java code.
 * 
 * @goal xjc-episode
 * @phase process-sources
 * @requiresDependencyResolution compile
 */
public class XjcEpisodeMojo extends AbstractMojo {
	/**
	 * The default maven project object
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * Location of generated java code
	 * 
	 * @parameter expression="${jaxb.episode.output.directory}" default-value=
	 *            "${project.build.directory}/generated-sources/jaxb-episode"
	 * @required
	 */
	private File sourcesOutputDirectory = new File("${project.build.directory}/generated-sources/jaxb-episode");

	/**
	 * Location of generated resources
	 * 
	 * @parameter expression="${jaxb.episode.resources.output.directory}" default-value=
	 *            "${project.build.directory}/generated-resources/jaxb-episode"
	 * @required
	 */
	private File resourcesOutputDirectory = new File("${project.build.directory}/generated-resources/jaxb-episode");

	/**
	 * Relative path to the generated episode file.
	 * 
	 * @parameter expression="${jaxb.episode.episode.name}" default-value="META-INF/SOAClient.episode"
	 * @required
	 */
	private String episodePath;

	/**
	 * Schema file to generate SOA episode from.
	 * 
	 * @parameter expression="${schemaEpisode}"
	 * @required
	 */
	private File xsdEpisodeFile;
	
	/**
	 * Catalog to help find content such as <code>&lt;xs:import&gt;</code> references
	 * to schemas found in the project dependencies instead.
	 * 
	 * @parameter
	 * @optional
	 */
	private CatalogConfiguration catalog;

	/**
	 * List of packages to exclude (delete) from the generated sources
	 * 
	 * @parameter expression="${excludedPackages}"
	 * @optional
	 */
	private String excludedPackages[];
	
	/**
	 * Verbose XJC Output
	 * 
	 * @parameter expression="${verbose}" default-value="false"
	 * @optional
	 */
	private boolean verbose = false;

	/**
	 * Schemas to generate sources from.
	 * 
	 * @parameter expression="${service.codegen.schemas}"
	 * @required
	 */
	private File schemas[];

	/**
	 * Timestamp file used for tracking last generation and preventing
	 * a loop of generation seen in m2eclipse.
	 * 
	 * @parameter expression="${xjc.timestamp.file}" 
	 * 		default-value="${project.build.directory}/jaxb-episode-gen-timestamp"
	 * @required
	 */
	private File xjcTimestampFile = new File("${project.build.directory}/jaxb-episode-gen-timestamp");

    private File catalogFile;
    
    public MavenProject getProject() {
        return project;
    }

	private void ensureDirectoryExists(String id, File dir)
			throws MojoExecutionException {
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				throw new MojoExecutionException("Unable to create " + id
						+ ": " + dir);
			}
		}
	}

	public void execute() throws MojoExecutionException {
		sourcesOutputDirectory = expandFile("sourcesOutputDirectory", sourcesOutputDirectory);
		resourcesOutputDirectory = expandFile("resourcesOutputDirectory", resourcesOutputDirectory);
		
		ensureDirectoryExists("sourcesOutputDirectory", sourcesOutputDirectory);
		ensureDirectoryExists("resourcesOutputDirectory", resourcesOutputDirectory);

		// When Unit testing the plugin, the project object is unset
		if (project != null) {
			// Attach the generated source directory to the maven project
			project.addCompileSourceRoot(sourcesOutputDirectory.getAbsolutePath());

			// Attach the generated resources directory to the maven project
			Resource resource = new Resource();
			resource.setDirectory(resourcesOutputDirectory.getAbsolutePath());
			resource.addInclude("**/" + episodePath);
			project.addResource(resource);
		}
		
		getLog().debug("Catalog = " + catalog);
		if(catalog != null) {
		    File targetDir = new File(project.getBuild().getDirectory());
		    ensureDirectoryExists("target", targetDir);
		    catalogFile = new File(targetDir, "jaxb-episode-catalog.xml");
		    FileWriter writer = null;
		    PrintWriter out = null;
		    try {
		        writer = new FileWriter(catalogFile);
		        out = new PrintWriter(writer);
		        catalog.writeXml(out);
		        out.flush();
		    } catch(IOException e) {
                throw new MojoExecutionException("Unable to write temporary catalog file: " + catalogFile, e);
		    } finally {
		        IOUtil.close(out);
		        IOUtil.close(writer);
		    }
		}
		
		// Expand any paths
		episodePath = expandParameter("episodePath", episodePath);
		xjcTimestampFile = expandFile("xjcTimestampFile", xjcTimestampFile);
		xsdEpisodeFile = expandFile("xsdEpisodeFile", xsdEpisodeFile);
		if(schemas != null) {
			for (int i = 0; i < schemas.length; i++) {
				schemas[i] = expandFile("schemas[" + i + "]", schemas[i]);
			}
		}

		// Test to see if a rebuild is even required.
		if (needsGeneration() == false) {
			getLog().info("Skipping xjc-episode, No generation needed");
			return;
		}

		// Perform generation
		long now = System.currentTimeMillis();
		try {
			File episodeFile = new File(resourcesOutputDirectory, episodePath);
			ensureDirectoryExists("Episode Home Dir",
					episodeFile.getParentFile());

			XjcEpisode gen = new XjcEpisode();
			gen.setVerbose(verbose);
			gen.setLog(getLog());
			gen.setCatalogFile(catalogFile);
			gen.setXsdEpisodeFile(xsdEpisodeFile);
			gen.setSourceOutputDir(sourcesOutputDirectory);
			gen.setResourceOutputDir(resourcesOutputDirectory);
			gen.setEpisodeFile(episodeFile);

			gen.readXSD();
			gen.xjcCreateEpisode();
			gen.filterEpisode();
			gen.generateEpisodeSourceFiles();
			if (excludedPackages != null) {
				File packageDir;
				for (String excludedPackage : excludedPackages) {
					packageDir = new File(sourcesOutputDirectory,
							excludedPackage.replace('.', File.separatorChar));
					if (packageDir.exists()) {
						getLog().info(
								"Removing exclude Source Package: "
										+ excludedPackage);
						FileUtils.deleteDirectory(packageDir);
					}
				}
			}
			for (File xsd : schemas) {
				gen.generateSourceFiles(xsd);
			}
			gen.deleteObjectFactory();
			writeLastGenTimestamp(now);
		} catch (Exception e) {
			throw new MojoExecutionException("Unable to generate code", e);
		}
	}
	
	private void writeLastGenTimestamp(long timestamp)
			throws MojoExecutionException {
		try {
			FileUtils.fileWrite(xjcTimestampFile.getAbsolutePath(), "UTF-8",
					String.valueOf(timestamp));
		} catch (IOException e) {
			throw new MojoExecutionException(
					"Unable to write timestamp generation tracking file: "
							+ xjcTimestampFile, e);
		}
	}

	/**
	 * Check the various files and timestamps to determine if a generation
	 * is needed. 
	 * @return true if generation is needed
	 */
	private boolean needsGeneration() {
		if(xjcTimestampFile == null) {
			File targetDir = new File(project.getBuild().getDirectory());
			xjcTimestampFile = new File(targetDir, "jaxb-episode-gen-timestamp");
		}
		
		if(!xjcTimestampFile.exists()) {
			getLog().debug("Generation Needed: Timestamp file not present: " + xjcTimestampFile);
			return true;
		}
		
		// Load Timestamp
		long lastGenTimestamp = 0;
		try {
			String rawTimestamp = FileUtils.fileRead(xjcTimestampFile);
			if (StringUtils.isBlank(rawTimestamp)) {
				getLog().debug(
						"Generation Needed: last timestamp is blank: "
								+ xjcTimestampFile);
				return true;
			}
			lastGenTimestamp = Long.parseLong(rawTimestamp.trim());
		} catch (IOException e) {
			getLog().debug(
					"Generation Needed: Unable to read last timestamp: "
							+ xjcTimestampFile, e);
			return true;
		}
		
		// Test the various xsd files for changes.
		if (modifiedSince(lastGenTimestamp, xsdEpisodeFile)) {
			getLog().debug(
					"Generation Needed: File modified since last generation: "
							+ xsdEpisodeFile);
		}
		for (File xsd : schemas) {
			if (modifiedSince(lastGenTimestamp, xsd)) {
				getLog().debug(
						"Generation Needed: File modified since last generation: "
								+ xsd);
			}
		}

		// No need to generate
		getLog().debug("No need to generate this time, nothing modified");
		return false;
	}
	
	private boolean modifiedSince(long lastGenTimestamp, File file) {
		return file.lastModified() > lastGenTimestamp;
	}

	/**
	 * Convenience method for using {@link #expandParameter(String)} with
	 * File objects.
	 * 
	 * @param rawfile the raw file object
	 * @return null if rawfile is null, or the expanded File object.
	 * @throws MojoExecutionException
	 */
	private File expandFile(String name, File rawfile) throws MojoExecutionException {
		if (rawfile == null) {
			return null;
		}
		String rawpath = rawfile.getPath();
		return new File(expandParameter(name, rawpath));
	}
	
	/**
	 * Take a raw parameter value, and expand any found properties within it.
	 * 
	 * @param parameter
	 * @return the expanded parameter
	 * @throws MojoExecutionException
	 *             if unable to interpolate
	 */
	private String expandParameter(String name, String rawparameter)
			throws MojoExecutionException {
		if (StringUtils.isBlank(rawparameter)) {
			return rawparameter;
		}

		try {
			Interpolator interpolator = new RegexBasedInterpolator();
			interpolator.addValueSource(new PrefixedObjectValueSource("project",
					project));
			interpolator.addValueSource(new PrefixedObjectValueSource("mojo",
					this));
			interpolator.addValueSource(new PropertiesBasedValueSource(System
					.getProperties()));
			interpolator.addValueSource(new EnvarBasedValueSource());

			String result = interpolator.interpolate(rawparameter);
			if(getLog().isDebugEnabled()) {
				StringBuilder msg = new StringBuilder();
				msg.append("Expand Parameter: ").append(name);
				msg.append("\n  Raw     : ").append(rawparameter);
				msg.append("\n  Expanded: ").append(result);
				getLog().debug(msg.toString());
			}
			return result;
		} catch (IOException e) {
			throw new MojoExecutionException(
					"Unable to use Environment for Parameter Interpolation", e);
		} catch (InterpolationException e) {
			throw new MojoExecutionException("Unable to use interpolatate: "
					+ rawparameter, e);
		}
	}

	public File getXsdEpisodeFile() {
		return xsdEpisodeFile;
	}
}
