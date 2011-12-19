package org.ebayopensource.turmeric.uat.v1.services.messageservice.consumer;

import java.util.Date;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.uat.v1.services.GetMessagesRequest;
import org.ebayopensource.turmeric.uat.v1.services.GetMessagesResponse;
import org.ebayopensource.turmeric.uat.v1.services.SendMessageRequest;
import org.ebayopensource.turmeric.uat.v1.services.SendMessageResponse;
import org.ebayopensource.turmeric.uat.v1.services.messageservice.gen.SharedUatMessageServiceV1Consumer;

public class UatMessageServiceV1Consumer extends
		SharedUatMessageServiceV1Consumer {

	public UatMessageServiceV1Consumer(String clientName, String environment)
			throws ServiceException {
		super(clientName, environment);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws ServiceException {
		SendMessageRequest request = new SendMessageRequest();
		request.setUserName("uat-turmeric");
		request.setMessage("This message was created in " + new Date());
		UatMessageServiceV1Consumer uatMessageServiceV1Consumer = new UatMessageServiceV1Consumer(
				"UatMessageServiceV1Consumer", "production");
		uatMessageServiceV1Consumer.setAuthToken("uat-turmeric-auth-token");
		for (int i = 0; i < 5; i++) {
			SendMessageResponse res = uatMessageServiceV1Consumer
					.sendMessage(request);
		}
		
		// now, i get the messages back
		GetMessagesRequest getMsgRequest = new GetMessagesRequest();
		getMsgRequest.setUserName("uat-turmeric");
		GetMessagesResponse getMsgResponse = uatMessageServiceV1Consumer
				.getMessages(getMsgRequest);
		System.out.println("getMsgResponse.getMessages="
				+ getMsgResponse.getMessages());
		
		Map<String,String> transportHeaders = uatMessageServiceV1Consumer.getService().getResponseContext().getTransportHeaders();
		for (String key : transportHeaders.keySet()) {
			System.out.println("["+key+"] ->"+transportHeaders.get(key));
		}

	}
}
