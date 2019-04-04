package net.venren.messaging;



import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQTopicSubscriber;

import javax.jms.*;
import java.net.URI;

public class AMQConsumer implements IConsumer  {

    ConnectionFactory connectionFactory = null;
    Connection connection = null;
    Session session = null;
    public AMQConsumer(URI amqUri, String clientName) throws Exception{
        connectionFactory = new ActiveMQConnectionFactory(amqUri);
        try {
            connection = connectionFactory.createConnection();
            connection.setClientID(clientName);
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        } catch (JMSException e) {
            e.printStackTrace();
            throw new Exception("Unable to connect to AMQ");
        }
    }

    public TopicSubscriber getTopicSubscriber(String topic, String consumerName) throws JMSException {
        Topic t =session.createTopic(topic);
        return session.createDurableSubscriber(t,consumerName);
    }
}
