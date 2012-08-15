package Communication;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class has data structures about the communication between the different
 * CTAs in the system. This class defines the various status that all CTA's understand
 * and use to interpret the received messages.
 */
public class ACNetwork {

    /**
     * The list of hosts that contain a CTA
     */
    public static List<String> hosts = Collections.synchronizedList(new ArrayList<String>());
    /**
     * The map of the hosts and their respective queue parameters for messageing.
     */
    public static Map<String, QueueParameters> hostQueueMap = Collections.synchronizedMap(new HashMap<String, QueueParameters>());//new HashMap<String, QueueParameters>();
    /**
     * The map of the hosts and their types.
     */
    public static Map<Integer, ArrayList<String>> hostTypeMap = Collections.synchronizedMap(new HashMap<Integer, ArrayList<String>>());
    /**
     * Value representing work done status for CTA
     */
    public final static int AC_DONE_WITH_WORK = 0;
    /**
     * Value representing ready for next tick status for CTA
     */
    public final static int AC_READY_FOR_NEXT_TICK = 1;
    /**
     * Value representing RMQ type status update
     */
    public final static int RMQ_TYPE_STATUS_UPDATE = 2;
    /**
     * Value repersneting the RMQ type agent message
     */
    public final static int RMQ_TYPE_AGENT_MSG = 3;
    /**
     * Value represneting the RMQ type agent Data for data exchanges
     */
    public final static int RMQ_TYPE_AGENT_DATA = 7;
    /**
     * The CTA time out value.
     */
    public final static int AC_TIMED_OUT = 4;
    /**
     * The status of computing
     */
    public final static int AC_COMPUTING = 6;
    /**
     * The status of Saving State
     */
    public final static int AC_SAVING_STATE = 10;
    /**
     * The status of Saved State
     */
    public final static int AC_SAVED_STATE = 8;
    /**
     * The status of Saved State
     */
    public final static int AC_RESTORED_STATE = 9;
    /**
     *Maximum time out value for CTA
     */
    public final static long MAXIMUM_TIME_OUT_FOR_CTA = 100000;
    /**
     *Maximum time out value for CTA
     */
    public final static long MAXIMUM_TIME_OUT_FOR_SAVING_STATE = 100000;

    /**
     * Create a empty object
     */
    private ACNetwork() {
    }
}


