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
import java.io.UnsupportedEncodingException;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.NotFoundException;
import org.apache.cassandra.thrift.TBinaryProtocol;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.ebayopensource.turmeric.utils.cassandra.HectorManager;
import org.junit.Before;
import org.junit.Test;

/*
 * @author jamuguerza
 */
public class CassandraManagerTest extends CassandraManager {

	@Before
	public void setup()   throws IOException{
//		CassandraServiceDataCleaner cleaner = new CassandraServiceDataCleaner();
//        cleaner.prepare();
		
  //      CassandraManager.initialize();
	}

	
	@Test
	public void testCassandraUp() {
//		HectorManager.getOrCreateCluster(clusterName, host)
	}

	
//    @Test
//    public void testInProcessCassandraServer()
//            throws UnsupportedEncodingException, InvalidRequestException,
//            UnavailableException, TimedOutException, TException,
//            NotFoundException {
//    	
//        Cassandra.Client client = getClient();
// 
//        String key_user_id = "1";
// 
//        long timestamp = System.currentTimeMillis();
//        ColumnPath cp = new ColumnPath("Standard1");
//        cp.setColumn("name".getBytes("utf-8"));
// 
//        // insert
//        client.insert("Keyspace1", key_user_id, cp, "Ran".getBytes("UTF-8"),
//                timestamp, ConsistencyLevel.ONE);
// 
//        // read
//        ColumnOrSuperColumn got = client.get("Keyspace1", key_user_id, cp,
//                ConsistencyLevel.ONE);
// 
//        // assert
//        assertNotNull("Got a null ColumnOrSuperColumn", got);
//        assertEquals("Ran", new String(got.getColumn().getValue(), "utf-8"));
//    }
 
    /**
     * Gets a connection to the localhost client
     *
     * @return
     * @throws TTransportException
     */
    private Cassandra.Client getClient() throws TTransportException {
        TTransport tr = new TSocket("localhost", 9170);
        TProtocol proto = new TBinaryProtocol(tr);
        Cassandra.Client client = new Cassandra.Client(proto);
        tr.open();
        return client;
    }

    
}
