import java.util.concurrent.ExecutionException;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.uat1.v1.services.SayHelloRequest;
import org.ebayopensource.turmeric.uat1.v1.services.SayHelloResponse;
import org.ebayopensource.turmeric.uat1.v1.services.helloworld.gen.SharedUatHelloWorldV1Consumer;

public class HelloWorldV1Consumer extends SharedUatHelloWorldV1Consumer implements AsyncHandler<SayHelloResponse>{


	public HelloWorldV1Consumer(String clientName, String environment)
			throws ServiceException {
		super(clientName, environment);
	}

	public static void main(String[] args) throws ServiceException, InterruptedException {
		HelloWorldV1Consumer consumer = new HelloWorldV1Consumer("HelloWorldV1Consumer", "production");
		consumer.getServiceInvokerOptions().setTransportName("LOCAL");
		SayHelloRequest request = new SayHelloRequest();
		request.setHello("World");
		SayHelloResponse  res = consumer.sayHello(request);
		System.out.println(res.getHello());
		//now, in asynch mode
		request.setHello("Asynch World");
		consumer.sayHelloAsync(request, consumer);
		//Need to sleep thread to wait for the asynch message to arrive
		Thread.currentThread().sleep(5000);
	}

	@Override
	public void handleResponse(Response<SayHelloResponse> res) {
		try {
			System.out.println("receving asynch message :"+res.get().getHello());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
