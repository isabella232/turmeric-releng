/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils.cassandra;

import org.ebayopensource.turmeric.utils.cassandra.server.CassandraTestManager;

/**
 * The Class BootStrap.
 * @author jamuguerza
 */
public class BootStrap {
  
  /** The started. */
  private static volatile boolean started;
  
  /** The monitor. */
  private static Object monitor = new Object();
  
  /** The server. */
  private static CassandraTestManager server;
  
  /**
   * Inits the.
   */
  public static void init() {
    if (started) {
      return;
    }
    synchronized (monitor) {
      server = new CassandraTestManager();
      try {
        server.setup();
        started = true;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
}
