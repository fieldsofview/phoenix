package Communication.messages;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * This class represents text messages is exchanged. The message is capable of
 * sending one object and records the time-stamp, source and destination.
 */
public class TextMessage extends Message implements Serializable {

	/**
	 * 
	 */
	// TODO: Check how to implement serialisation in java now.
	private static final long serialVersionUID = 1L;
	public int AC_STATUS;

	/**
	 * Constructor creates an empty message object with current system time as
	 * time stamp. NOTE: The time stamp is the current system time on which the
	 * simulation is running.
	 */
	// TODO: Check if different time zones will affect message time stamps
	public TextMessage() {
		createMessage();
	}

	@Override
	protected void createMessage() {
		Calendar calendar = Calendar.getInstance();
		Date now = calendar.getTime();
		this.timestamp = new Timestamp(now.getTime());
		this.messageObject = new String();
	}
}