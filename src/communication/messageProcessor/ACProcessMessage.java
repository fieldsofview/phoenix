/*
 * Remove this file eventually
 */
package communication.messageProcessor;

import java.util.List;

import system.Log;

import communication.ACNetwork;
import communication.messages.ACStatusMessage;
import communication.messages.Message;

import agents.Agent;
import agents.AgentController;

/**
 * This class implemented as a thread will interpret the message received
 * 
 */
public class ACProcessMessage extends Thread implements IMessageProcessor {

	/**
	 * The list of agents
	 */
	public List<Agent> agents;
	/**
	 * The message object that is received.
	 */
	public Message message;

	/**
     * 
     */
	public ACProcessMessage(Message receivedMessage) {
		this.message = receivedMessage;
		Log.logger.info("Created Message Receiver");
	}

	/**
     * 
     */
	public void run() {
		receivedMessage();
	}

	/**
	 * 
	 * @param message
	 */
	// public void receivedMessageHelper(Message message) {
	// this.message = message;
	// }

	/**
     * 
     */
	public void receivedMessage() {
		// update status of the host from which message was received
		Log.logger.info("AC: ReceivedMessage");
		// Find out what type of status update it is
		ACStatusMessage statusType = (ACStatusMessage) message.messageObject;
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

	@Override
	public void processMessage(Message receivedMessage) {
		// TODO Auto-generated method stub
		// ACStatusMessage revcMessage = (ACStatusMessage) receivedMessage;

	}
}
