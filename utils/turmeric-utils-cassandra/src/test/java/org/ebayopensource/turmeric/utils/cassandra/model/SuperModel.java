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
public class SuperModel<SK, K>  {

	public SuperModel(SK superKeyType, K keyType){
	}
		
	/** The key data. */
	private SK key;
	
	private Map<K, Model> columns ;

	public void setKey(SK key) {
		this.key = key;
	}

	public SK getKey() {
		return key;
	}

	public void setColumns(Map<K, Model> columns) {
		this.columns = columns;
	}

	public Map<K, Model> getColumns() {
		return columns;
	}
		
}
