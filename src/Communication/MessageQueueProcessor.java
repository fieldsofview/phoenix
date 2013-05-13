package Communication;

/**
 * This interface defines the method to be called to process the received 
 * message. Agent Controllers implement this interface to handle their 
 * received messages. Agents can implement this interface to handle their 
 * direct messages in case the simulation uses agent to agent communication.
 */
public interface MessageQueueProcessor {

    /**
     * Received message processing method
     * @param message the message received
     */
    public ProcessReceivedMessage processMessage = new ProcessReceivedMessage();
}
