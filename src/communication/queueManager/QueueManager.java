/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. and 
 * at http://code.fieldsofview.in/phoenix/wiki/FOV-MPL2 */

package communication.queueManager;

import java.io.IOException;

import communication.QueueParameters;
import communication.messages.Message;

//TODO: Abstract out all the functions of the Queue manager to this class.
/**
 * Abstract class with the skeletal structure to set up a queue manager. Will
 * contain the send and possibly define the receive method too. If you define
 * receive method remove interface IMessageProcessor and add the abstract
 * receive method here.
 * 
 * This class is also implemented as a singleton in order to make sure only a
 * single queue management system is present for a given entity.
 * 
 * @author bubby
 * 
 */
public abstract class QueueManager extends Thread {

	// private static QueueManager queueManager = null;

	protected QueueParameters queueParameters;
	public boolean setupQueueListener = false;

	/**
	 * A hash map of the list of rabbitMQ channels corresponding the host name
	 * Each host name is a host running AgentController and these channels are
	 * used to send messages to them. This map represents a directory / address
	 * map
	 */
	// private HashMap<String, Channel> hostChannelMap = null;

	/**
	 * Create a new queue manager given the QueueParameters
	 * 
	 * @param queueParameters
	 *            the QueueParameters for the queue manager
	 * @see QueueManagement
	 * @see QueueParameters
	 */
	// private QueueManager(QueueParameters queueParameters) {
	// this.queueParameters = queueParameters;
	// }

	/**
	 * Get an instance of the QueueManagement for singleton.
	 */
	// public abstract QueueManager getInstance();

	/**
	 * This method creates a connection to the rabbitMQ server on each of the
	 * given hosts and opens channels for communication. These channels are used
	 * for communication between between the given CTA and import
	 * system.TrafficLightCTA; the rest of the CTAs.
	 * 
	 * @throws Exception
	 */
	protected abstract void createConnectionAndChannel();

	/**
	 * This method is called during CTA shutdown for stopping the messaging and
	 * running cleanup on the message queues and channels
	 * 
	 * @throws IOException
	 */
	protected abstract void exitMessaging();

	/**
	 * This method adds a new queue listener given the queue parameters. The
	 * listener waits for a message to arrive and calls the appropriate method
	 * for processing, after it arrives
	 * 
	 * @param queueParameters
	 *            the QueueParameters for listening
	 * @see QueueParameters
	 */
	protected abstract Message addQueueListener(QueueParameters queueParameters);

	/**
	 * This method is called when a message has to be sent form one location to
	 * another. The method makes use of the already open channels between the
	 * nodes to send its messages.
	 * 
	 * @param host
	 *            the destination host name
	 * @param message
	 *            the message that has to be sent
	 * @return true if the message was successfully sent
	 * @see Message
	 */
	abstract public boolean send(String destination, Message message);

	protected abstract void processMessage(Message receivedMessage);

	public void run() {
		addQueueListener(queueParameters);
	}
}
