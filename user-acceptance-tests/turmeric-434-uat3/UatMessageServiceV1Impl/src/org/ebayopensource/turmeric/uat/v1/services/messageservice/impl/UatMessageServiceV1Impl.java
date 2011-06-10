package org.ebayopensource.turmeric.uat.v1.services.messageservice.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.ebayopensource.turmeric.common.v1.types.AckValue;
import org.ebayopensource.turmeric.uat.v1.services.GetMessagesRequest;
import org.ebayopensource.turmeric.uat.v1.services.GetMessagesResponse;
import org.ebayopensource.turmeric.uat.v1.services.GetVersionRequest;
import org.ebayopensource.turmeric.uat.v1.services.GetVersionResponse;
import org.ebayopensource.turmeric.uat.v1.services.SendMessageRequest;
import org.ebayopensource.turmeric.uat.v1.services.SendMessageResponse;
import org.ebayopensource.turmeric.uat.v1.services.messageservice.impl.UatMessageServiceV1;
import org.ebayopensource.turmeric.uat.v1.services.messageservice.model.Message;

public class UatMessageServiceV1Impl implements UatMessageServiceV1 {
	private static final EntityManagerFactory jpaFactory = Persistence
			.createEntityManagerFactory("uat-messageservice-v1");

	@Override
	public GetVersionResponse getVersion(GetVersionRequest getVersionRequest) {
		GetVersionResponse resp = new GetVersionResponse();
		resp.setVersion("1.0.0");
		return resp;
	}

	@Override
	public SendMessageResponse sendMessage(SendMessageRequest sendMessageRequest) {
		SendMessageResponse response = new SendMessageResponse();
		Message msg = new Message();
		msg.setUserName(sendMessageRequest.getUserName());
		msg.setMessageContent(sendMessageRequest.getMessage());
		EntityManager em = jpaFactory.createEntityManager();
		em.getTransaction().begin();
		em.persist(msg);
		em.getTransaction().commit();
		em.close();
		response.setAck(AckValue.SUCCESS);
		return response;
	}

	@Override
	public GetMessagesResponse getMessages(GetMessagesRequest getMessagesRequest) {
		GetMessagesResponse response = new GetMessagesResponse();
		EntityManager em = jpaFactory.createEntityManager();
		Query query = em
				.createQuery("Select m from Message m where m.userName =:userName");
		query.setParameter("userName", getMessagesRequest.getUserName());
		List<Message> messages = query.getResultList();
		for (Message message : messages) {
			response.getMessages().add(message.getMessageContent());
		}
		em.close();
		response.setAck(AckValue.SUCCESS);
		return response;
	}

}
