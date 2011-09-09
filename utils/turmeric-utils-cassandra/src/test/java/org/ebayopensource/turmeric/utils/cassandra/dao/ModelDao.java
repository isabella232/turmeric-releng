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
import java.util.Set;

import org.ebayopensource.turmeric.utils.cassandra.model.Model;

/**
 * The Interface ModelDao.
 * @author jamuguerza
 */
public interface ModelDao {
	
	/**
	 * Save.
	 *
	 * @param testModel the test model
	 */
	public void save(Model testModel);

	/**
	 * Gets the all keys.
	 *
	 * @return the all keys
	 */
	public Set<String> getAllKeys();
	
	/**
	 * Find.
	 *
	 * @param key the key
	 * @return the model
	 */
	public Model find(String key);

	/**
	 * Delete.
	 *
	 * @param testModel the test model
	 */
	public void delete(Model testModel);
	
	/**
	 * Contains key.
	 *
	 * @param key the key
	 * @return true, if successful
	 */
	public boolean containsKey(String key);
	

	/**
	 * Find items.
	 *
	 * @param keys the keys
	 * @param rangeFrom the range from
	 * @param rangeTo the range to
	 * @return the sets the
	 */
	public Set<Model> findItems(final List<String> keys, final String rangeFrom, final String rangeTo ) ;
}