import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.net.URI;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private String topic = null;
    private URI activeMQUri = null;
    private ExecutorService executors;

    public Main(String topic, String amqUri){
        this.topic = topic;
        this.activeMQUri = URI.create(amqUri);
        executors = Executors.newSingleThreadExecutor();
    }

    public static void main(String[] args) {
        if(validateArgs(args)){
            new Main(args[0],args[1]).startConsuming();
        }
    }

    public void startConsuming(){
        Runnable consumerThread = () -> {
            ConnectionFactory csFactory = new ActiveMQConnectionFactory(activeMQUri);
            try{
                Connection connection = csFactory.createConnection();
                connection.setClientID("random client");
                connection.start();
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Topic t =session.createTopic(topic);
                MessageConsumer consumer =  session.createDurableSubscriber(t,"random consumer");
                consumer.setMessageListener((ml) -> {
                    System.out.println(ml.toString());
                });
            } catch (JMSException e) {
                e.printStackTrace();
            }
        };

        executors.submit(consumerThread);

    }

    private static boolean validateArgs(String[] args) {
        if (args.length < 2) {
            System.out.println("Invalid input. Provide two arguments 1) Topic to consumer 2) Uri of queue");
            return false;
        }

        if(!Objects.isNull(args[0]) &&  !Objects.isNull(args[1])){
            try {
                URI amqUri = URI.create(args[1]);
                amqUri = null;
            }catch (IllegalArgumentException ile){
                System.out.println("Invalid URI");
                return false;
            }
        }else{
            System.out.println("Invalid input");
            return false;
        }

        return true;
    }
}
