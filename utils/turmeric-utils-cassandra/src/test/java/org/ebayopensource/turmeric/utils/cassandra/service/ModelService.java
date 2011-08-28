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

import org.ebayopensource.turmeric.utils.cassandra.model.Model;




/**
 * The Interface ModelService.
 * @author jamuguerza
 */
public interface ModelService {

      /**
       * Gets the test model.
       *
       * @param key the key
       * @return the test model
       */
      public Model getTestModel(String key);

     /**
      * Delete test model.
      *
      * @param testModel the test model
      */
     public void deleteTestModel(Model testModel);

     /**
      * Contains model.
      *
      * @param key the key
      */
     public boolean containsModel(String key);
     
	    /**
    	 * Gets the all keys.
    	 *
    	 * @return the all keys
    	 */
    	public Set<String> getAllKeys();

	    /**
    	 * Creates the test model.
    	 *
    	 * @param testModel the test model
    	 */
    	public void createTestModel(Model testModel);
}