package Communication.QueueManager;

import Communication.QueueParameters;
import Communication.messages.Message;

//TODO: Abstract out all the functions of the Queue manager to this class.
/**
 * Abstract class with the skeletal structure to set up a queue manager. Will
 * contain the send and possibly define the receive method too. If you define
 * receive method remove interface MessageQueueProcessor and add the abstract
 * receive method here.
 * 
 * @author bubby
 * 
 */
public abstract class QueueManager extends Thread {

	protected QueueParameters queueParameters;

	protected abstract Message addQueueListener(QueueParameters queueParameters);

	public void run() {
		addQueueListener(queueParameters);
	}
}
