package org.ebayopensource.turmeric.uat.v2.services.messageservice.consumer;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.uat.v2.services.GetMessagesRequest;
import org.ebayopensource.turmeric.uat.v2.services.GetMessagesResponse;
import org.ebayopensource.turmeric.uat.v2.services.Message;
import org.ebayopensource.turmeric.uat.v2.services.SendMessageRequest;
import org.ebayopensource.turmeric.uat.v2.services.SendMessageResponse;
import org.ebayopensource.turmeric.uat.v2.services.messageservice.gen.SharedUatMessageServiceV2Consumer;

public class UatMessageServiceV2Consumer extends
		SharedUatMessageServiceV2Consumer {

	public UatMessageServiceV2Consumer(String clientName, String environment)
			throws ServiceException {
		super(clientName, environment);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws ServiceException {
		SendMessageRequest request = new SendMessageRequest();
		Message msg = new Message();
		msg.setUserName("uat-turmeric");
		msg.setPriority(BigInteger.ONE);
		msg.setMessageContent("message sent to version 2 of the Message Service "
				+ new Date());
		request.setMessage(msg);
		UatMessageServiceV2Consumer uatMessageServiceV2Consumer = new UatMessageServiceV2Consumer(
				"UatMessageServiceV2Consumer", "production");
		uatMessageServiceV2Consumer.setAuthToken("turmeric-433-auth-token");
		
		for (int i = 0; i < 5; i++) {
			SendMessageResponse res = uatMessageServiceV2Consumer
					.sendMessage(request);
		}
		// now, i get the messages back
		GetMessagesRequest getMsgRequest = new GetMessagesRequest();
		getMsgRequest.setUserName("uat-turmeric");
		GetMessagesResponse getMsgResponse = uatMessageServiceV2Consumer
				.getMessages(getMsgRequest);
		List<Message> messages = getMsgResponse.getMessages();
		for (Message message : messages) {
			System.out
					.println("message.getUserName()=" + message.getUserName());
			System.out
					.println("message.getPriority()=" + message.getPriority());
			System.out.println("message.getMessageContent()="
					+ message.getMessageContent());
		}
		
		Map<String,String> transportHeaders = uatMessageServiceV2Consumer.getService().getResponseContext().getTransportHeaders();
		for (String key : transportHeaders.keySet()) {
			System.out.println("["+key+"] ->"+transportHeaders.get(key));
		}

	}
}
