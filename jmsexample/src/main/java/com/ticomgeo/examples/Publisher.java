package com.ticomgeo.examples;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.integration.transports.netty.NettyConnectorFactory;
import org.hornetq.integration.transports.netty.TransportConstants;

public class Publisher {
	
	
	public static void main(String[] args) throws Exception {
		// Start the server
		if (args.length!=4) usuage();
        String host=args[0];
        String portStr=args[1];
        String topicStr=args[2];
        String msgText=args[3];
        int port=Integer.valueOf(portStr);

		TopicConnection connection = null;
	 
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

			// Step 6. Create a JMS Message Producer this is to generate the messages for test
	        TopicPublisher send = session.createPublisher(topic);     
           
			// Step 7. Create a Text Message

			 TextMessage tm = session.createTextMessage(msgText);
			 		
			// Step 8. Send the Message

			 send.publish(tm);
			 System.out.println( " - published "+tm.getText());
	
			
		
	}

	private static void usuage() {
		System.out.println("usage: <cmdName> host port topic msg");
				
		
	}
	

}
