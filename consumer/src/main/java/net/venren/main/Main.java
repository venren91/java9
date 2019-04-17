package net.venren.main;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.net.URI;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class Main {
    private String topic = null;
    private URI activeMQUri = null;
    private Integer threadCount = 0;
    private ExecutorService executors;

    public Main(String topic, String amqUri, String numberOfThreads){
        this.topic = topic;
        this.activeMQUri = URI.create(amqUri);
        threadCount = Integer.parseInt(numberOfThreads);
        executors = Executors.newFixedThreadPool(threadCount);
    }

    public static void main(String[] args) {
        if(validateArgs(args)){
            new Main(args[0],args[1], args[2]).startConsuming();
        }
    }

    public void startConsuming(){
        IntStream.range(0,threadCount).forEachOrdered(i -> executors.submit(generateConsumer(i)));
    }

    private Runnable generateConsumer(int i) {
        return () -> {
                System.out.println("Starting thread: " + i + " to consume");
                ConnectionFactory csFactory = new ActiveMQConnectionFactory(activeMQUri);
                try{
                    Connection connection = csFactory.createConnection();
                    connection.setClientID("random_consumer_" + i);
                    connection.start();
                    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                    Topic t =session.createTopic(topic);
                    MessageConsumer consumer =  session.createDurableSubscriber(t,"random consumer");
                    consumer.setMessageListener((ml) -> {
                        System.out.println("Message received in consumer " + i);
                        System.out.println(ml.toString());
                    });
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            };
    }

    private static boolean validateArgs(String[] args) {
        if (args.length < 3) {
            System.out.println("Invalid input. Provide three arguments \n 1) Topic to consumer \n 2) Uri of queue \n 3) number of consumer threads");
            return false;
        }

        if(!Objects.isNull(args[0]) &&  !Objects.isNull(args[1])){
            try {
                URI.create(args[1]);
            }catch (IllegalArgumentException ile){
                System.out.println("Invalid URI");
                return false;
            }
        }else{
            System.out.println("Invalid input");
            return false;
        }

        try{
            Integer.parseInt(args[2]);
        }catch (NumberFormatException nfe){
            System.out.println("Invalid input number of thread argument.");
            return false;
        }

        return true;
    }
}
