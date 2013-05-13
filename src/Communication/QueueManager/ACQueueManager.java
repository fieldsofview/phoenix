package Communication.QueueManager;

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
import Communication.ACNetwork;
import Communication.MessageQueueProcessor;
import Communication.QueueParameters;
import Communication.messages.ACStatusMessage;
import Communication.messages.Message;
import System.Log;

/**
 * This class helps creation, send and receive from the queue using RabbitMQ or
 * any other AMQP protocol. It extends thread so that receive can be done in a
 * loop. This class handles the rabbitMQ for a given AgentController
 * 
 * Each AgentController creates a queue for itself where it receives messages
 * for running the step simulation and synchronisation. The various states it
 * can be in is listed in ACNetwork. Each queue thus created are bound to the
 * same exchange which behaves as a fan out exchange. Any message sent is
 * broadcast to all listening queues.
 * 
 * @see ACNetwork
 */
public class ACQueueManager extends QueueManager implements Serializable {

	/**
	 * For Serialisation of the class.
	 */
	private static final long serialVersionUID = 1L;
	private static ACQueueManager queueManager = null;
	// private QueueParameters queueParameters = null;
	public boolean setupQueueListener = false;
	private static final int prefetchCount = 1;

	private Connection conn;
	private Channel channel;
	private ConnectionFactory factory;
	private QueueingConsumer consumer;

	/**
	 * Create a new queue manager given the QueueParameters
	 * 
	 * @param queueParameters
	 *            the QueueParameters for the queue manager
	 * @see ACQueueManager
	 * @see QueueParameters
	 */
	private ACQueueManager(QueueParameters queueParameters) {
		this.queueParameters = queueParameters;
	}

	/**
	 * Get an instance of the QueueManager
	 * 
	 * @param queueParameters
	 *            the parameters for the QueueManager
	 * @param queueUser
	 *            the QueueUser for the QueueManager
	 * @return returns an instance of QueueManager
	 * @see ACQueueManager
	 */
	public static ACQueueManager getInstance(QueueParameters queueParameters,
			MessageQueueProcessor queueUser) {
		if (queueManager == null) {
			queueManager = new ACQueueManager(queueParameters);
		}
		return queueManager;
	}

	/**
	 * The run method adds a listener which listens to received messages in a
	 * separate thread
	 */
	@Override
	public void run() {
		// super.run();
		addQueueListener(queueParameters);
	}

	private void createConnectionAndChannel() throws Exception {
		Log.logger.info("Creating a connection and channel");
		QueueParameters hostQueueParameters = ACNetwork.queueParameters;
		factory = new ConnectionFactory();
		factory.setHost(GlobalData.Constants.localHost);
		factory.setPort(Integer.parseInt(hostQueueParameters.port));
		factory.setUsername(hostQueueParameters.username);
		factory.setPassword(hostQueueParameters.password);
		factory.setVirtualHost(hostQueueParameters.virtualHost);
		Log.logger.info(hostQueueParameters.toString());
		// factory.setRequestedHeartbeat(0);
		conn = factory.newConnection();
		channel = conn.createChannel();
		channel.basicQos(prefetchCount);
		channel.exchangeDeclare(hostQueueParameters.exchange, "fanout");
		channel.queueDeclare(hostQueueParameters.queueName, false, false, true,
				null);
		channel.queueBind(hostQueueParameters.queueName,
				hostQueueParameters.exchange, hostQueueParameters.routingKey);
		this.setupQueueListener = true;
		Log.logger.info("Finished creating connection and channel");
	}

	/**
	 * This method is called during AgentController shutdown for stopping the
	 * messaging and running cleanup on the message queues and channels.
	 * 
	 * @throws IOException
	 */
	public void exitMessaging() throws IOException {
		channel.close();
		conn.close();
	}

	/**
	 * This method adds a new queue listener given the queue parameters. The
	 * listener waits for a message to arrive and calls the appropriate method
	 * for processing, after it arrives
	 * 
	 * @param queueParameters
	 *            the QueueParameters for listening
	 * @see QueueParameters
	 */
	public Message addQueueListener(QueueParameters queueParameters) {
		Log.logger.info("Adding queue listener");
		ACStatusMessage message = null;
		try {
			createConnectionAndChannel();
			boolean noAck = false;
			consumer = new QueueingConsumer(channel);
			channel.basicConsume(queueParameters.queueName, noAck, consumer);

			while (!noAck) {
				QueueingConsumer.Delivery delivery;
				try {
					delivery = consumer.nextDelivery();
				} catch (InterruptedException ie) {
					continue;
				}
				InputStream inputStream = new ByteArrayInputStream(
						delivery.getBody());
				ObjectInputStream input = new ObjectInputStream(inputStream);
				message = (ACStatusMessage) input.readObject();
				inputStream.close();
				input.close();
				Log.logger.info("Received Message");
				// messageProcessor.processMessage(message);
				// messageProcessor.processMessage.run();
				// Utilities.Log.logger.info("QM: I am listening for messages in the while loop 10");
				channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
				// Utilities.Log.logger.info("QM: I am listening for messages in the while loop 11");
			}
			Log.logger.error("Finished Adding queue listener");

		} catch (ClassNotFoundException ex) {
			Log.logger.error("" + ACQueueManager.class.getName());
			ex.printStackTrace();
			System.exit(0);
		} catch (IOException ex) {
			Log.logger.error("" + ACQueueManager.class.getName());
			ex.printStackTrace();
			System.exit(0);
		} catch (Exception ex) {
			Log.logger.error(ACQueueManager.class.getName());
			ex.printStackTrace();
			System.exit(0);
		}
		return message;
	}

	/**
	 * This method is called when a message has to broadcast to all
	 * AgentControllers The method makes use of the exchange to which all the
	 * AgentControllers have subscribed. Only the status is used in the Message
	 * object to send the values.
	 * 
	 * @param message
	 *            the message that has to be sent
	 * @return true if the message was successfully sent
	 * @see ACStatusMessage
	 */
	public boolean send(ACStatusMessage message) {
		// Utilities.Log.logger.info("RabbitMQ Send Method");
		while (setupQueueListener == false) {
			Log.logger.info("Waiting to send");
		}
		ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
		try {
			// Utilities.Log.logger.info("Contents of Host Channel Map: " +
			// hostChannelMap.toString());
			ObjectOutputStream outputWriter = new ObjectOutputStream(
					outputBuffer);
			outputWriter.writeObject(message);
			outputWriter.close(); // write to buffer and flush;
			byte[] messageBodyBytes = outputBuffer.toByteArray();
			channel.basicPublish(ACNetwork.queueParameters.exchange, "", null,
					messageBodyBytes);
			outputBuffer.close();
			return true;
		} catch (IOException ex) {
			Log.logger.info(ACQueueManager.class.getName());
			Log.logger.info("Error Sending Message" + ex.getMessage());
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * This method checks if two QueueManager objects are same
	 * 
	 * @param obj
	 *            an object of QueueManager type
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
		final ACQueueManager other = (ACQueueManager) obj;
		if (this.queueParameters != other.queueParameters
				&& (this.queueParameters == null || !this.queueParameters
						.equals(other.queueParameters))) {
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
		hash = 79
				* hash
				+ (this.queueParameters != null ? this.queueParameters
						.hashCode() : 0);
		return hash;
	}
}
