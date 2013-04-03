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
import Communication.ACNetwork;
import Communication.QueueParameters;
import Communication.messageprocessor.IMessageProcessor;
import Communication.messages.ACStatusMessage;
import Communication.messages.Message;
import System.Log;

/**
 * This class helps creation, send and receive from the queue using RabbitMQ or
 * any other AMQP protocol. It extends thread so that receive can be done in a
 * loop. This class handles the rabbitMQ for a given AgentController
 * 
 * Each AgentController creates a queue for itself where it receives messages
 * for running the step simulation and synchronization. The various states it
 * can be in is listed in ACNetwork. Each queue thus created are bound to the
 * same exchange which behaves as a fan out exchange. Any message sent is
 * broadcast to all listening queues.
 * 
 * @see ACNetwork
 */
public class ACQueueManagement extends QueueManager {

	private static ACQueueManagement queueManager = null;
	// queue parameters in super class
	// public boolean setupQueueListener = false;
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
	 * @see ACQueueManagement
	 * @see QueueParameters
	 */
	private ACQueueManagement(QueueParameters queueParameters) {
		this.queueParameters = queueParameters;
		// TODO: Write the Log System initialization here.
		Log.ConfigureLogger();
	}

	/**
	 * Get an instance of the QueueManagement
	 * 
	 * @param queueParameters
	 *            the parameters for the QueueManagement
	 * @param queueUser
	 *            the QueueUser for the QueueManagement
	 * @return returns an instance of QueueManagement
	 * @see ACQueueManagement
	 */
	public static ACQueueManagement getInstance(
			QueueParameters queueParameters, IMessageProcessor queueUser) {
		if (queueManager == null) {
			queueManager = new ACQueueManagement(queueParameters);
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

	@Override
	protected void createConnectionAndChannel() {
		Log.logger.info("Creating a connection and channel");
		QueueParameters hostQueueParameters = ACNetwork.ACMessageQueueParameters;
		factory = new ConnectionFactory();
		factory.setHost(GlobalData.Constants.localHost);
		factory.setPort(Integer.parseInt(hostQueueParameters.port));
		factory.setUsername(hostQueueParameters.username);
		factory.setPassword(hostQueueParameters.password);
		factory.setVirtualHost(hostQueueParameters.virtualHost);
		Log.logger.info(hostQueueParameters.toString());
		// factory.setRequestedHeartbeat(0);
		try {
			conn = factory.newConnection();

			channel = conn.createChannel();
			channel.basicQos(prefetchCount);
			channel.exchangeDeclare(hostQueueParameters.exchange, "fanout");
			channel.queueDeclare(hostQueueParameters.queueName, false, false,
					true, null);
			channel.queueBind(hostQueueParameters.queueName,
					hostQueueParameters.exchange,
					hostQueueParameters.routingKey);
			this.setupQueueListener = true;
			Log.logger.info("Finished creating connection and channel");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * This method is called during AgentController shutdown for stopping the
	 * messaging and running cleanup on the message queues and channels.
	 * 
	 * @throws IOException
	 */
	public void exitMessaging() {
		try {
			channel.close();
			conn.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			Log.logger.error("" + ACQueueManagement.class.getName());
			ex.printStackTrace();
			System.exit(0);
		} catch (IOException ex) {
			Log.logger.error("" + ACQueueManagement.class.getName());
			ex.printStackTrace();
			System.exit(0);
		} catch (Exception ex) {
			Log.logger.error(ACQueueManagement.class.getName());
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
			channel.basicPublish(ACNetwork.ACMessageQueueParameters.exchange,
					"", null, messageBodyBytes);
			outputBuffer.close();
			return true;
		} catch (IOException ex) {
			Log.logger.info(ACQueueManagement.class.getName());
			Log.logger.info("Error Sending Message" + ex.getMessage());
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * This method checks if two QueueManagement objects are same
	 * 
	 * @param obj
	 *            an object of QueueManagement type
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
		final ACQueueManagement other = (ACQueueManagement) obj;
		if (this.queueParameters != other.queueParameters
				&& (this.queueParameters == null || !this.queueParameters
						.equals(other.queueParameters))) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the hash code of the QueueManagement object
	 * 
	 * @return the hash code of the QueueManagement object
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

	@Override
	public boolean send(String host, Message message) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void processMessage(Communication.messages.Message receivedMessage) {
		// TODO Auto-generated method stub

	}
}
