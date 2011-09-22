/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils.cassandra.service;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.cassandra.service.EmbeddedCassandraService;
import org.ebayopensource.turmeric.utils.MemoryUsage;

/**
 * The Class CassandraManager.
 * 
 * @author jamuguerza
 */
public class CassandraManager {
    /** The cassandra service. */
    private static EmbeddedCassandraService cassandraService = null;

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LogManager.getLogManager().getLogger(MemoryUsage.class.getName());

    public static EmbeddedCassandraService getEmbeddedService() {
        return cassandraService;
    }

    /**
     * Initialize.
     */
    public static void initialize() {
        try {
            printConfig();
            if (cassandraService == null) {
                cassandraService = new EmbeddedCassandraService();
                cassandraService.start();
            }

        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not load Cassandra service" + e.getMessage(), e);
            if (cassandraService != null) {
                cassandraService = null;
            }
        }
    }

    private static void printConfig() {
        System.out.println("Cassandra embedded service. log4j.configuration = "
                        + System.getProperty("log4j.configuration"));
        System.out.println("Cassandra embedded service.cassandra.config = " + System.getProperty("cassandra.config"));
    }

}
