package org.ebayopensource.turmeric.utils.cassandra.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.ebayopensource.turmeric.utils.cassandra.model.Model;

public class ModelTestManager extends Thread {
  private final BlockingQueue<String> testModelQueue = new ArrayBlockingQueue<String>(100);
  private final CassandraTestService service;
  
  public ModelTestManager(CassandraTestService service) {
    this.service = service;
  }
  
  public void give(String userName) throws InterruptedException {
    testModelQueue.put(userName);
  }

  public String take() throws InterruptedException {
    return testModelQueue.take();
  }
  
  public Iterable<String> getAll() {
    List<String> allTestModels = new ArrayList<String>();
    testModelQueue.drainTo(allTestModels);
    return allTestModels;
  }
  
  private static final long TIME_BEFORE_NEXT_ITEM_CREATION = 500L;

  public void run() {
    int item = 0;
    try {
      while (!isInterrupted()) {
        Model  tm = new Model();
        tm.setKey("testModel" + item);
        //true / false alternate
        tm.setBooleanData((item%2) == 0 );
        tm.setIntData(item);
        tm.setLongData(Long.valueOf(item));
        tm.setStringData("anyString" + item);
        tm.setTimeData(new Date(System.currentTimeMillis()));
        
        service.createTestModel(tm);
        testModelQueue.put(tm.getKey());
        System.out.println("Created an TestModel Item:" + tm);
        item++;
        Thread.sleep(TIME_BEFORE_NEXT_ITEM_CREATION);
      }
    }
    catch (InterruptedException e) {
      System.out.println("Stopping Cassandra Test Manager");
    }
  }

}
