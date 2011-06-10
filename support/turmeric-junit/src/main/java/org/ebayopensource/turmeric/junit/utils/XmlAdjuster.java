package org.ebayopensource.turmeric.junit.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * Utility for adjusting pre-written XML content.
 * <p>
 * Example of use:
 * <pre>
 *   File xmlActual = TestResourceUtil.copyResource("soa/service/example.xml", testingdir, "meta-src");
 *      
 *   // Adjust path entries in XML 
 *   Map<String, String> ns = new HashMap<String,String>();
 *   ns.put("c", "http://www.ebay.com/soaframework/tools/codegen/common");
 *   Map<String, String> entries = new HashMap<String, String>();
 *   entries.put("//c:service-code-gen/c:interface-info/c:wsdl-def/c:wsdl-file", wsdl.getAbsolutePath());
 *   entries.put("//c:service-code-gen/c:tool-input-info/c:src-location", srcDir.getAbsolutePath());
 *   entries.put("//c:service-code-gen/c:tool-input-info/c:dest-location", destDir.getAbsolutePath());
 *   entries.put("//c:service-code-gen/c:tool-input-info/c:bin-location", binDir.getAbsolutePath());
 *   XmlAdjuster.correct(xmlActual, ns, entries);
 * </pre>
 */
public class XmlAdjuster {

	/**
	 * Correct the xml entries as specified.
	 * 
	 * @param xmlFile
	 *            the file to correct.
	 * @param entries
	 *            the map of entries to correct. (Map.key is the xpath Map.value is the value to use)
	 * @throws IOException
	 * @throws JDOMException
	 * @throws JaxenException
	 */
	public static void correct(File xmlFile, Map<String, String> namespaceMap,
			Map<String, String> entries) throws IOException, JDOMException,
			JaxenException {
		Document doc = readXml(xmlFile);

		for (Map.Entry<String, String> xpathEntry : entries.entrySet()) {
			XPath expression = new JDOMXPath(xpathEntry.getKey());
			if (namespaceMap != null) {
				for (Map.Entry<String, String> ns : namespaceMap.entrySet()) {
					expression.addNamespace(ns.getKey(), ns.getValue());
				}
			}

			@SuppressWarnings("unchecked")
			List<Element> elements = expression.selectNodes(doc);
			for (Element elem : elements) {
				elem.setText(xpathEntry.getValue());
			}
		}

		writeXml(xmlFile, doc);
	}

	public static Document readXml(File xmlFile) throws JDOMException,
			IOException {
		SAXBuilder builder = new SAXBuilder(false);
		return builder.build(xmlFile);
	}

	public static void writeXml(File xmlFile, Document doc) throws IOException {
		FileWriter writer = null;
		try {
			writer = new FileWriter(xmlFile);
			XMLOutputter serializer = new XMLOutputter();
			serializer.getFormat().setIndent("  ");
			serializer.getFormat().setLineSeparator(SystemUtils.LINE_SEPARATOR);
			serializer.output(doc, writer);
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

}
