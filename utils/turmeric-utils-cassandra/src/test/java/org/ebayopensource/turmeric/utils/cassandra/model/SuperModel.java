/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils.cassandra.model;

import java.util.Map;


/**
 * The Class ModelSuper.
 * @author jamuguerza
 */
public class SuperModel  {

	/** The key data. */
	private String key;
	
	private Map<String, Model> columns ;

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setColumns(Map<String, Model> columns) {
		this.columns = columns;
	}

	public Map<String, Model> getColumns() {
		return columns;
	}
		
}
