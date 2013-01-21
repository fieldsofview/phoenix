/** Create and maintain RabbitMQ Queues
*/
package Communication;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import System.Log;
import org.apache.log4j.Level;

/**
 * A static class to manage send and receive from the queue. It extends thread
 * so that receive can be done in a loop. This class handles the rabbitMQ
 * message queue for the given CTA
 */
public class QueueManager extends Thread implements Serializable {

    /**
	 * 
	 */
	//TODO: Check how to implement serialisation in java now.
	private static final long serialVersionUID = 999654678404420891L;
	/**
     * The rabbitMQ queue manager object
     */
    private static QueueManager queueManager = null;
    /**
     * The parameters for the queues to be created
     */
    private QueueParameters queueParameters = null;
    /**
     * The rabbitMQ queue receiver object
     */
    private MessageQueueProcessor queueUser = null;
    /**
     * A hash map of the list of rabbitMQ channels corresponding the host name
     * Each host name is a host running CTA and these channels are used to send
     * messages to them. This map represents a directory / address map
     */
    private HashMap<String, Channel> hostChannelMap = null;
    /**
     * Flag to check to set up a new queue listener
     */
    public boolean setupQueueListener = false;

    /**
     * Create a new queue manager given the QueueParameters
     *
     * @param queueParameters the QueueParameters for the queue manager
     * @see QueueManager
     * @see QueueParameters
     */
    private QueueManager(QueueParameters queueParameters) {
        this.queueParameters = queueParameters;
    }

    /**
     * Get an instance of the QueueManager
     *
     * @param queueParameters the parameters for the QueueManager
     * @param queueUser the QueueUser for the QueueManager
     * @return returns an instance of QueueManager
     * @see QueueManager
     */
    public static QueueManager getInstance(QueueParameters queueParameters, MessageQueueProcessor queueUser) {
        if (queueManager == null) {
            queueManager = new QueueManager(queueParameters);
        }
        queueManager.queueUser = queueUser;
        return queueManager;
    }

    /**
     * The run method adds a listener which listens to received messages in a
     * separate thread
     */
    @Override
    public void run() {
        addQueueListener(queueParameters);
    }

    /**
     * This method creates a connection to the rabbitMQ server on each of the
     * given hosts and opens channels for communication. These channels are used
     * for communication between between the given CTA and import
     * System.TrafficLightCTA; the rest of the CTAs.
     *
     * @throws Exception
     */
    private void createConnectionAndChannel() throws Exception {

        Log.logger.info("Creating a connection and channel");

        List<String> hosts = ACNetwork.agentControllerhostList;
        Map<String, QueueParameters> hostQueueParamMap = ACNetwork.hostMessageQueueLookup;

        Log.logger.info("Size of hosts: " + hosts.size() + " and host queue map: " + hostQueueParamMap.size());

        hostChannelMap = new HashMap<String, Channel>();

        for (int i = 0; i < hosts.size(); i++) {
            String host = hosts.get(i);
            QueueParameters hostQueueParameters = hostQueueParamMap.get(host);
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(host);
            factory.setPort(13000);
            factory.setUsername(hostQueueParameters.username);
            factory.setPassword(hostQueueParameters.password);
            factory.setVirtualHost(hostQueueParameters.virtualHost);
            factory.setRequestedHeartbeat(0);
            Connection conn = factory.newConnection();
            Channel channel = conn.createChannel();
            channel.exchangeDeclare(hostQueueParameters.exchange, "direct");
            channel.queueDeclare(hostQueueParameters.queueName, false, false, true, null);
            channel.queueBind(hostQueueParameters.queueName, hostQueueParameters.exchange, hostQueueParameters.routingKey);

            hostChannelMap.put(host, channel);
        }
        setupQueueListener = true;
        Log.logger.info("Finished creating connection and channel");
        Log.logger.info("Contents of Host Channel Map: " + hostChannelMap.toString());
    }

    /**
     * This method is called during CTA shutdown for stopping the messaging and
     * running cleanup on the message queues and channels
     *
     * @throws IOException
     */
    public void exitMessaging() throws IOException {
        List<String> hosts = ACNetwork.agentControllerhostList;
        for (int i = 0; i < hosts.size(); i++) {
            String host = hosts.get(i);
            Channel channel = hostChannelMap.get(host);
            Connection conn = channel.getConnection();
            channel.close();
            conn.close();
        }
    }

    /**
     * This method adds a new queue listener given the queue parameters. The
     * listener waits for a message to arrive and calls the appropriate method
     * for processing, after it arrives
     *
     * @param queueParameters the QueueParameters for listening
     * @see QueueParameters
     */
    private void addQueueListener(QueueParameters queueParameters) {

        Log.logger.info("Adding queue listener");

        try {
            createConnectionAndChannel();

            Log.logger.info("started listening to input queue");

            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(GlobalData.Constants.localHost);
            factory.setPort(13000);
            factory.setUsername(queueParameters.username);
            factory.setPassword(queueParameters.password);
            factory.setVirtualHost(queueParameters.virtualHost);
            factory.setRequestedHeartbeat(0);
            Connection conn = factory.newConnection();
            Channel channel = conn.createChannel();
            channel.exchangeDeclare(queueParameters.exchange, "direct");
            channel.queueDeclare(queueParameters.queueName, false, false, true, null);
            channel.queueBind(queueParameters.queueName, queueParameters.exchange, queueParameters.routingKey);

            //byte[] messageBodyBytes = "Hello, worldoooo!".getBytes();
            //channel.basicPublish(queueParameters.exchange, queueParameters.routingKey, null, messageBodyBytes);


            boolean noAck = false;
            QueueingConsumer consumer = new QueueingConsumer(channel);
            channel.basicConsume(queueParameters.queueName, noAck, consumer);

            //AMQP.Queue.Purge(1, queueParameters.queueName, true);

            while (!noAck) {
//                Utilities.Log.logger.info("QM: I am listening for messages in the while loop 1");
                QueueingConsumer.Delivery delivery;
//                Utilities.Log.logger.info("QM: I am listening for messages in the while loop 2");
                try {
//                    Utilities.Log.logger.info("QM: I am listening for messages in the while loop 3");
                    delivery = consumer.nextDelivery();
//                    Utilities.Log.logger.info("QM: I am listening for messages in the while loop 4");
                } catch (InterruptedException ie) {
//                    Utilities.Log.logger.info("QM: I am listening for messages in the while loop 5");
                    continue;

                }

//                Utilities.Log.logger.info("QM: I am listening for messages in the while loop 6");
                //String message = new String(delivery.getBody());
                InputStream inputStream = new ByteArrayInputStream(delivery.getBody());
                ObjectInputStream input = new ObjectInputStream(inputStream);
                Message message = (Message) input.readObject();
//                Utilities.Log.logger.info("QM: I am listening for messages in the while loop 7");
                inputStream.close();
//                Utilities.Log.logger.info("QM: I am listening for messages in the while loop 8");
                input.close();
//                Utilities.Log.logger.info("QM: I am listening for messages in the while loop 9");
                //System.out.println("Received Message" + message);
                Log.logger.info("Received Message");

                queueUser.processMessage.message = message;
                queueUser.processMessage.run();
//                Utilities.Log.logger.info("QM: I am listening for messages in the while loop 10");
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
//                Utilities.Log.logger.info("QM: I am listening for messages in the while loop 11");
            }
            Log.logger.info("Finished Adding queue listener");

        } catch (ClassNotFoundException ex) {
            Log.logger.info("" + QueueManager.class.getName() + Level.FATAL);
        } catch (IOException ex) {
        	Log.logger.info("" + QueueManager.class.getName() + Level.FATAL);
        } catch (Exception ex) {
        	Log.logger.info(QueueManager.class.getName() + Level.FATAL);
        }
    }

    /**
     * This method is called when a message has to be sent form one CTA to
     * another. The method makes use of the already open channels between the
     * CTAs to send its messages.
     *
     * @param host the destination host name
     * @param message the message that has to be sent
     * @return true if the message was successfully sent
     * @see Message
     */
    public boolean send(String host, Message message) {
        //Utilities.Log.logger.info("RabbitMQ Send Method");

        while (setupQueueListener == false) {
            Log.logger.info("Waiting to send");
        }

        QueueParameters hostQueueParameters = ACNetwork.hostMessageQueueLookup.get(host);

        ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();

        //System.out.println(" send 1");

        try {

            //Utilities.Log.logger.info("Contents of Host Channel Map: " + hostChannelMap.toString());

            Channel channel = hostChannelMap.get(host);

            ObjectOutputStream outputWriter = new ObjectOutputStream(outputBuffer);
            outputWriter.writeObject(message);
            outputWriter.close(); //write to buffer and flush;

            byte[] messageBodyBytes = outputBuffer.toByteArray();
            channel.basicPublish(hostQueueParameters.exchange, hostQueueParameters.routingKey, null, messageBodyBytes);
            outputBuffer.close();
            return true;

        } catch (IOException ex) {
            Log.logger.info(QueueManager.class.getName() + Level.FATAL);
            Log.logger.info("Error Sending Message" + ex.getMessage());
            return false;
        }
    }

    /**
     * This method checks if two QueueManager objects are same
     *
     * @param obj an object of QueueManager type
     * @return true if the objects are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final QueueManager other = (QueueManager) obj;
        if (this.queueParameters != other.queueParameters && (this.queueParameters == null || !this.queueParameters.equals(other.queueParameters))) {
            return false;
        }
        return true;
    }

    /**
     * Returns the hash code of the QueueManager object
     *
     * @return the hash code of the QueueManager object
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.queueParameters != null ? this.queueParameters.hashCode() : 0);
        return hash;
    }

    /**
     * This is a Debug Main method used to ping different servers
     *
     * @param args UNSUED
     */
//    public static void main(String[] args) {
//        QueueParameters queueParameters = new QueueParameters("queue1", "guest", "guest", "/", "77", "exchangeName", "routingkey");
//        ACNetwork.hostQueueMap.put("192.168.0.124", queueParameters);
//
//        PeopleCTA peopleCTA = new PeopleCTA();
//
//        QueueManager qm = QueueManager.getInstance(queueParameters, peopleCTA);
//        //qm.send("192.168.0.124", "192.168.0.124 - gud morng");
//    }
}
