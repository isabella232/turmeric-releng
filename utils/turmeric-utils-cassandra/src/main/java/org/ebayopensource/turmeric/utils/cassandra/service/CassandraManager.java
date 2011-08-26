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
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import me.prettyprint.cassandra.testutils.EmbeddedServerHelper;

import org.apache.cassandra.service.EmbeddedCassandraService;
import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.Compression;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.SchemaDisagreementException;
import org.apache.cassandra.thrift.TBinaryProtocol;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.ebayopensource.turmeric.utils.MemoryUsage;
import org.ebayopensource.turmeric.utils.cassandra.HectorManager;

/*
 * @author jamuguerza
 */
public class CassandraManager {

	private static EmbeddedCassandraService cassandraService = null;

	private static final Logger LOGGER = LogManager.getLogManager().getLogger(
			MemoryUsage.class.getName());

	public static void initialize() {

		loadConfig();

		EmbeddedServerHelper.loadSchemaFromYaml();

		try {
			if (cassandraService == null) {
				cassandraService = new EmbeddedCassandraService();
				cassandraService.start();
			}

		} catch (IOException e) {
			LOGGER.log(Level.SEVERE,
					"Could not load Cassandra service" + e.getMessage(), e);
		}

	}

	public static void createKeyspace(final String kSpace) {
		TTransport tr = new TFramedTransport(new TSocket("localhost", 9160));
		TProtocol proto = new TBinaryProtocol(tr);
		Cassandra.Client client = new Cassandra.Client(proto);
		try {
			tr.open();

			String cql = "CREATE keyspace "
					+ kSpace
					+ "  WITH strategy_options:DC1 = '1' AND replication_factor = '1' AND strategy_class = 'SimpleStrategy'";

			client.execute_cql_query(ByteBuffer.wrap(cql.getBytes()),
					Compression.NONE);

		} catch (TTransportException e1) {
			LOGGER.log(Level.SEVERE, "Could create keyspace " + kSpace + ". "
					+ e1.getMessage(), e1);
		} catch (InvalidRequestException e) {
			LOGGER.log(Level.SEVERE, "Could create keyspace " + kSpace + ". "
					+ e.getMessage(), e);
		} catch (UnavailableException e) {
			LOGGER.log(Level.SEVERE, "Could create keyspace " + kSpace + ". "
					+ e.getMessage(), e);
		} catch (TimedOutException e) {
			LOGGER.log(Level.SEVERE, "Could create keyspace " + kSpace + ". "
					+ e.getMessage(), e);
		} catch (SchemaDisagreementException e) {
			LOGGER.log(Level.SEVERE, "Could create keyspace " + kSpace + ". "
					+ e.getMessage(), e);
		} catch (TException e) {
			LOGGER.log(Level.SEVERE, "Could create keyspace " + kSpace + ". "
					+ e.getMessage(), e);
		}
		tr.close();
	}

	private static void loadConfig() {
		System.setProperty("log4j.configuration",
				"META-INF/config/cassandra/log4j.properties");

		System.setProperty("cassandra.config",
				"META-INF/config/cassandra/cassandra.yaml");
	}

}
