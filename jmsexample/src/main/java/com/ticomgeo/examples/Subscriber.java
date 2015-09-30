package com.ticomgeo.examples;

import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.integration.transports.netty.NettyConnectorFactory;
import org.hornetq.integration.transports.netty.TransportConstants;

public class Subscriber {

	 

	public static void main(String[] args) throws Exception {
		// Start the server
		if (args.length!=3) usuage();
        String host=args[0];
        String portStr=args[1];
        String topicStr=args[2];
        int port=Integer.valueOf(portStr);

		TopicConnection connection = null;
		try {
			// Step 1. Directly instantiate thetopic object.
            Topic topic=HornetQJMSClient.createTopic(topicStr);
			// Step 2. Instantiate the TransportConfiguration object which
			// contains the knowledge of what transport to use,
			// The server port etc.
			Map<String, Object> connectionParams = new HashMap<String, Object>();
			connectionParams.put(TransportConstants.PORT_PROP_NAME, port);
			connectionParams.put(TransportConstants.HOST_PROP_NAME, host);
			TransportConfiguration transportConfiguration = new TransportConfiguration(
					NettyConnectorFactory.class.getName(), connectionParams);

			// Step 3 Directly instantiate the JMS ConnectionFactory object
			// using that TransportConfiguration
			TopicConnectionFactory cf = (TopicConnectionFactory)HornetQJMSClient
					.createConnectionFactory(transportConfiguration);

			// Step 4.Create a JMS Connection
			connection = cf.createTopicConnection();

			// Step 5. Create a JMS Session
			TopicSession session = connection.createTopicSession(false,
					Session.AUTO_ACKNOWLEDGE);

			// Step 9. Create a JMS Message Consumer
			// this is the topic susbscriber for async delivery. Its argument is an instance of 
			// MessageListener who's onMessage method is called with the message when it is received
			TopicSubscriber recv = session.createSubscriber(topic);
			 recv.setMessageListener(new DoSomeWork());
			// Step 10. Start the Connection
			connection.start();
			System.out.println("Subscriber listeing on topic "+topicStr+ " at "+host+":"+port);
            Object lock=new Object();
			 synchronized (lock){
                 try {
                     lock.wait();
                 } catch (InterruptedException e) {
                     Thread.currentThread().interrupt();
                     return;
                 }
             }
			
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	private static void usuage() {
		System.out.println("usage: <cmdName> host port topic");
				
		
	}
	
}
	class DoSomeWork implements MessageListener{

		public void onMessage(Message inMsg)  {
			TextMessage itm=(TextMessage)inMsg;
			try {
				System.out.println("Subscriber received "+itm.getText());
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
