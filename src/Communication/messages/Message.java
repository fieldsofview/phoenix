package Communication.messages;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * This class represents a message template which is used to create the
 * different types of messages required for exchange during the simulation.
 */

public abstract class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Object messageObject;
	/**
	 * The destination host-name for the message
	 */
	public String hostName;
	/**
	 * The time when the message is sent
	 */
	public Timestamp timestamp;

	protected abstract void createMessage();
}
