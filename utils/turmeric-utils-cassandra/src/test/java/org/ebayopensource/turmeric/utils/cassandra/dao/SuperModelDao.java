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

import org.ebayopensource.turmeric.utils.cassandra.model.SuperModel;

/**
 * The Interface SuperModelDao.
 * @author jamuguerza
 */
public interface SuperModelDao {
	
	/**
	 * Save.
	 *
	 * @param testSuperModel the test model
	 */
	public void save(SuperModel testSuperModel);


	/**
	 * Find.
	 *
	 * @param key the key
	 * @param columnNames the column names
	 * @return the model
	 */
	public SuperModel find(String key, String [] columnNames );

	/**
	 * Delete.
	 *
	 * @param testSuperModel the test super model
	 */
	public void delete(SuperModel testSuperModel);
	
	/**
	 * Contains key.
	 *
	 * @param key the key
	 * @return true, if successful
	 */
	public boolean containsKey(String key);
	

	/**
	 * Gets the all keys.
	 *
	 * @return the all keys
	 */
	public Set<String> getAllKeys();
	
	/**
	 * Find items.
	 *
	 * @param superKeys the super keys
	 * @param columnNames the column names
	 * @return the sets the
	 */
	public  Map<String, SuperModel> findItems(final List<String> superKeys, final String [] columnNames );;
}