package org.ebayopensource.turmeric.utils.cassandra;

import org.ebayopensource.turmeric.utils.cassandra.service.CassandraTestServer;


public class BootStrap {
  private static volatile boolean started;
  private static Object monitor = new Object();
  private static CassandraTestServer server;
  public static void init() {
    if (started) {
      return;
    }
    synchronized (monitor) {
      server = new CassandraTestServer();
      try {
        server.setup();
        started = true;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
}
