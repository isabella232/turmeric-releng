package org.ebayopensource.turmeric.utils.cassandra;



import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;

public class demo {
	final static StringSerializer serializer = StringSerializer.get();

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Keyspace keyspace = null;
		keyspace = HectorManager.getKeyspace("Keyspace1");
		
		try {

			Mutator m = HFactory.createMutator(keyspace, serializer);
			
			m.addInsertion("testbatch", "Standard1",
					HFactory.createStringColumn("Type", "somevalue1"));
			m.addInsertion("testbatch", "Standard1",
					HFactory.createStringColumn("TypeBatch", "somevalue2"));
			m.execute();
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	

	 
}
