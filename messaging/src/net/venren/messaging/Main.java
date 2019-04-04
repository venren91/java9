package net.venren.messaging;

import javax.jms.MessageListener;
import javax.jms.TopicSubscriber;
import java.net.URI;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) throws Exception {
        AMQConsumer amqConsumer = new AMQConsumer(new URI("tcp://localhost:61616"), "app");
        TopicSubscriber ts = amqConsumer.getTopicSubscriber("*.sometopic","firstconsumer");
        MessageListener ml = new CustomMessageListener();
        ts.setMessageListener(ml);
        
    }
}
