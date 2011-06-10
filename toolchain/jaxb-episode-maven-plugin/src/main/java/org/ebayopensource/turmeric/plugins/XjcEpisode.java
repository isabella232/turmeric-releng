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
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.FileUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.tools.xjc.Driver;

/**
 * 
 * suitable for use within a Maven Plugin.
 */
public class XjcEpisode {

    private List<String> m_typesDefined = new ArrayList<String>();

    private String m_targetNamespace = null;
    private File xsdEpisodeFile;
    private File sourceOutputDir;
    private File resourceOutputDir;
    private File episodeFile;
    private File catalogFile;
    private Log log;
    private boolean verbose = false;

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public File getCatalogFile() {
        return catalogFile;
    }

    public void setCatalogFile(File catalogFile) {
        this.catalogFile = catalogFile;
        if( (catalogFile != null) && (catalogFile.exists())) {
            // Xerces Catalog Reference
            System.setProperty("xml.catalog.files", catalogFile.getAbsolutePath());
        }
    }

    public File getSourceOutputDir() {
        return sourceOutputDir;
    }

    public File getXsdEpisodeFile() {
        return xsdEpisodeFile;
    }

    public void setXsdEpisodeFile(File xsdSourceFile) {
        this.xsdEpisodeFile = xsdSourceFile;
    }

    public void setSourceOutputDir(File sourceOutputDir) {
        this.sourceOutputDir = sourceOutputDir;
    }

    public File getResourceOutputDir() {
        return resourceOutputDir;
    }

    public void setResourceOutputDir(File resourceOutputDir) {
        this.resourceOutputDir = resourceOutputDir;
    }

    public File getEpisodeFile() {
        return episodeFile;
    }

    public void setEpisodeFile(File episodeFile) {
        this.episodeFile = episodeFile;
    }

    private String parseAttribute(XMLStreamReader xmlStreamReader, String attributeName) {
        String attributeValue = null;
        for (int i = 0; i < xmlStreamReader.getAttributeCount(); i++) {
            if (xmlStreamReader.getAttributeLocalName(i).equals(attributeName))
                attributeValue = xmlStreamReader.getAttributeValue(i);
        }
        return attributeValue;
    }

    public void readXSD() throws Exception {
        log.info("Reading Episode Schema");
        log.debug("Episode Schema: " + xsdEpisodeFile);
        assertFileExists(xsdEpisodeFile);
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        InputStream input = new FileInputStream(xsdEpisodeFile);
        XMLStreamReader xmlStreamReader = inputFactory.createXMLStreamReader(input);

        while (xmlStreamReader.hasNext()) {
            int event = xmlStreamReader.next();

            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlStreamReader.getLocalName().equals("schema")) {
                    m_targetNamespace = parseAttribute(xmlStreamReader, "targetNamespace");
                }
                if (xmlStreamReader.getLocalName().equals("complexType")) {
                    m_typesDefined.add(parseAttribute(xmlStreamReader, "name"));
                }
            }
        }
    }

    public void filterEpisode() throws Exception {
        log.info("Filtering XJC Generated Episode");
        log.debug("Episode File: " + episodeFile);
        File tempEpisodeFile = new File(episodeFile.getAbsolutePath() + ".temp");
        FileUtils.copyFile(episodeFile, tempEpisodeFile);

        DocumentBuilderFactory episodeDocBuilderFactory = DocumentBuilderFactory.newInstance();

        DocumentBuilder episodeDocBuilder = episodeDocBuilderFactory.newDocumentBuilder();
        Document episodeDoc = episodeDocBuilder.parse(tempEpisodeFile);

        Element docElement = episodeDoc.getDocumentElement();
        NodeList childNodeList = docElement.getChildNodes();

        Element requiredBindingNode = null;

        for (int i = 0; i < childNodeList.getLength(); i++) {
            Node node = childNodeList.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue; // Only interested in element nodes.
            }
            Element elem = (Element) node;
            NamedNodeMap attributeNodeMap = elem.getAttributes();
            if (attributeNodeMap != null) {
                for (int j = 0; j < attributeNodeMap.getLength(); j++) {
                    Node mapNode = attributeNodeMap.item(j);
                    String nsValue = mapNode.getNodeValue();
                    if (nsValue.equals(m_targetNamespace)) {
                        requiredBindingNode = elem;
                        if (log.isDebugEnabled()) {
                            log.debug("Found required binding node: " + debug(requiredBindingNode));
                        }
                        break;
                    }
                }
            }
        }

        if (requiredBindingNode == null) {
            String err = String.format("Unable to find Required Binding Node matching target namespace: %s",
                            m_targetNamespace);
            log.error(err);
            throw new MojoFailureException(err);
        }

        NodeList childNodesOfBinding = requiredBindingNode.getChildNodes();
        for (int i = 0; i < childNodesOfBinding.getLength(); i++) {
            Node node = childNodesOfBinding.item(i);
            if (node == null) {
                continue; // skip null node.
            }
            if (node.getNodeType() == Node.COMMENT_NODE) {
                // Remove COMMENT nodes.
                requiredBindingNode.removeChild(node);
                i--;
                continue;
            }
            if (node.getNodeType() == Node.TEXT_NODE) {
                // Remove TEXT nodes.
                requiredBindingNode.removeChild(node);
                i--;
                continue;
            }
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;
                if (elem.getNodeName().equals("schemaBindings")) {
                    log.debug("Removing <schemaBindings>");
                    requiredBindingNode.removeChild(elem);
                    i--;
                    continue;
                }

                NamedNodeMap attributeNodeMap = elem.getAttributes();
                if (attributeNodeMap != null) {
                    for (int j = 0; j < attributeNodeMap.getLength(); j++) {
                        Node mapNode = attributeNodeMap.item(j);
                        String nodeName = mapNode.getNodeValue();
                        if (nodeName != null && nodeName.contains(":")) {
                            nodeName = nodeName.substring(nodeName.indexOf(":") + 1);
                        }
                        if (m_typesDefined.contains(nodeName)) {
                            if (log.isDebugEnabled()) {
                                log.debug("Removing Element: " + debug(elem));
                            }
                            requiredBindingNode.removeChild(elem);
                            i--;
                            break;
                        }
                    }
                }
            }
        }

        if (childNodesOfBinding.getLength() == 0) {
            docElement.removeChild(requiredBindingNode);
        }

        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();
        DOMSource source = new DOMSource(episodeDoc);
        StreamResult result = new StreamResult(episodeFile);
        transformer.transform(source, result);

        tempEpisodeFile.delete();
    }

    private String debug(Element elem) {
        StringBuilder buf = new StringBuilder();
        buf.append('<');
        if (elem.getPrefix() != null) {
            buf.append(elem.getPrefix());
            buf.append(':');
        }
        buf.append(elem.getNodeName());
        NamedNodeMap attrs = elem.getAttributes();
        if (attrs != null) {
            int len = attrs.getLength();
            for (int i = 0; i < len; i++) {
                buf.append(' ');
                Attr attr = (Attr) attrs.item(i);
                if (attr.getPrefix() != null) {
                    buf.append(attr.getPrefix()).append(':');
                }
                buf.append(attr.getName());
                buf.append("=\"").append(attr.getValue()).append('\"');
            }
        }
        buf.append('>');
        return buf.toString();
    }

    public void xjcCreateEpisode() throws Exception {
        log.info("Creating Episode with XJC");
        log.debug("Episode Input: " + xsdEpisodeFile);
        log.debug("Episode Output: " + episodeFile);

        LinkedList<String> args = new LinkedList<String>();
        args.add("-nv");
        args.add("-extension");
        if (catalogFile != null) {
            args.add("-catalog");
            args.add(catalogFile.getAbsolutePath());
        }
        args.add("-d");
        args.add(sourceOutputDir.getAbsolutePath());
        args.add("-episode");
        args.add(episodeFile.getAbsolutePath());
        args.add(xsdEpisodeFile.getAbsolutePath());

        executeXJC("Create Episode Files", args);
    }

    public void generateEpisodeSourceFiles() throws Exception {
        log.info("Creating Episode Source");
        log.debug("Episode Schema: " + xsdEpisodeFile);
        log.debug("Episode File: " + episodeFile);
        log.debug("Output Dir: " + sourceOutputDir);
        
        LinkedList<String> args = new LinkedList<String>();
        args.add("-nv");
        args.add("-extension");
        if (catalogFile != null) {
            args.add("-catalog");
            args.add(catalogFile.getAbsolutePath());
        }
        args.add("-d");
        args.add(sourceOutputDir.getAbsolutePath());
        args.add("-b");
        args.add(episodeFile.getAbsolutePath());
        args.add(xsdEpisodeFile.getAbsolutePath());
        
        executeXJC("Creating Episode Source", args);
    }

    public void generateSourceFiles(File schemaFile) throws Exception {
        log.info("Creating Source Files: " + schemaFile.getName());
        log.debug("Schema File: " + schemaFile);
        log.debug("Output Dir: " + sourceOutputDir);
        
        LinkedList<String> args = new LinkedList<String>();
        args.add("-nv");
        args.add("-extension");
        if (catalogFile != null) {
            args.add("-catalog");
            args.add(catalogFile.getAbsolutePath());
        }
        args.add("-d");
        args.add(sourceOutputDir.getAbsolutePath());
        args.add(schemaFile.getAbsolutePath());
        
        executeXJC("Creating Source Files: " + schemaFile.getName(), args);
    }

    // TODO: Should be able to specify the ObjectFactory to delete.
    public void deleteObjectFactory() throws MojoExecutionException {
        String objectFactoryLoc = sourceOutputDir.getAbsolutePath() + File.separator + "org" + File.separator
                        + "ebayopensource" + File.separator + "turmeric" + File.separator + "common" + File.separator
                        + "config" + File.separator + "ObjectFactory.java";
        File objectFactory = new File(objectFactoryLoc);
        if (objectFactory.exists()) {
            log.debug("Deleting object factory: " + objectFactory);
            if (!objectFactory.delete()) {
                throw new MojoExecutionException("Unable to delete object factory: " + objectFactory.getAbsolutePath());
            }
        }
    }

    private void executeXJC(String purpose, LinkedList<String> args) throws Exception {
        if (log.isDebugEnabled()) {
            args.addFirst("-debug");
            StringBuilder msg = new StringBuilder();
            msg.append("Passing the following args to ");
            msg.append(Driver.class.getName()).append("#run()");
            for (String arg : args) {
                msg.append("\n  \"").append(arg).append("\",");
            }
            log.debug(msg.toString());
        }
        String[] xjcArguments = args.toArray(new String[0]);
        PrintStream status = null;
        if (verbose) {
            status = System.out;
        }
        int result = Driver.run(xjcArguments, status, System.out);
        if (result != 0) {
            String err = String.format("XJC Failure: Failed to %s (error code=%d) - See console for details", purpose,
                            result);
            throw new MojoExecutionException(err);
        }
    }

    private void assertFileExists(File file) throws MojoExecutionException {
        if (!file.exists()) {
            log.warn("File not found: " + file.getPath());
            throw new MojoExecutionException("File not found: " + file.getAbsolutePath());
        }
    }

    public void setLog(Log log) {
        this.log = log;
    }
}
