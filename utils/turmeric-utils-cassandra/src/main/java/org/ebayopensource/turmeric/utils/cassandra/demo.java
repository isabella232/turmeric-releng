package org.ebayopensource.turmeric.utils.cassandra;

import java.nio.ByteBuffer;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;

import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.thrift.TException;

public class demo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		System.out.println("grab cluster");
//		Cluster cluster = HectorManager.getOrCreateCluster();
//		
//		// Choose a keyspace
//		System.out.println("adding keyspace");
//		Keyspace keyspace = HFactory.createKeyspace("turmeric", cluster);
//
//		// create an string extractor. I'll explain that later
//		StringExtractor se = StringExtractor.get();
//		// insert value
//		Mutator m = HFactory.createMutator(keyspace);
//		m.insert("key1", "ColumnFamily1", createColumn("column1", "value1", StringSerializer.get(), StringSerializer.get()));
//		 
//		// Now read a value
//		// Create a query
//		ColumnQuery q = HFactory.createColumnQuery(keyspace, se, se);
//		// set key, name, cf and execute
//		Result&gt; r = q.setKey("key1").
//		        setName("column1").
//		        setColumnFamily("ColumnFamily1").
//		        execute();
//		// read value from the result
//		HColumn c = r.get();
//		String value =  c.getValue();
//		System.out.println(value);
//		
		

//		try {
//			client.set_keyspace("turmeric");
//		
//		
//		} catch (InvalidRequestException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (TException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		
//		// define column parent
//        ColumnParent parent = new ColumnParent("User");
//
//        // define row id
//        ByteBuffer rowid = ByteBuffer.wrap("100".getBytes());
//
//        // define column to add
//        Column description = new Column();
//        description.setName("description".getBytes());
//        description.setValue("I’m a nice guy".getBytes());
//        description.setTimestamp(System.currentTimeMillis());
//
//        // define consistency level
//        ConsistencyLevel consistencyLevel = ConsistencyLevel.ONE;
//
//        // execute insert
//        try {
//			client.insert(rowid, parent, description, consistencyLevel);
//		
//        
//        } catch (InvalidRequestException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UnavailableException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (TimedOutException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (TException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
	}

}
