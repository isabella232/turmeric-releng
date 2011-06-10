/*******************************************************************************
 *     Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 *     Licensed under the Apache License, Version 2.0 (the "License"); 
 *     you may not use this file except in compliance with the License. 
 *     You may obtain a copy of the License at 
 *    
 *        http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.ebayopensource.turmeric.utils.config.NameValue;
import org.ebayopensource.turmeric.utils.config.NodeListAdaptor;
import org.ebayopensource.turmeric.utils.config.OptionList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class DomParseUtils {
	public static Element getSingleElement(String filename, Element parent, String name) throws Exception {
		return (Element) getSingleNode(filename, parent, name);
	}
	
	public static Node getSingleNode(String filename, Element parent, String name) throws Exception {
		NodeList nodes = getImmediateChildrenByTagName(parent, name);
		if (nodes.getLength() == 0) {
			return null;
		}
		if (nodes.getLength() > 1) {
			throwError(filename, "Extra element values seen for element " + name);
		}
		return nodes.item(0);
	}
	
	public static NodeList getImmediateChildrenByTagName(Element parent, String name) throws Exception {
		NodeListAdaptor result = new NodeListAdaptor();
		NodeList nodes = parent.getElementsByTagName(name);
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node.getParentNode() == parent) {
				result.add(node);
			}
		}
		return result;
	}
	public static String getElementText(String filename, Element parent, String name) throws Exception {
		return getElementText(filename, parent, name, false);
	}
	
	
	public static String getElementText(String filename, Element parent, String name, boolean isRequired) throws Exception {
		Element node = getSingleElement(filename, parent, name);
		if (node == null) {
			if (!isRequired) {
				return null;
			}
			throwError(filename, "Missing required element: '" + name + "'");
		}
		return getText(node);
	}
	
	public static String getText(Node node) {
		StringBuffer result = new StringBuffer();
		NodeList nodes = node.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node2 = nodes.item(i);
			if (node2.getNodeType() == Node.TEXT_NODE || node2.getNodeType() == Node.CDATA_SECTION_NODE) {
				String value = node2.getNodeValue();
				result.append(value);
			}
		}
		return result.toString().trim();
	} 
	
	public static Integer getElementInteger(String filename, Element parent, String name) throws Exception {
		String text = getElementText(filename, parent, name);
		return textToInteger(filename, text, name);
	}
	
	public static Boolean getElementBoolean(String filename, Element parent, String name) throws Exception {
		String text = getElementText(filename, parent, name);
		return textToBoolean(filename, text, name);
	}
	
	public static Integer getAttributeInteger(String filename, Element parent, String name) throws Exception {
		String attrStr = parent.getAttribute(name);
		if (attrStr == null) {
			return null;
		}
		return textToInteger(filename, attrStr, name);
	}
	
	public static void storeNVListToHashMap(String filename, OptionList options, Map<String, String> dstMap) {
		if (options == null || options.getOption() == null || options.getOption().isEmpty()) {
			return;
		}
		List<NameValue> optionNvList = options.getOption();
		for (int i=0; i<optionNvList.size(); i++) {
			NameValue nv = optionNvList.get(i);
			dstMap.put(nv.getName(), nv.getValue());
		}
	}
	
	public static OptionList getOptionList(String filename, Element parent, String childName) throws Exception {
		if (parent == null) {
			return null;
		}
		OptionList result = new OptionList();
		Element optionContainer = DomParseUtils.getSingleElement(filename, parent, childName);
		if (optionContainer == null) {
			return null;
		}
		List<NameValue> outList = result.getOption();
		putNVList(filename, childName, optionContainer, outList);
		return result;
	}
	
	public static void putNVList(String filename, String containerName, Element optionContainer, List<NameValue> outList) throws Exception {
		NodeList childElements = DomParseUtils.getImmediateChildrenByTagName(optionContainer, "option");
		if (childElements == null) {
			return;
		}
		for (int i = 0; i < childElements.getLength(); i++) {
			Element option = (Element) childElements.item(i);
			String name = option.getAttribute("name");
			if (name == null || name.length() == 0) {
				throwError(filename, "Missing option name in option list: '" + containerName + "'");
			}
			String value = DomParseUtils.getText(option);
			if (value == null) {
				throwError(filename, "Missing option value for option list: '" + containerName + "'");
			}
			NameValue nv = new NameValue();
			nv.setName(name);
			nv.setValue(value);
			outList.add(nv);
		}
	}
	
	public static List<String> getStringList(String filename, Element parent, String name) throws Exception {
		NodeList childElements = DomParseUtils.getImmediateChildrenByTagName(parent, name);
		if (childElements == null) {
			return null;
		}
		ArrayList<String> result = new ArrayList<String>(); 
		for (int i = 0; i < childElements.getLength(); i++) {
			Element oneElement = (Element) childElements.item(i);
			String outValue = DomParseUtils.getText(oneElement);
			result.add(outValue);
		}
		return result;
	}
	
	public static List<Integer> getIntegerList(String filename, Element parent, String name) throws Exception {
		NodeList childElements = DomParseUtils.getImmediateChildrenByTagName(parent, name);
		if (childElements == null) {
			return null;
		}
		ArrayList<Integer> result = new ArrayList<Integer>(); 
		for (int i = 0; i < childElements.getLength(); i++) {
			Element oneElement = (Element) childElements.item(i);
			String text = DomParseUtils.getText(oneElement);
			Integer intValue = textToInteger(filename, text, name);
			result.add(intValue);
		}
		return result;
	}
	
	public static List<Long> getLongList(String filename, Element parent, String name) throws Exception {
		NodeList childElements = DomParseUtils.getImmediateChildrenByTagName(parent, name);
		if (childElements == null) {
			return null;
		}
		ArrayList<Long> result = new ArrayList<Long>(); 
		for (int i = 0; i < childElements.getLength(); i++) {
			Element oneElement = (Element) childElements.item(i);
			String text = DomParseUtils.getText(oneElement);
			Long intValue = textToLong(filename, text, name);
			result.add(intValue);
		}
		return result;
	}
	


	public static QName getQName(String configFilename, String serviceName, String namespaceURI, String tagName) {
		QName qname = QName.valueOf(serviceName);
		return qname;
	}
	
	public static String getRequiredAttribute(String filename, Element element, String name) throws Exception {
		String value = element.getAttribute(name);
		if (value == null || value.length() == 0) {
			throwError(filename, "Missing required attribute '" + name + "' on element '" + element.getTagName() + "'");
		}
		return value;
	}


	public static void throwError(String filename, String cause) throws Exception {
		throw new Exception("validation error. Filename= " + filename + ", cause= " + cause);
	}
	
	private static Integer textToInteger(String filename, String text, String name) throws Exception {
		if (text == null || text.length() == 0) {
			throw new Exception("Bad integer. Filename= " + filename + ", name= " + name);
		}
		Integer intValue = null;
		try {
			intValue = Integer.valueOf(text);
		} catch (NumberFormatException e) {
			throw new Exception("Bad integer. Filename= " + filename + ", exception= " + e.toString());
		}
		return intValue;
	}
	
	private static Long textToLong(String filename, String text, String name) throws Exception {
		if (text == null || text.length() == 0) {
			throw new Exception("Bad long. Filename= " + filename + ", name= " + name);
		}
		Long intValue = null;
		try {
			intValue = Long.valueOf(text);
		} catch (NumberFormatException e) {
			throw new Exception("Bad long. Filename= " + filename + ", exception= " + e.toString());
		}
		return intValue;
	}

	private static Boolean textToBoolean(String filename, String text, String name) throws Exception {
		if (text == null || text.length() == 0) {
			throw new Exception("Bad boolean. Filename= " + filename + ", name= " + name);
		}
		Boolean booleanValue = Boolean.valueOf(text);
		if (booleanValue == null) {
			throw new Exception("Bad boolean. Filename= " + ", cause= null value");
		}
		return booleanValue;
	}

	public static Document deserializeDOMTree(final String xmlDocument,
			final boolean validate) throws Exception {

		final DocumentBuilderFactory factory = DocumentBuilderFactory
				.newInstance();
		final DocumentBuilder builder = factory.newDocumentBuilder();
		final InputStream input = new BufferedInputStream(
				new ByteArrayInputStream(xmlDocument.getBytes()));
		final Document document = builder.parse(input);
		return document;
	}

	/**
     * Get root element of the given document.
     */
    public static Element getRoot(final Document doc)
    {
        if (doc != null)
            return doc.getDocumentElement();
        return null;
    }

    public static Element getChildByName(final Element parent, final String name){
        if (parent == null || name == null)
            return null;
        final NodeList list = parent.getChildNodes();
        for (int k=0, listCount = list.getLength(); k<listCount; k++)
        {
            final Node child = list.item(k);
            if (child.getNodeType() == Node.ELEMENT_NODE &&
                    nodeNameEqualToWithNoNamespace(child, name))
                return (Element)child;
        }
        return null;
    }

    public static boolean nodeNameEqualToWithNoNamespace(final Node node, final String
            target){
        if (node == null || target == null)
            return false;
        String name = node.getNodeName();
        final int index = name.indexOf(':');
        if (index >= 0)
            name = name.substring(index+1); // Strip namespace
        return name.equals(target);
    }

    /**
     * Get a child element from given node with given name.
     * Return "" if not found or error.
     */
    public static String getChildValue(final Element parent, final String tag){
        final Element child = getChild(parent, tag);
        if (child == null)
            return "";
        String str = getNodeValue(child);
        if (str == null || str.equals("null"))
            str = "";
        return str;
    }

    /**
     * Get child node with given tag name.
     * Return null if no child can be found.
     */
    public static Element getChild(final Element parent, final String tag){
        if (parent == null || tag == null)
            return null;
        final NodeList list = parent.getChildNodes();
        for (int k=0, listCount = list.getLength(); k<listCount; k++)
        {
            final Node child = list.item(k);
            if (child.getNodeType() == Node.ELEMENT_NODE &&
                    nodeNameEqualTo(child, tag))
                return (Element)child;
        }
        return null;
    }
    
    /**
     * Check whether a name of given node is equal to a given name.
     * Strips namespace (if any). Case-sensitive.
     */
    public static boolean nodeNameEqualTo(final Node node, final String target){
        if (node == null || target == null)
            return false;
        String name = node.getNodeName();
        if (target.indexOf(':') < 0) {      // If target contains namespace, require exact match
            final int index = name.indexOf(':');
            if (index >= 0)
                name = name.substring(index+1); // Strip namespace
        }
        return name.equals(target);
    }



    /**
     * Get value of a given node.
     */
    public static String getNodeValue(final Element node)
    {
        if (node == null)
            return null;
        final String result = node.getNodeValue();
        if (result != null)
            return result;

        final NodeList list = node.getChildNodes();
        if (list == null || list.getLength()==0)
            return null;
        final StringBuffer buff = new StringBuffer();
        boolean counter = false;

        for (int k=0, listCount=list.getLength(); k<listCount; k++)
        {
            final Node child = list.item(k);
            if (child.getNodeType()==Node.TEXT_NODE ||
                    child.getNodeType()==Node.CDATA_SECTION_NODE)
            {
                buff.append(child.getNodeValue());
                counter = true;
            }
            else if
            (child.getNodeType()==Node.ENTITY_REFERENCE_NODE ||
                    child.getNodeType()==Node.ENTITY_NODE)
            {
                final Node child2 = child.getFirstChild();
                if (child2 != null && child2.getNodeValue() !=
                    null)
                    buff.append(child2.getNodeValue());
                counter = true;
            }
        }

        if (counter == false)
            return null;
        return buff.toString();
    }

    
    public static void main(final String[] args)
    {
        try {
        	//final String src = "<RLRuleExprValidation><Status>Success</Status></RLRuleExprValidation>";
            final String src = "<RLRuleExprValidation><Status>Failure</Status><Error>TestError</Error></RLRuleExprValidation>";
            final Document domMessage = DomParseUtils.deserializeDOMTree(src,false);

            final Element root = getRoot(domMessage);
            final Element status = getChildByName(root, "Status");
            String responseStatus = getNodeValue(status);
            System.out.println("The response status is ==> " + responseStatus);
            if( !"Success".equalsIgnoreCase(responseStatus) ) {
            	final Element error = getChildByName(root, "Error");
            	String errorMsg = getNodeValue(error);
            	System.out.println("The error message is ==> " + errorMsg);
            }            
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    
}
