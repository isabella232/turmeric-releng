/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.maven.license;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.model.Build;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.ebayopensource.turmeric.maven.license.checks.JavaHeaderCheck;
import org.ebayopensource.turmeric.maven.license.checks.PropertiesHeaderCheck;
import org.ebayopensource.turmeric.maven.license.checks.XmlHeaderCheck;
import org.ebayopensource.turmeric.maven.license.filters.DirectoryFilter;
import org.ebayopensource.turmeric.maven.license.filters.FileExtensionFilter;
import org.ebayopensource.turmeric.maven.license.filters.PropertiesFileFilter;
import org.ebayopensource.turmeric.maven.license.reports.CheckstyleXmlReport;
import org.ebayopensource.turmeric.maven.license.reports.ConsoleReport;
import org.ebayopensource.turmeric.maven.license.reports.XmlReport;
import org.ebayopensource.turmeric.maven.license.util.Console;
import org.ebayopensource.turmeric.maven.license.util.MojoConsole;

/**
 * Perform a Licensing Check on the project.
 * 
 * @goal check
 * @phase validate
 * @requiresProject true
 */
public class LicenseCheckMojo extends AbstractMojo {
    private static final String XML_EXTENSIONS[] = { ".xml", ".wsdl", ".xsd", ".episode" };;
    private static final String PROP_EXTENSIONS[] = { ".properties" };
    private static final String JAVA_EXTENSIONS[] = { ".java" };

    /**
     * The default maven project object
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * Location of generated java code.
     * 
     * @parameter expression="${licensecheck.output.directory}" default-value="${project.build.directory}"
     * @required
     */
    protected File outputDirectory;

    /**
     * Filename of the xml report.
     * 
     * @parameter expression="${licensecheck.output.filename}" default-value="license-check-report.xml"
     * @required
     */
    protected String outputFilename;

    /**
     * The report output format.
     * <p>
     * Available Formats: "xml", "checkstyleXml", or "console"
     * 
     * @parameter expression="${licensecheck.report.format}" default-value="xml"
     * @required
     */
    private String reportFormat = "xml";

    private Report report;
    private Console console;
    private Checker xmlChecker;
    private Checker propChecker;
    private Checker javaChecker;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        ensureDirectoryExists("Output Directory", outputDirectory);
        console = new MojoConsole(getLog());
        xmlChecker = new XmlHeaderCheck();
        propChecker = new PropertiesHeaderCheck();
        javaChecker = new JavaHeaderCheck();

        initReport();

        try {
            Build build = project.getBuild();
            if (build != null) {
                /* Main Source Directory */
                processSourceDir(build.getSourceDirectory());
                /* Main Resources */
                processResourceDirs(build.getResources());

                /* Test Source Directory */
                processSourceDir(build.getTestSourceDirectory());
                /* Test Resources */
                processResourceDirs(build.getTestResources());

                /* Root Directory */
                processRoot(project.getBasedir());
            }
        }
        finally {
            report.close();
        }
    }

    private void initReport() throws MojoExecutionException {
        reportFormat = System.getProperty("licensecheck.report.format", reportFormat);
        
        if ("xml".equals(reportFormat)) {
            try {
                File reportFile = new File(outputDirectory, outputFilename);
                report = new XmlReport(console, project, reportFile);
                return;
            }
            catch (IOException e) {
                throw new MojoExecutionException("Unable to initialize XML reportFormat", e);
            }
        }

        if ("checkstyleXml".equals(reportFormat)) {
            try {
                File reportFile = new File(outputDirectory, outputFilename);
                report = new CheckstyleXmlReport(console, reportFile);
                return;
            }
            catch (IOException e) {
                throw new MojoExecutionException("Unable to initialize Checkstyle XML reportFormat", e);
            }
        }

        if ("console".equals(reportFormat)) {
            report = new ConsoleReport(project);
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

    private void processRoot(File root) {
        xmlChecker.check(report, new File(root, "pom.xml"));

        if ("jar".equalsIgnoreCase(project.getPackaging())) {
            fileRequired(new File(root, "LICENSE"));
            fileRequired(new File(root, "NOTICE"));
        }

        for (File propFile : root.listFiles(new PropertiesFileFilter())) {
            propChecker.check(report, propFile);
        }
    }

    private void fileRequired(File file) {
        if (!file.exists()) {
            report.violation(file, "missing", 0, "Missing Required File: %s", file);
        }
    }

    @SuppressWarnings("unchecked")
    private void processResourceDirs(List<?> resources) throws MojoExecutionException {
        if (resources == null) {
            return;
        }

        for (Resource resource : (List<Resource>) resources) {
            File resourceDir = new File(resource.getDirectory());
            if (resourceDir.exists()) {
                recursiveCheck(resourceDir, XML_EXTENSIONS, xmlChecker);
                recursiveCheck(resourceDir, PROP_EXTENSIONS, propChecker);
            }
        }
    }

    private void processSourceDir(String sourceDirName) throws MojoExecutionException {
        File sourceDirectory = new File(sourceDirName);
        if (!sourceDirectory.exists()) {
            return;
        }
        recursiveCheck(sourceDirectory, JAVA_EXTENSIONS, javaChecker);
    }

    private void recursiveCheck(File dir, String extensions[], Checker checker) {
        File files[] = dir.listFiles(new FileExtensionFilter(extensions));
        for (File file : files) {
            getLog().debug("[" + checker.getClass().getSimpleName() + "] " + file.getAbsolutePath());
            checker.check(report, file);
        }

        for (File subdir : dir.listFiles(DirectoryFilter.INSTANCE)) {
            recursiveCheck(subdir, extensions, checker);
        }
    }
}
