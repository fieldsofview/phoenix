package Communication.messageprocessor;

import Communication.messages.Message;

/**
 * This interface defines the method to be called to process the received
 * message. Agent Controllers implement this interface to handle their received
 * messages. Agents can implement this interface to handle their direct messages
 * in case the simulation uses agent to agent communication.
 * 
 * For example, based on a given message if a clean-up has to be performed this
 * operation is implemented using this interface.
 */
public interface IMessageProcessor {

	/**
	 * Received message processing method
	 * 
	 * @param message
	 *            the message received
	 */
	public void processMessage(Message receivedMessage);
}
