/*******************************************************************************
 *     Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 *     Licensed under the Apache License, Version 2.0 (the "License"); 
 *     you may not use this file except in compliance with the License. 
 *     You may obtain a copy of the License at 
 *    
 *        http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils.config.impl;


import org.ebayopensource.turmeric.utils.XMLParseUtils;
import org.ebayopensource.turmeric.utils.config.PolicyProvider;
import org.ebayopensource.turmeric.utils.config.exceptions.PolicyProviderException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Base File Policy provider class
 * @author gyue
 *  
 */ 
public abstract class BaseFilePolicyProvider implements PolicyProvider{
	
	private static final String BASE_PATH = "META-INF/security/";
	private static final String BASE_SCHEMA_PATH = "META-INF/security/schema/";
	private static final String POLICY_RELATIVE_PATH = "policy";

	protected String m_policyPath = BASE_PATH + POLICY_RELATIVE_PATH + "/";
	protected String m_schemaPath = BASE_SCHEMA_PATH + POLICY_RELATIVE_PATH + "/";
	private String m_policyRelativePath = POLICY_RELATIVE_PATH;
	
	protected Element m_policyData = null;
	protected boolean m_policyLoaded = false;
	
	public static final String SYS_PROP_POLICY_LOCATION 
				= "com.ebay.securityframework.impl.policy.location";
	protected static String s_system_policy_location = null;
	
	static {
		// read from system property for the location of the config
		s_system_policy_location = System.getProperty(SYS_PROP_POLICY_LOCATION);
	}
	
	public void initialize() throws PolicyProviderException {
		loadPolicy();
	}
	
	protected synchronized void loadPolicy() throws PolicyProviderException {
		if (m_policyLoaded) {
			return;
		}
		// system config location takes precedence over the default ones
		if (s_system_policy_location != null) {
			m_policyPath = s_system_policy_location;
		}

		loadPolicy(getPolicyFileName());
		m_policyLoaded = true;
	}
	
	protected synchronized void loadPolicy(String fileName) throws PolicyProviderException {
		if (m_policyData != null) {
			return;
		}
		loadPolicyDataFromXMLFile();
   	 	mapPolicyData(m_policyData);
	}
	
	protected synchronized void loadPolicy(String fileName, String schemaName, String rootElement) throws PolicyProviderException {
		if (m_policyData != null) {
			return;
		}
		loadPolicyDataFromXMLFile(fileName, schemaName, rootElement);
   	 	mapPolicyData(m_policyData);
	}

	protected void loadPolicyDataFromXMLFile() throws PolicyProviderException {
		loadPolicyDataFromXMLFile(getPolicyFileName(), getPolicySchemaName(), getPolicyRootElement());
	}
		
	protected void loadPolicyDataFromXMLFile(String fileName, String schemaName, String rootElement) throws PolicyProviderException {
		if (m_policyData != null)
			return;
		Document globalDoc = null;
		try { 
			//System.out.println("loadPolicyDataFromXMLFile: " + fileName);
			globalDoc = XMLParseUtils.parseXML(fileName, schemaName, true, rootElement);
		} catch (Exception e) {
			throw new PolicyProviderException(
					"Parse XML failed: " + e.getMessage(), 
					e);
		}
		if (globalDoc != null) {
			m_policyData = globalDoc.getDocumentElement();
		}
	}

	protected synchronized void setPolicyPath(String path) {
		m_policyPath = path;
	}

	public synchronized void setPolicyTestCase(String relativePath, boolean force) throws PolicyProviderException {
		m_policyRelativePath = relativePath;
		String newPath = BASE_PATH + relativePath + "/";
		if (!force && m_policyPath != null && m_policyPath.equals(newPath)) {
			return;
		}

		m_policyLoaded = false;
		m_policyData = null;

		setPolicyPath(newPath);

		// reload policy again
		loadPolicy();
	}

	public void setPolicyTestCase(String relativePath) throws PolicyProviderException {
		setPolicyTestCase(relativePath, false);
	}
	
	public String getPolicyTestCase() {
		return m_policyRelativePath;
	}
	
	
	protected abstract String getPolicyFileName();
	
	protected abstract String getPolicySchemaName();
	
	protected abstract String getPolicyRootElement();
	
	protected abstract void mapPolicyData(Element policyData) throws PolicyProviderException;

	
	

}
