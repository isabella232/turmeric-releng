/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils.cassandra.dao;

import java.util.Set;

import org.ebayopensource.turmeric.utils.cassandra.model.Model;

/**
 * The Class ModelDaoImpl.
 * @author jamuguerza
 */
public class ModelDaoImpl extends
		AbstractColumnFamilyDao<String, Model> implements ModelDao {

	/**
	 * Instantiates a new model dao impl.
	 *
	 * @param clusterName the cluster name
	 * @param host the host
	 * @param keySpace the key space
	 * @param cf the cf
	 */
	public ModelDaoImpl(final String clusterName,  final String host, final String keySpace, final String cf) {
		super(clusterName, host, keySpace, String.class, Model.class,  cf);
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.utils.cassandra.dao.ModelDao#save(org.ebayopensource.turmeric.utils.cassandra.model.Model)
	 */
	public void save(final Model testModel) {
		super.save(testModel.getKey(), testModel);
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.utils.cassandra.dao.ModelDao#delete(org.ebayopensource.turmeric.utils.cassandra.model.Model)
	 */
	public void delete(final Model testModel) {
		delete(testModel.getKey());
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.utils.cassandra.dao.ModelDao#getAllKeys()
	 */
	@Override
	public Set<String> getAllKeys() {
		return super.getKeys();
	}
	
	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.utils.cassandra.dao.AbstractColumnFamilyDao#containsKey(java.lang.Object)
	 */
	public boolean containsKey(final String key){
		return super.containsKey(key);
	}

}