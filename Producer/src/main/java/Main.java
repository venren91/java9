import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;

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
    private Integer messagePerProducer = 0;

    public Main(String topic, String amqUri, String numberOfThreads, String messagesPerThread){
        this.topic = topic;
        this.activeMQUri = URI.create(amqUri);
        threadCount = Integer.parseInt(numberOfThreads);
        messagePerProducer = Integer.parseInt(messagesPerThread);
        executors = Executors.newFixedThreadPool(threadCount);
    }

    public static void main(String[] args) {
        if(validateArgs(args)){
            new Main(args[0],args[1], args[2], args[3]).startProducing();
        }
    }

    public void startProducing(){
        IntStream.range(0,threadCount).forEachOrdered(i -> executors.submit(generateProducer(i)));
    }

    private Runnable generateProducer(int i) {
        return () -> {
            System.out.println("Starting thread: " + i + " to produce");
            ConnectionFactory csFactory = new ActiveMQConnectionFactory(activeMQUri);
            try{
                Connection connection = csFactory.createConnection();
                connection.setClientID("random_producer_" + i);
                connection.start();
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                MessageProducer producer =  session.createProducer(new ActiveMQTopic(topic));
                TextMessage message = session.createTextMessage();
                IntStream.range(0,messagePerProducer).forEach(in ->{
                    try {
                        message.setText("Message " + in + " from producer " + i);
                        producer.send(message);
                    } catch (JMSException e) {
                    e.printStackTrace();
                    }
                } );
            } catch (JMSException e) {
                e.printStackTrace();
            }
        };
    }

    private static boolean validateArgs(String[] args) {
        if (args.length < 4) {
            System.out.println("Invalid input. Provide three arguments \n 1) Topic for producer \n 2) Uri of queue \n 3) number of producer threads 4) Number of message per producer thread");
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