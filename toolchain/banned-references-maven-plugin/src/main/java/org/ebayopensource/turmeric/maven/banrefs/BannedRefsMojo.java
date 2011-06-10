/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.maven.banrefs;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.model.Build;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.ebayopensource.turmeric.maven.banrefs.console.Console;
import org.ebayopensource.turmeric.maven.banrefs.console.MojoConsole;
import org.ebayopensource.turmeric.maven.banrefs.filters.DirectoryFilter;
import org.ebayopensource.turmeric.maven.banrefs.filters.TextFileFilter;
import org.ebayopensource.turmeric.maven.banrefs.reports.CheckstyleXmlReport;
import org.ebayopensource.turmeric.maven.banrefs.reports.ConsoleReport;
import org.ebayopensource.turmeric.maven.banrefs.reports.XmlReport;

/**
 * Checks for banned references within a project source, resources, and even directory and filenames.
 * 
 * @goal check
 * @phase validate
 * @requiresProject true
 */
public class BannedRefsMojo extends AbstractMojo {
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
     * @parameter expression="${bannedrefs.output.file}"
     *            default-value="${project.build.directory}/bannedrefs-report.xml"
     * @required
     */
    protected File outputFile;

    /**
     * The report output format.
     * <p>
     * Available Formats: "xml", "checkstyleXml", or "console"
     * 
     * @parameter expression="${bannedrefs.report.format}" default-value="xml"
     * @required
     */
    private String reportFormat = "xml";
    
    /**
     * The banned references.
     * 
     * @parameter expression="${bannedrefs.includes}"
     * @required
     */
    private String includes[];
    
    /**
     * The banned references exclusions.
     * <p>
     * Those references, where matched in <code>includes</code>, are actually valid due to context.
     * 
     * @parameter expression="${bannedrefs.excludes}"
     * @optional
     */
    private String excludes[];

    private Console console;
    private Report report;
    private BannedRefChecker checker;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        this.console = new MojoConsole(getLog());
        initReport();
        checker = new BannedRefChecker(includes, excludes);

        try {
            Build build = project.getBuild();
            if (build != null) {
                /* Main Source Directory */
                processDir(build.getSourceDirectory());
                /* Main Resources */
                processResourceDirs(build.getResources());

                /* Test Source Directory */
                processDir(build.getTestSourceDirectory());
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

    private void processRoot(File root) {
        checker.check(report, new File(root, "pom.xml"));

        for (File file : root.listFiles(TextFileFilter.INSTANCE)) {
            checker.check(report, file);
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
                recursiveCheck(resourceDir);
            }
        }
    }

    private void processDir(String dirName) throws MojoExecutionException {
        File dir = new File(dirName);
        if (!dir.exists()) {
            return;
        }
        recursiveCheck(dir);
    }

    private void recursiveCheck(File dir) {
        File files[] = dir.listFiles(TextFileFilter.INSTANCE);
        for (File file : files) {
            getLog().debug("[" + checker.getClass().getSimpleName() + "] " + file.getAbsolutePath());
            checker.check(report, file);
        }

        for (File subdir : dir.listFiles(DirectoryFilter.INSTANCE)) {
            recursiveCheck(subdir);
        }
    }

    private void initReport() throws MojoExecutionException {
        reportFormat = System.getProperty("bannedrefs.report.format", reportFormat);
        
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
}
