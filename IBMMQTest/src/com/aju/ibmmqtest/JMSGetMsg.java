package com.aju.ibmmqtest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;

public class JMSGetMsg {
	private static int status = 1;

	
	private static final String HOST = "IP_ADDRESS"; 
	private static final int PORT = 1414;
	private static final String CHANNEL = "CHANNEL_NAME"; 
	private static final String QMGR = "QUEUE_MANAGER_NAME"; 
	private static final String APP_USER = "APP_USERNAME";
	private static final String APP_PASSWORD = "PASSWORD_OF_APP_USER"; 
	//private static final String QUEUE_NAME = "DEV.QUEUE.1";



	public static void main(String[] args) throws IOException {

		JMSContext context = null;
		Destination destination = null;
		JMSConsumer consumer = null;



		try {
			
			JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
			JmsConnectionFactory cf = ff.createConnectionFactory();
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	
			cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, HOST);
			cf.setIntProperty(WMQConstants.WMQ_PORT, PORT);
			cf.setStringProperty(WMQConstants.WMQ_CHANNEL, CHANNEL);
			cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
			cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, QMGR);
			cf.setStringProperty(WMQConstants.WMQ_APPLICATIONNAME, "JMSGetMsg (JMS)");
			cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
			cf.setStringProperty(WMQConstants.USERID, APP_USER);
			cf.setStringProperty(WMQConstants.PASSWORD, APP_PASSWORD);

		
			context = cf.createContext();
			System.out.println("Enter Queue name for getting messages:");
	        String des = reader.readLine();
			destination = context.createQueue("queue:///" + des);

			

			consumer = context.createConsumer(destination); 
			String receivedMessage = consumer.receiveBody(String.class, 15000); 

			System.out.println("\nReceived message:\n" + receivedMessage);
			if(receivedMessage!=null)
			recordSuccess();
			else
				System.out.println("The queue "+des+" is empty");
		} catch (JMSException jmsex) {
			recordFailure(jmsex);
		}

		System.exit(status);

	} // end main()

	/**
	 * Record this run as successful.
	 */
	private static void recordSuccess() {
		System.out.println("SUCCESS");
		status = 0;
		return;
	}

	/**
	 * Record this run as failure.
	 *
	 * @param ex
	 */
	private static void recordFailure(Exception ex) {
		if (ex != null) {
			if (ex instanceof JMSException) {
				processJMSException((JMSException) ex);
			} else {
				System.out.println(ex);
			}
		}
		System.out.println("FAILURE");
		status = -1;
		return;
	}

	/**
	 * Process a JMSException and any associated inner exceptions.
	 *
	 * @param jmsex
	 */
	private static void processJMSException(JMSException jmsex) {
		System.out.println(jmsex);
		Throwable innerException = jmsex.getLinkedException();
		if (innerException != null) {
			System.out.println("Inner exception(s):");
		}
		while (innerException != null) {
			System.out.println(innerException);
			innerException = innerException.getCause();
		}
		return;
	}


	

}
