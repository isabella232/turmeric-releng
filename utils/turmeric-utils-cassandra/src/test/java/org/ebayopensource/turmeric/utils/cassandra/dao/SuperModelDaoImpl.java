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

import org.ebayopensource.turmeric.utils.ContextUtils;
import org.ebayopensource.turmeric.utils.cassandra.model.Model;
import org.ebayopensource.turmeric.utils.cassandra.model.SuperModel;


/**
 * The Class SuperModelDaoImpl.
 * @author jamuguerza
 */
public class SuperModelDaoImpl<SK, K> extends
		AbstractSuperColumnFamilyDao<SK, SuperModel , K, Model> implements SuperModelDao<SK, K> {

	/**
	 * Instantiates a new model dao impl.
	 *
	 * @param clusterName the cluster name
	 * @param host the host
	 * @param keySpace the key space
	 * @param cf the cf
	 */
	public SuperModelDaoImpl(final String clusterName,  final String host, final String keySpace, final String cf, final Class<SK> sKTypeClass, final Class<K> kTypeClass) {
		super(clusterName, host, keySpace, sKTypeClass,  SuperModel.class, kTypeClass, Model.class,  cf);
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.utils.cassandra.dao.ModelDao#save(org.ebayopensource.turmeric.utils.cassandra.model.Model)
	 */
	public void save(final SuperModel<?, ?> testSuperModel) {
		super.save( (SK) testSuperModel.getKey(), (Map<K, Model>) testSuperModel.getColumns());
	}

	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.utils.cassandra.dao.ModelDao#delete(org.ebayopensource.turmeric.utils.cassandra.model.Model)
	 */
	public void delete(final SuperModel<?,?> testSuperModel) {
		super.delete((SK)testSuperModel.getKey());
	}

	

//	public SuperModel<?, ?> find(final SK superKey, final K [] columnNames ) {
//		return super.find(superKey, columnNames );
//	}

	
	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.utils.cassandra.dao.AbstractColumnFamilyDao#containsKey(java.lang.Object)
	 */
//	public boolean containsKey(SK key) {
//		return super.containsKey((SK)key);
//	}
	
	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.utils.cassandra.dao.SuperModelDao#getAllKeys()
	 */
	@Override
	public Set<SK> getAllKeys() {
		return (Set<SK>) super.getKeys();
	}
	
	/* (non-Javadoc)
	 * @see org.ebayopensource.turmeric.utils.cassandra.dao.AbstractColumnFamilyDao#findItems(java.util.List, java.lang.String, java.lang.String)
	 */
	public Map<SK, SuperModel> findItems(final List<SK> superKeys, final K [] columnNames ) {
		return super.findItems(superKeys,  columnNames);
	}

	
//	public List<SuperModel>  find(K fromSCNmame, K toSCNmame){
//		return super.find(fromSCNmame, toSCNmame);
//	}


}