package org.ebayopensource.turmeric.utils.cassandra.dao;

import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;

import org.ebayopensource.turmeric.utils.cassandra.BootStrap;
import org.ebayopensource.turmeric.utils.cassandra.service.CassandraTestServer;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;


public abstract class AbstractDao {
//TODO get from properties
protected static final String TURMERIC_TEST_CLUSTER = "TestCluster";
  private static final String KEY_SPACE = "TestCassandra";
  protected static final String HOST = "127.0.1.10:9160";
  
  private Cluster cluster;
  protected Keyspace keySpace;
  private static CassandraTestServer server;

  @BeforeClass
  public static void beforeClass() throws Exception {
    BootStrap.init();
  }

  @AfterClass
  public static void afterClass() throws Exception {
    if (server != null) {
      server.teardown();
    }
  }

  @Before
  public void before() {
    cluster = HFactory.getOrCreateCluster(TURMERIC_TEST_CLUSTER, HOST);
    keySpace = HFactory.createKeyspace(KEY_SPACE, cluster);
  }

}
