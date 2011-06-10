package org.ebayopensource.turmeric.uat.v2.services.messageservice.model;

import java.math.BigInteger;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.ebayopensource.turmeric.uat.v2.services.Message;

@Entity
@Table(name="message_v2")
public class MessageV2 extends Message {

	
	private Long id;

	@GeneratedValue(strategy=GenerationType.AUTO)
	@Id
	public Long getId() {
		return id;
	}
	
	
	
	public void setId(Long id) {
		this.id = id;
	}



	public void copyFrom(Message msg){
		this.setMessageContent(msg.getMessageContent());
		this.setPriority(msg.getPriority());
		this.setUserName(msg.getUserName());
	}
	
	public void setUserName(String value) {
		// TODO Auto-generated method stub
		super.setUserName(value);
	}

	
	public void setMessageContent(String value) {
		// TODO Auto-generated method stub
		super.setMessageContent(value);
	}

	
	public void setPriority(BigInteger value) {
		// TODO Auto-generated method stub
		super.setPriority(value);
	}


	@Override
	@Basic
	public String getUserName() {
		// TODO Auto-generated method stub
		return super.getUserName();
	}


	@Override
	@Basic
	public String getMessageContent() {
		// TODO Auto-generated method stub
		return super.getMessageContent();
	}


	@Override
	@Basic
	public BigInteger getPriority() {
		// TODO Auto-generated method stub
		return super.getPriority();
	}



	public static MessageV2 createMessage(String userName, String mesgContent) {
		MessageV2 result = new MessageV2();
		result.setUserName(userName);
		result.setMessageContent(mesgContent);
		result.setPriority(BigInteger.ZERO);
		return result;
	}
	
	
	
	
}
