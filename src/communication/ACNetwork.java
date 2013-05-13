package communication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class has data structures about the communication between the different
 * Agent Controllers in the system. This class defines the various status that
 * all Agent Controllers understand, i.e. it defines the protocol values. This
 * class is created as a singleton.
 */
public class ACNetwork {

	/**
	 * The list of hosts that contain a Agent Controller. The list will contain
	 * IP addresses for the hosts.
	 */
	public static List<String> agentControllerhostList = Collections
			.synchronizedList(new ArrayList<String>());
	/**
	 * The map of the queue for look-up of their respective queue parameters for
	 * messaging. Note that the queue name is used and NOT the IP address
	 * allowing for multiple AC to exist with the same IP but different Queue
	 * names.
	 */
	public static Map<String, QueueParameters> hostMessageQueueLookup = Collections
			.synchronizedMap(new HashMap<String, QueueParameters>());

	/**
	 * TODO: New Code to store the queue parameters for AgentControllers. This
	 * will be the only queue hence stored in a single variable. (Unlike the old
	 * hash-map).
	 */
	public static QueueParameters ACMessageQueueParameters = new QueueParameters();

	/**
	 * TODO: A new configuration file or MachineConfig file needs to be
	 * modified. The Boot process also needs to be modified. This is the second
	 * queue dedicated for agent to agent communication.
	 */
	public static QueueParameters agentMessageQueueParameters = new QueueParameters();

	/*
	 * Values representing work done status for CTA
	 */
	public final static int AC_DONE_WITH_WORK = 0;
	public final static int AC_READY_FOR_NEXT_TICK = 1;
	public final static int AC_TIMED_OUT = 4;
	public final static int AC_COMPUTING = 6;
	public final static long MAXIMUM_TIME_OUT_FOR_AC = 100000;

	/*
	 * Create a empty object for singleton.
	 */
	private ACNetwork() {
	}
}
