/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.maven.config;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.model.Build;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.ebayopensource.turmeric.maven.config.console.Console;
import org.ebayopensource.turmeric.maven.config.console.MojoConsole;
import org.ebayopensource.turmeric.maven.config.reports.AbstractReport;
import org.ebayopensource.turmeric.maven.config.reports.CheckstyleXmlReport;
import org.ebayopensource.turmeric.maven.config.reports.ConsoleReport;
import org.ebayopensource.turmeric.maven.config.reports.XmlReport;
import org.ebayopensource.turmeric.runtime.config.validation.RuntimeConfigValidator;

/**
 * Checks for various Turmeric project configuration files and performs some basic validation
 * against them.
 * 
 * @goal validate-config
 * @phase validate
 * @requiresProject true
 */
public class ConfigValidationMojo extends AbstractMojo {
    /**
     * The default maven project object
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * The output file for the report.
     * 
     * @parameter expression="${configvalidate.output.file}"
     *            default-value="${project.build.directory}/config-validation-report.xml"
     * @required
     */
    protected File outputFile;

    /**
     * The report output format.
     * <p>
     * Available Formats: "xml", "checkstyleXml", or "console"
     * 
     * @parameter expression="${configvalidate.report.format}" default-value="xml"
     * @required
     */
    private String reportFormat = "xml";

    private Console console;
    private AbstractReport report;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        this.console = new MojoConsole(getLog());
        initReport();

        try {
            Build build = project.getBuild();
            if (build != null) {
                RuntimeConfigValidator validator = new RuntimeConfigValidator();

                /* Main Source Directory */
                processDir(validator, build.getSourceDirectory());
                /* Main Resources */
                processResourceDirs(validator, build.getResources());

                /* Test Source Directory */
                processDir(validator, build.getTestSourceDirectory());
                /* Test Resources */
                processResourceDirs(validator, build.getTestResources());
            }
        }
        finally {
            report.close();
        }
    }

    @SuppressWarnings("unchecked")
    private void processResourceDirs(RuntimeConfigValidator validator, List<?> resources)
                    throws MojoExecutionException {
        if (resources == null) {
            return;
        }

        for (Resource resource : (List<Resource>) resources) {
            File resourceDir = new File(resource.getDirectory());
            if (resourceDir.exists()) {
                validator.validateAll(resourceDir, report);
            }
        }
    }

    private void processDir(RuntimeConfigValidator validator, String dirName)
                    throws MojoExecutionException {
        File dir = new File(dirName);
        if (!dir.exists()) {
            return;
        }
        validator.validateAll(dir, report);
    }

    private void initReport() throws MojoExecutionException {
        reportFormat = System.getProperty("configvalidate.report.format", reportFormat);

        if ("xml".equals(reportFormat)) {
            try {
                ensureDirectoryExists("Output Directory", outputFile.getParentFile());
                report = new XmlReport(console, project, outputFile);
                return;
            }
            catch (IOException e) {
                throw new MojoExecutionException("Unable to initialize XML reportFormat", e);
            }
        }

        if ("checkstyleXml".equals(reportFormat)) {
            try {
                ensureDirectoryExists("Output Directory", outputFile.getParentFile());
                report = new CheckstyleXmlReport(console, outputFile);
                return;
            }
            catch (IOException e) {
                throw new MojoExecutionException(
                                "Unable to initialize Checkstyle XML reportFormat", e);
            }
        }

        if ("console".equals(reportFormat)) {
            report = new ConsoleReport(console, project);
            return;
        }

        throw new MojoExecutionException("Unknown <reportFormat>" + reportFormat
                        + "</reportFormat> (only accept 'xml', 'checkstyleXml', or 'console')");
    }

    protected final void ensureDirectoryExists(String id, File dir) throws MojoExecutionException {
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new MojoExecutionException("Unable to create " + id + ": " + dir);
            }
        }
    }
}
