/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. and 
 * at http://code.fieldsofview.in/phoenix/wiki/FOV-MPL2 */

package communication.queueManager;

import agents.AgentController;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import communication.ACNetwork;
import communication.QueueParameters;
import communication.messages.ACStatusMessage;
import communication.messages.Message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import system.Log;

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
		system.Log.ConfigureLogger();
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
	public static ACQueueManagement getInstance(QueueParameters queueParameters) {
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
		this.addQueueListener(queueParameters);
	}

	@Override
	protected void createConnectionAndChannel() {
		Log.logger.info("Creating a connection and channel");
		QueueParameters hostQueueParameters = ACNetwork.ACMessageQueueParameters;
		factory = new ConnectionFactory();
		factory.setHost(system.Constants.localHost);
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
			// TODO: Test exchange code here
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

				/* Process the received message */
				processMessage(message);
				// messageProcessor.processMessage.run();
				// Log.logger.info("QM: I am listening for messages in the while loop 10");
				channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
				// Log.logger.info("QM: I am listening for messages in the while loop 11");
			}
			Log.logger.error("Finished Adding queue listener");

		} catch (ClassNotFoundException ex) {
			Log.logger.error("" + ACQueueManagement.class.getName()+" "+ex.getMessage());
			
			System.exit(0);
		} catch (IOException ex) {
			Log.logger.error("" + ACQueueManagement.class.getName()+" "+ex.getMessage());
			ex.getMessage();
			System.exit(0);
		} catch (Exception ex) {
			Log.logger.error(ACQueueManagement.class.getName()+" "+ex.getMessage());
			ex.getMessage();
			System.exit(0);
		}
		return message;
	}

	/**
	 * This method is called when a message has to be sent form one
	 * AgentController to another. The method makes use of the already open
	 * channels between the ACs to send its messages. THe destination is not
	 * used for ACQueueManagemnt as the ACs use a fan-out exchange to exchange
	 * information.
	 * 
	 * @param host
	 *            defaults to Null in ACQueueMangement
	 * 
	 * @param message
	 *            the message that has to be sent
	 * @return true if the message was successfully sent
	 * @see Message
	 */
	@Override
	public boolean send(String destination, Message receivedMessage) {

		/*
		 * The receivedMessage is the generic message object that is cast into
		 * the ACStatusMessage type
		 */
		ACStatusMessage message = (ACStatusMessage) receivedMessage;

		while (setupQueueListener == false) {
			Log.logger.info("Waiting to send");
		}

		ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
		try {

			ObjectOutputStream outputWriter = new ObjectOutputStream(
					outputBuffer);
			outputWriter.writeObject(message);
			//outputWriter.close(); // write to buffer and flush;
			byte[] messageBodyBytes = outputBuffer.toByteArray();
                        
			channel.basicPublish(ACNetwork.ACMessageQueueParameters.exchange,
					"", null, messageBodyBytes);
			outputBuffer.close();
			return true;
		} catch (Exception ex) {
			Log.logger.info(ACQueueManagement.class.getName());
			Log.logger.info("Error Sending Message" + ex.getMessage());
			ex.printStackTrace();
                        ex.getMessage();
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

	@Override
	public void processMessage(Message receivedMessage) {
		// update status of the host from which message was received
		Log.logger.info("AC: ReceivedMessage");
		// Find out what type of status update it is
		ACStatusMessage statusType = (ACStatusMessage) receivedMessage;

		switch (statusType.AC_STATUS) {

		case ACNetwork.AC_READY_FOR_NEXT_TICK:
			AgentController.changeACStatus(statusType.hostName,
					statusType.AC_STATUS);
			Log.logger.info("Received Next Tick from : " + statusType.hostName
					+ ":" + statusType.AC_STATUS);
			break;
		case ACNetwork.AC_DONE_WITH_WORK:
			AgentController.changeACStatus(statusType.hostName,
					statusType.AC_STATUS);
			Log.logger.info("Received Done with work from : "
					+ statusType.hostName + ":" + statusType.AC_STATUS);
			break;
		}

	}
}
