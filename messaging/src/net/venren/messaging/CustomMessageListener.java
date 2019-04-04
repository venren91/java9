package net.venren.messaging;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

public class CustomMessageListener implements MessageListener {
    public void onMessage(Message message) {
        try {
            System.out.println(message.getJMSCorrelationID());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
