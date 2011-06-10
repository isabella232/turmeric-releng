package org.ebayopensource.turmeric.uat.test;

import static org.junit.Assert.*;

import java.util.concurrent.ExecutionException;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.uat1.v1.services.SayHelloRequest;
import org.ebayopensource.turmeric.uat1.v1.services.SayHelloResponse;
import org.ebayopensource.turmeric.uat1.v1.services.helloworld.consumer.HelloWorldV1Consumer;

import org.junit.Before;
import org.junit.Test;

public class UAT432Test implements AsyncHandler<SayHelloResponse>{
	
	public UAT432Test(){
		System.out.println("instancing UAT432Test");
	}

	private HelloWorldV1Consumer consumer = null;
	private static String asynchResponse = null;

	@Before
	public void setUp() throws ServiceException {
		consumer = new HelloWorldV1Consumer("HelloWorldV1Consumer",
				"production");
		consumer.getServiceInvokerOptions().setTransportName("LOCAL");
	}

	@Test
	public void testLocalSynchCall() throws ServiceException {
		
		SayHelloRequest request = new SayHelloRequest();
		request.setHello("World");
		SayHelloResponse res = consumer.sayHello(request);
		String responseMsg = res.getHello();
		System.out.println(responseMsg);
		assertEquals("Hello, World", responseMsg);
	}
	
	@Test
	public void testLocalAsynchCall() throws ServiceException, InterruptedException {
		consumer.getServiceInvokerOptions().setTransportName("LOCAL");
		SayHelloRequest request = new SayHelloRequest();
		//now, in asynch mode
		request.setHello("Asynch World");
		consumer.sayHelloAsync(request, this);
		//Need to sleep thread to wait for the asynch message to arrive
		Thread.currentThread().sleep(5000);
		assertEquals("Hello, Asynch World", asynchResponse);
	}

	@Override
	public void handleResponse(Response<SayHelloResponse> res) {
		try {
			String responseMsg = res.get().getHello();
			System.out.println("receving asynch message:"+responseMsg);
			asynchResponse = responseMsg;
			System.out.println("asynchResponse="+asynchResponse);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
