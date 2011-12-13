
package org.ebayopensource.turmeric.uat.v2.services.messageservice.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.ebayopensource.turmeric.common.v1.types.AckValue;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.uat.v1.services.messageservice.consumer.UatMessageServiceV1Consumer;
import org.ebayopensource.turmeric.uat.v2.services.GetMessagesRequest;
import org.ebayopensource.turmeric.uat.v2.services.GetMessagesResponse;
import org.ebayopensource.turmeric.uat.v2.services.Message;
import org.ebayopensource.turmeric.uat.v2.services.SendMessageRequest;
import org.ebayopensource.turmeric.uat.v2.services.SendMessageResponse;
import org.ebayopensource.turmeric.uat.v2.services.messageservice.impl.UatMessageServiceV2;
import org.ebayopensource.turmeric.uat.v2.services.messageservice.model.MessageV2;

public class UatMessageServiceV2Impl
    implements UatMessageServiceV2
{
	private static final EntityManagerFactory jpaFactory = Persistence
	.createEntityManagerFactory("uat-messageservice-v2");



    public org.ebayopensource.turmeric.uat.v2.services.GetMessagesResponse getMessages(GetMessagesRequest param0) {
    	GetMessagesResponse resp = new GetMessagesResponse();
    	
    	
    	EntityManager em = jpaFactory.createEntityManager();
		Query query = em
				.createQuery("Select m from MessageV2 m where m.userName =:userName");
		query.setParameter("userName", param0.getUserName());
		List<MessageV2> messages = query.getResultList();
		em.close();
		for (MessageV2 msgV2 : messages) {
			resp.getMessages().add(msgV2);
		}
		List<String> messagesV1 = null;
		//now, i use the MessageServiceV1Consumer to get the messages for the same userName
		try {
			UatMessageServiceV1Consumer uatMessageServiceV1Consumer = new UatMessageServiceV1Consumer(
					"UatMessageServiceV1Consumer", "production");
			org.ebayopensource.turmeric.uat.v1.services.GetMessagesRequest getMsgRequest = new org.ebayopensource.turmeric.uat.v1.services.GetMessagesRequest();
			getMsgRequest.setUserName(param0.getUserName());
			org.ebayopensource.turmeric.uat.v1.services.GetMessagesResponse responseV1 = uatMessageServiceV1Consumer.getMessages(getMsgRequest);
			messagesV1 = responseV1.getMessages();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (String string : messagesV1) {
			resp.getMessages().add(MessageV2.createMessage("uat-turmeric" , string));
		}
		//now, i got the messages coming from the v1 service together with the ones coming from version 2
		resp.setAck(AckValue.SUCCESS);
		return resp;
        
    }

    public SendMessageResponse sendMessage(SendMessageRequest param0) {
    	SendMessageResponse resp = new SendMessageResponse();
    	Message msg = param0.getMessage();
    	MessageV2 msgV2 = new MessageV2();
    	msgV2.copyFrom(msg);//copy the values to the jpa entity
    	EntityManager em = jpaFactory.createEntityManager();
		em.getTransaction().begin();
		em.persist(msgV2);
		em.getTransaction().commit();
		em.close();
    	resp.setAck(AckValue.SUCCESS);
    	return resp;
    }

}
