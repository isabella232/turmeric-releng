/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils.cassandra.service;

import java.util.Set;

import org.ebayopensource.turmeric.utils.cassandra.dao.ModelDao;
import org.ebayopensource.turmeric.utils.cassandra.dao.ModelDaoImpl;
import org.ebayopensource.turmeric.utils.cassandra.model.Model;

/**
 * The Class ModelServiceImpl.
 * @author jamuguerza
 */
public class ModelServiceImpl implements ModelService {

	/** The test model dao. */
	private final ModelDao testModelDao;

	/**
	 * Instantiates a new model service impl.
	 * 
	 * @param clusterName
	 *            the cluster name
	 * @param host
	 *            the host
	 * @param keySpace
	 *            the key space
	 * @param cf
	 *            the cf
	 */
	public ModelServiceImpl(final String clusterName, final String host,
			final String keySpace, final String cf) {
		testModelDao = new ModelDaoImpl(clusterName, host, keySpace, cf);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.ebayopensource.turmeric.utils.cassandra.service.ModelService#getTestModel
	 * (java.lang.String)
	 */
	@Override
	public Model getTestModel(final String key) {
		return testModelDao.find(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ebayopensource.turmeric.utils.cassandra.service.ModelService#
	 * deleteTestModel(org.ebayopensource.turmeric.utils.cassandra.model.Model)
	 */
	@Override
	public void deleteTestModel(final Model testModel) {
		testModelDao.delete(testModel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.ebayopensource.turmeric.utils.cassandra.service.ModelService#getAllKeys
	 * ()
	 */
	@Override
	public Set<String> getAllKeys() {
		return testModelDao.getAllKeys();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ebayopensource.turmeric.utils.cassandra.service.ModelService#
	 * createTestModel(org.ebayopensource.turmeric.utils.cassandra.model.Model)
	 */
	@Override
	public void createTestModel(final Model testModel) {
		testModelDao.save(testModel);
	}

	@Override
	public boolean  containsModel(final String key) {
		return testModelDao.containsKey(key);
	}

}