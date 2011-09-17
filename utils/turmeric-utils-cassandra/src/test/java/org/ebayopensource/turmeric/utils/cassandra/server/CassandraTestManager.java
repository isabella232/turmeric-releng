/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.utils.cassandra.server;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.cassandra.config.ConfigurationException;
import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.io.util.FileUtils;
import org.apache.cassandra.service.EmbeddedCassandraService;
import org.apache.thrift.transport.TTransportException;

/**
 * The Class CassandraTestManager.
 * 
 * @author jamuguerza
 */
public class CassandraTestManager {

	/** The cassandra service. */
	private static EmbeddedCassandraService cassandraService = null;

	/**
	 * Set embedded cassandra.
	 * 
	 * @throws TTransportException
	 *             the t transport exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 *             the interrupted exception
	 * @throws ConfigurationException
	 *             the configuration exception
	 */
	public static void initialize() throws TTransportException, IOException,
			InterruptedException, ConfigurationException {
		if (cassandraService == null) {
			loadConfig();

//			for (String dir : getDataDirs()) {
//				FileUtils.createDirectory(dir);
//			}

			cassandraService = new EmbeddedCassandraService();
			cassandraService.start();
		}

	}

	private static Set<String> getDataDirs() {
		Set<String> dirs = new HashSet<String>();
		for (String s : DatabaseDescriptor.getAllDataFileLocations()) {
			dirs.add(s);
		}
		return dirs;
	}

	/**
	 * Load config.
	 */
	private static void loadConfig() {
		// use particular test properties, maybe with copy method
		System.setProperty("log4j.configuration",
				"META-INF/config/cassandra/log4j.properties");

		System.setProperty("cassandra.config",
				"META-INF/config/cassandra/cassandra-test.yaml");
	}

}
