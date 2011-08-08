package org.ebayopensource.turmeric.utils.cassandra;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;


public class HectorManager {

	private static Keyspace keyspace;
	
	public static Cluster getOrCreateCluster(){
		//retreive form properties
		//clusterName, hostIp
		return HFactory.getOrCreateCluster("TurmericCluster", "192.168.0.7:9160");
	}
	
	public static Keyspace getKeyspace(){
		//retreive form properties
		//Keyspace
		if(keyspace == null){
			keyspace = HFactory.createKeyspace("turmeric_rate_limiter", getOrCreateCluster());
		}
		return keyspace;
	}
	
	
	
	
	
	
	
	
	
	
	
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
