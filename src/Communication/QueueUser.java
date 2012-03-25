package Communication;

import System.ProcessReceivedMessage;

/**
 * This interfce defines the method to be called to process the received message.
 * CTAs implement this interface to handle their received messages.
 */
public interface QueueUser {

    /**
     * Received message processing method
     * @param message the message received
     */
//    public void receivedMessageHelper(Message message);
    public ProcessReceivedMessage processMessage = new ProcessReceivedMessage();
}
