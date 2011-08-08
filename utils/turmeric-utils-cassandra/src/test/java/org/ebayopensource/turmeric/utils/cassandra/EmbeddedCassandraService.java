package org.ebayopensource.turmeric.utils.cassandra;

 
import java.io.IOException;

import org.apache.cassandra.thrift.CassandraDaemon;
import org.apache.thrift.transport.TTransportException;
 
/**
 * An embedded, in-memory cassandra storage service that listens
 * on the thrift interface as configured in storage-conf.xml
 * This kind of service is useful when running unit tests of
 * services using cassandra for example.
 *
 */
public class EmbeddedCassandraService implements Runnable
{
 
    CassandraDaemon cassandraDaemon;
 
    public void init() throws TTransportException, IOException
    {
        cassandraDaemon = new CassandraDaemon();
        cassandraDaemon.init(null);
    }
 
    public void run()
    {
        cassandraDaemon.start();
    }
}
