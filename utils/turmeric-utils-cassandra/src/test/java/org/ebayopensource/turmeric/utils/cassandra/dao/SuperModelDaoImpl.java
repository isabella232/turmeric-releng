/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils.cassandra.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ebayopensource.turmeric.utils.cassandra.model.Model;
import org.ebayopensource.turmeric.utils.cassandra.model.SuperModel;


/**
 * The Class SuperModelDaoImpl.
 * @author jamuguerza
 */
public class SuperModelDaoImpl extends
		AbstractSuperColumnFamilyDao<String, SuperModel, String, Model> implements SuperModelDao {

	/**
	 * Instantiates a new model dao impl.
	 *
	 * @param clusterName the cluster name
	 * @param host the host
	 * @param keySpace the key space
	 * @param cf the cf
	 */
	public SuperModelDaoImpl(final String clusterName,  final String host, final String keySpace, final String cf) {
		super(clusterName, host, keySpace, String.class,  SuperModel.class, String.class, Model.class,  cf);
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.utils.cassandra.dao.ModelDao#save(org.ebayopensource.turmeric.utils.cassandra.model.Model)
	 */
	public void save(final SuperModel testSuperModel) {
		super.save(testSuperModel.getKey(), testSuperModel.getColumns());
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.utils.cassandra.dao.ModelDao#delete(org.ebayopensource.turmeric.utils.cassandra.model.Model)
	 */
	public void delete(final SuperModel testSuperModel) {
		super.delete(testSuperModel.getKey());
	}
	
	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.utils.cassandra.dao.SuperModelDao#delete(org.ebayopensource.turmeric.utils.cassandra.model.SuperModel)
	 */
	public SuperModel find(final String superKey, final String [] columnNames ) {
		return super.find(superKey, columnNames );
	}
	
	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.utils.cassandra.dao.AbstractColumnFamilyDao#containsKey(java.lang.Object)
	 */
	public boolean containsKey(final String key){
		return super.containsKey(key);
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.utils.cassandra.dao.SuperModelDao#getAllKeys()
	 */
	@Override
	public Set<String> getAllKeys() {
		return super.getKeys();
	}
	
	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.utils.cassandra.dao.AbstractColumnFamilyDao#findItems(java.util.List, java.lang.String, java.lang.String)
	 */
	public Map<String, SuperModel> findItems(final List<String> superKeys, final String [] columnNames ) {
		return super.findItems(superKeys,  columnNames);
	}
	
}