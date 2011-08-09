package org.ebayopensource.turmeric.utils.cassandra;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.ColumnQuery;
import me.prettyprint.hector.api.query.Query;


public class HectorManager {

	private static Keyspace keyspace;
	private final static StringSerializer serializer = StringSerializer.get();
	
	private static Mutator mutator;
	
	public static Cluster getOrCreateCluster(){
		//retreive form properties
		//clusterName, hostIp
		return HFactory.getOrCreateCluster("TurmericCluster", "192.168.2.101:9160");
	}
	
	public static Keyspace getKeyspace(final String space){
		//retreive form properties
		//Keyspace
		if(keyspace == null){
			keyspace = HFactory.createKeyspace(space, getOrCreateCluster());
			mutator = HFactory.createMutator(keyspace, serializer);
		}
		return keyspace;
	}
	
	
	
	public static void insert(final String key, final String param, final String name, final String value){
		try {
			mutator.addInsertion(key, param,
					HFactory.createStringColumn(name, value));
			mutator.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
//	public static String get(final String key, final String column, final String columnFamily  ){
//		
//		
//		Query que =  new Query();
//		que.
//		
//		ColumnQuery q = HFactory.createColumnQuery(keyspace, serializer, serializer, serializer);
//		// set key, name, cf and execute
//		Result r = q.setKey("key1").
//		        setName(column).
//		        setColumnFamily(columnFamily).
//		        execute();
//		// read value from the result
//		HColumn c = r.get();
//		String value =  c.getValue();
//
//		
//		
//	}
//	
	
	
	
//	
//	private static final Mutator<String> rlMutator;
//	private static final Keyspace rlKeyspace ;
//	public static Cluster getOrCreateCluster(){
//		//retreive form properties
//		//clusterName, hostIp
//		return HFactory.getOrCreateCluster("TurmericCluster", "localhost:9160");
//	}
//	
//	public static Keyspace getRLKeyspace(){
//		//retreive form properties
//		//RLKeyspace
//		if(rlKeyspace == null){
//			rlKeyspace = HFactory.createKeyspace("RLKeyspace", getOrCreateCluster());
//		}
//		return rlKeyspace;
//	}
//	
//	public static Mutator<String> getRLMutator(){
//		if(rlMutator == null){
//			rlMutator = HFactory.createMutator(rlKeyspace, StringSerializer.get());
//		}
//		return rlMutator;
//	}
//	 
//	public static void insert()
//	 Keyspace keyspaceOperator = HFactory.createKeyspace("Keyspace1", cluster);
//     try {
//         Mutator<String> mutator = HFactory.createMutator(keyspaceOperator, StringSerializer.get());
//         mutator.insert("jsmith", "Standard1", HFactory.createStringColumn("first", "John"));
//         
//         ColumnQuery<String, String, String> columnQuery = HFactory.createStringColumnQuery(keyspaceOperator);
//         columnQuery.setColumnFamily("Standard1").setKey("jsmith").setName("first");
//         QueryResult<HColumn<String, String>> result = columnQuery.execute();
//         
//         System.out.println("Read HColumn from cassandra: " + result.get());
//         System.out.println("Verify on CLI with: get Keyspace1.Standard1['jsmith'] ");
//         
//     } catch (HectorException e) {
//         e.printStackTrace();
//     }
//     cluster.getConnectionManager().shutdown
//     
}
