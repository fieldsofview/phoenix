package Communication;

import Agents.AgentController;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * This class represents an empty message template which is used to generate
 * various messages that is exchanged between the CTA. The message is capable of
 * sending one object and records the time-stamp, source and destination.
 */
public class Message implements Serializable {

    /**
	 * 
	 */
	//TODO: Check how to implement serialisation in java now.
	private static final long serialVersionUID = 1L;
	
	/**
     * The type of message. The type defines how the message is processed on
     * individual CTA. The processing is the responsibility of the CTA
     * @see ACNetwork
     * @see AgentController
     * @see VehicleCTA
     * @see PeopleCTA
     * @see EmergencyServiceCTA
     * @see TrafficLightCTA
     */
    public int type;
    /**
     * The message body, sent as a generic object
     */
    public Object messageObject;
    /**
     * The destination host-name for the message
     */
    public String hostName;
    /**
     * The time when the message is sent
     */
    public Timestamp timestamp;

    /**
     * Constructor creates an empty message object with current system time as
     * time stamp. NOTE: The time stamp is the current system time on which
     * the CTA is running.
     */
    //TODO: Check if different time zones will affect message time stamps
    public Message() {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        this.timestamp = new Timestamp(now.getTime());
    }
}
