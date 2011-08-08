package org.ebayopensource.turmeric.utils.cassandra;

import java.io.Console;


import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

public class CassandraManager {

	private static final TTransport transport = new TSocket("192.168.0.7", 9160); 
	 private static final TProtocol protocol = new TBinaryProtocol(transport); 
	 private static Cassandra.Client client;  
     
     public static Cassandra.Client getClient() {
    	 if(!transport.isOpen()){
    		 try {
    			 
				transport.open();
			
    		 } catch (TTransportException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
    	 }
    	 
    	 if (client == null){
        	 client = new Cassandra.Client(protocol);
         }
         return client;
     }

     public static void close(){
    	// release resources
         try {
			transport.flush();
		} catch (TTransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
         transport.close();
     }
   
}
