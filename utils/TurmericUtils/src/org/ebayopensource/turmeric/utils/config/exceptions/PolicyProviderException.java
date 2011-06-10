/*******************************************************************************
 *     Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 *     Licensed under the Apache License, Version 2.0 (the "License"); 
 *     you may not use this file except in compliance with the License. 
 *     You may obtain a copy of the License at 
 *    
 *        http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils.config.exceptions;



/**
 * Policy Provider Exception
 * 
 * @author gyue
 */
public class PolicyProviderException extends Exception {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean m_partialFailure = false;

	public PolicyProviderException(String errorMsg) {
		super(errorMsg);
	}

	public PolicyProviderException(Throwable cause) {
		super(cause);
	}
	
	public PolicyProviderException(String errorMessage, Throwable cause) {
		super(errorMessage, cause);
	}

	public boolean isPartialFailure() {
		return m_partialFailure;
	}

	public void setPartialFailure(boolean partialFailure) {
		m_partialFailure = partialFailure;
	}
}
