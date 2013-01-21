package Communication;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class has data structures about the communication between the different
 * Agent Controllers in the system. This class defines the various status that 
 * all Agent Controllers understand, i.e. it defines the protocol values. 
 * This class is created as a singleton.
 */
public class ACNetwork {

    /**
     * The list of hosts that contain a Agent Controller
     */
    public static List<String> agentControllerhostList = Collections.synchronizedList(new ArrayList<String>());
    /**
     * The map of the hosts for look-up of their respective queue parameters 
     * for messaging.
     */
    public static Map<String, QueueParameters> hostMessageQueueLookup = Collections.synchronizedMap(new HashMap<String, QueueParameters>());//new HashMap<String, QueueParameters>();
    /**
     * The map of the hosts and their AgentController names.
     */
    //public static Map<Integer, ArrayList<String>> hostTypeMap = Collections.synchronizedMap(new HashMap<Integer, ArrayList<String>>());
    /*
     * Values representing work done status for CTA
     */
    public final static int AC_DONE_WITH_WORK = 0;
    public final static int AC_READY_FOR_NEXT_TICK = 1;
    public final static int AC_TIMED_OUT = 4;
    public final static int AC_COMPUTING = 6;
    public final static long MAXIMUM_TIME_OUT_FOR_CTA = 100000;
    //TODO: Check if this function is required and implemented
    public final static int AC_SAVING_STATE = 10;
    public final static int AC_SAVED_STATE = 8;
    public final static int AC_RESTORED_STATE = 9;
    public final static long MAXIMUM_TIME_OUT_FOR_SAVING_STATE = 100000;    
//    /**
//     * Value representing RMQ type status update
//     */
//    public final static int RMQ_TYPE_STATUS_UPDATE = 2;
//    /**
//     * Value representing the RMQ type agent message
//     */
//    public final static int RMQ_TYPE_AGENT_MSG = 3;
//    /**
//     * Value representing the RMQ type agent Data for data exchanges
//     */
//    public final static int RMQ_TYPE_AGENT_DATA = 7;
//    /**
//     * The CTA time out value.
//     */
    /*
     * Create a empty object for singleton.
     */
    private ACNetwork() {
    }
}


