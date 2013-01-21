package Agents;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import Communication.ACNetwork;
import Communication.Message;
import Communication.QueueManager;
import Communication.QueueParameters;
import Communication.MessageQueueProcessor;
import GlobalData.Constants;
import System.Boot;
import System.Log;

/**
 * The Default Agent Controller. All types of Agent Controllers extend this
 * class. The properties and methods of this class is shared among all Agent 
 * controllers within the simulation. This class is responsible for
 * synchronisation communication and progress of the simulation. This is first
 * class that establishes contact and proceeds with the simulation.
 */
public abstract class AgentController implements MessageQueueProcessor, Serializable {

    /*
	 * Current version for serialisation. 
	 */
	private static final long serialVersionUID = 1L;
	
	/*
	 * The current name of the agent controller which has to be set. This name
	 * will be used for routing messages to this agent controller.
	 */
	private String agentControllerName;
	
	/**
	 * The current TICK number. TICK number indicates the steps of simulation
	 * and is used for synchronisation of various CTAs.
	 */
    public static int currentTickNumber;
    
    //TODO: Check if restore and save state is implemented and required.
    /**
	 * This flag when set will direct the AC to start saving its state
	 */
    public static boolean initiateStateSave = false;
    
    //TODO: Check if restore and save state is implemented and required.
    /**
	 * This is the tick number at which other ACs will save their states
	 */
    public static int tickNumberForStateSave = -1;

    /**
	 * A list of all agents handled by the Agent Controller
	 */
    public List<Agent> agents;
    
    /**
     * The AgentController's communication queue.
     */
    public QueueManager queueManager;
    
    /**
     * The current status of the other AgentControllers that are involved
     * in the simulation.
     */
    public static Map<String, Integer> ACStatus;

    /* 
     * Current simulation step the AgentController is in. 
     */
    public int getCurrentTickNumber() {
        return currentTickNumber;
    }

    /**
	 * Abstract function to be overridden by each AC. This function should
	 * contain the logic for checking if every agent has completed its task for
	 * the current tick.
	 * 
	 * @return
	 */
    public abstract boolean checkIfAllAgentsReadyForNextTick();

    /**
     * Call the individual agents behaviour from its list of behaviour
     */
    public abstract void activateAgentBehaviour();

    /**
     * Check if all the AgentControllers are ready for the next step of the
     * simulation.
     * @return
     */
	public boolean checkIfAllACsReadyForNextTick(boolean flag) {
        if (ACStatus.size() == 0) {
            Log.logger.info("I am the only host");
            return true;
        }
        Log.logger.info("Holdmessages status:" + flag);
		if (ACStatus.containsValue(ACNetwork.AC_COMPUTING)) {
            Log.logger.info("Some hosts are busy");
            return false;
        } else {
            return true;
        }
    }

    /**
	 * This function checks if every AC in the system is ready to start saving
	 * their state.
	 * 
	 * @return true if each AC is ready, false otherwise
	 */
	public boolean checkIfAllCTAsReadyForStateSave() {
        if (ACStatus.size() == 0) {
            Log.logger.info("I am the only host");
            return true;
        }

		if (ACStatus.containsValue(ACNetwork.AC_COMPUTING)) {
            Log.logger.info("Some hosts are busy");
            return false;
        } else {
            return true;
        }
    }

	//TODO: Check if restore and save state is implemented and required.
    /**
	 * This function checks if every AC in the system has finished saving their
	 * states.
	 * 
	 * @return true if each AC is ready, false otherwise
	 */
	public boolean checkIfAllCTAsDoneWithStateSave() {
        if (ACStatus.size() == 0) {
            Log.logger.info("I am the only host");
            return true;
        }

		if (ACStatus.containsValue(ACNetwork.AC_COMPUTING)
				|| ACStatus.containsValue(ACNetwork.AC_SAVING_STATE)) {
            Log.logger.info("Some hosts are busy");
            return false;
        } else {
            return true;
        }
    }
	
	//TODO: Check if restore and save state is implemented and required.
    /**
	 * This function checks if every CTA in the system has completed restoring
	 * their states from an input file.
	 * 
	 * @return true if each CTA has completed, false otherwise
	 */
	public boolean checkIfAllACsDoneWithRestoreState() {
        if (ACStatus.size() == 0) {
            Log.logger.info("I am the only host");
            return true;
        }

		if (ACStatus.containsValue(ACNetwork.AC_COMPUTING)
				|| ACStatus.containsValue(ACNetwork.AC_SAVING_STATE)) {
            Log.logger.info("Some hosts are busy");
            return false;
        } else {
            return true;
        }
    }

    /**
     * Create the communication channel for the communication
     */
    public void addQueueListener() {
		QueueParameters queueParameters = ACNetwork.hostMessageQueueLookup
				.get(Constants.localHost);
        queueManager = QueueManager.getInstance(queueParameters, this);
        queueManager.start();

        //processMessage = new ProcessReceivedMessage(agents);
        //processMessage.start();
        processMessage.agents = agents;
    }

    /**
     * Broadcast a message to all the AgentControllers that the current
     * AgentController is ready to proceed to the next step of the simulation.
     */
    public void sendReadyForTick() {
        for (String host : ACStatus.keySet()) {
            Integer status = ACStatus.get(host);
			if (status == ACNetwork.AC_COMPUTING
					|| status == ACNetwork.AC_READY_FOR_NEXT_TICK) {
				// queueManager.send(host, ACNetwork.RMQ_TYPE_STATUS_UPDATE +
				// ":" + Constants.localHost + ":" +
				// ACNetwork.AC_READY_FOR_NEXT_TICK);
                Message sendTickMessage = new Message();
				sendTickMessage.type = ACNetwork.RMQ_TYPE_STATUS_UPDATE;
                sendTickMessage.hostName = Constants.localHost;

                Message statusMessage = new Message();
				statusMessage.type = ACNetwork.AC_READY_FOR_NEXT_TICK;
                statusMessage.messageObject = Constants.localHost;
                statusMessage.hostName = Constants.localHost;

                sendTickMessage.messageObject = statusMessage;
                queueManager.send(host, sendTickMessage);
            }
        }
    }

    /**
     * Broadcast a message that the current Agent COntroller has completed
     * running the behaviour of all its agents once and is ready to proceede
     * with the simulation.
     */
    public void sendDoneWithWork() {
        for (String host : ACStatus.keySet()) {
			if (ACStatus.get(host) == ACNetwork.AC_COMPUTING
					|| ACStatus.get(host) == ACNetwork.AC_READY_FOR_NEXT_TICK) {
				// queueManager.send(host, ACNetwork.RMQ_TYPE_STATUS_UPDATE +
				// ":" + Constants.localHost + ":" +
				// ACNetwork.AC_DONE_WITH_WORK);
                Message sendTickMessage = new Message();
				sendTickMessage.type = ACNetwork.RMQ_TYPE_STATUS_UPDATE;
                sendTickMessage.hostName = Constants.localHost;

                Message statusMessage = new Message();
				statusMessage.type = ACNetwork.AC_DONE_WITH_WORK;
                statusMessage.hostName = Constants.localHost;
                statusMessage.messageObject = Constants.localHost;

                sendTickMessage.messageObject = statusMessage;
                queueManager.send(host, sendTickMessage);
            }
        }
    }

    //TODO: Check if restore and save state is implemented and required.
    /**
     * sent a message to the other Agent Controllers to start saving states
     */
    public void sendSavingState(int tickNumber) {
        for (String host : ACStatus.keySet()) {
			if (ACStatus.get(host) == ACNetwork.AC_COMPUTING
					|| ACStatus.get(host) == ACNetwork.AC_READY_FOR_NEXT_TICK
					|| ACStatus.get(host) == ACNetwork.AC_SAVING_STATE) {
				// queueManager.send(host, ACNetwork.RMQ_TYPE_STATUS_UPDATE +
				// ":" + Constants.localHost + ":" +
				// ACNetwork.AC_DONE_WITH_WORK);
                Message sendTickMessage = new Message();
				sendTickMessage.type = ACNetwork.RMQ_TYPE_STATUS_UPDATE;
                sendTickMessage.hostName = Constants.localHost;

                Message statusMessage = new Message();
				statusMessage.type = ACNetwork.AC_SAVING_STATE;
                statusMessage.hostName = Constants.localHost;
                statusMessage.messageObject = tickNumber;

                sendTickMessage.messageObject = statusMessage;
                queueManager.send(host, sendTickMessage);
            }
        }
    }

  //TODO: Check if restore and save state is implemented and required.
    /**
     * 
     */
    public void sendSavedState() {
        for (String host : ACStatus.keySet()) {
			if (ACStatus.get(host) == ACNetwork.AC_COMPUTING
					|| ACStatus.get(host) == ACNetwork.AC_READY_FOR_NEXT_TICK
					|| ACStatus.get(host) == ACNetwork.AC_SAVING_STATE) {
				// queueManager.send(host, ACNetwork.RMQ_TYPE_STATUS_UPDATE +
				// ":" + Constants.localHost + ":" +
				// ACNetwork.AC_DONE_WITH_WORK);
                Message sendTickMessage = new Message();
				sendTickMessage.type = ACNetwork.RMQ_TYPE_STATUS_UPDATE;
                sendTickMessage.hostName = Constants.localHost;

                Message statusMessage = new Message();
				statusMessage.type = ACNetwork.AC_SAVED_STATE;
                statusMessage.hostName = Constants.localHost;
                statusMessage.messageObject = Constants.localHost;

                sendTickMessage.messageObject = statusMessage;
                queueManager.send(host, sendTickMessage);
            }
        }
    }

  //TODO: Check if restore and save state is implemented and required.
    /**
     *
     */
    public void sendRestoreState() {
        for (String host : ACStatus.keySet()) {
            Log.logger.info("In Send Restore Method");
			// if (CTAStatus.get(host) == ACNetwork.AC_COMPUTING ||
			// CTAStatus.get(host) == ACNetwork.AC_READY_FOR_NEXT_TICK) {
			// queueManager.send(host, ACNetwork.RMQ_TYPE_STATUS_UPDATE + ":" +
			// Constants.localHost + ":" + ACNetwork.AC_READY_FOR_NEXT_TICK);
            Message sendTickMessage = new Message();
			sendTickMessage.type = ACNetwork.RMQ_TYPE_STATUS_UPDATE;
            sendTickMessage.hostName = Constants.localHost;

            Message statusMessage = new Message();
			statusMessage.type = ACNetwork.AC_RESTORED_STATE;
            statusMessage.messageObject = Constants.localHost;
            statusMessage.hostName = Constants.localHost;

            sendTickMessage.messageObject = statusMessage;
            queueManager.send(host, sendTickMessage);
            //}
        }
    }
    /**
     *
     * @param host
     * @param status
     */
	public static void changeACStatus(String host, Integer status) {
        ACStatus.put(host, status);
    }

    /**
     *
     */
	public void buildACStatus() {
        Log.logger.info("Building list");
		Iterator<String> hosts = ACNetwork.agentControllerhostList.iterator();
        while (hosts.hasNext()) {
            String host = hosts.next();
            if (!host.equalsIgnoreCase(Constants.localHost)) {
				ACStatus.put(host, ACNetwork.AC_COMPUTING);
            }
        }
    }

    /**
     *
     */
    public void updateTimeOutList() {
        for (String host : ACStatus.keySet()) {
			if (ACStatus.get(host) == ACNetwork.AC_COMPUTING) {
				// CTAStatus.remove(host);
				Log.logger.info(host + " : AC has timed out");
				ACStatus.put(host, ACNetwork.AC_TIMED_OUT);
            }
        }

    }

    /**
     *
     */
	public void changeReadyACsToComputing() {
        for (String host : ACStatus.keySet()) {
			if (ACStatus.get(host) == ACNetwork.AC_READY_FOR_NEXT_TICK
					|| ACStatus.get(host) == ACNetwork.AC_SAVING_STATE
					|| ACStatus.get(host) == ACNetwork.AC_SAVED_STATE) {
				// ACStatus.remove(host);
				// Utilities.Log.logger.info(host + " : AC has timed out");
				ACStatus.put(host, ACNetwork.AC_COMPUTING);
            }
        }
    }

    /**
	 * Changes the status of all ACs to ready, once they finish saving their
	 * states. This is done because the status of the AC would be SAVED_STATE
	 * after they finish, and the system would not move forward.
	 */
	public void changeSavedCTAsToReady() {
        for (String host : ACStatus.keySet()) {
			if (ACStatus.get(host) == ACNetwork.AC_SAVED_STATE) {
				// CTAStatus.remove(host);
				// Utilities.Log.logger.info(host + " : CTA has timed out");
				ACStatus.put(host, ACNetwork.AC_READY_FOR_NEXT_TICK);
            }
        }
    }

	//TODO: Remove the KML outputs Write the ouput to java livegraph here.
    //protected void writeKMLFile(String ctaName) {
    //    String stamp = new SimpleDateFormat("hh-mm-ss-aaa_dd-MMMMM-yyyy").format(new Date()).toString();
    //    //kmlUtility.writeFile();
    //    //kmlUtility.writeFile("kml/" + ctaName + "_" + stamp + "_" + currentTickNumber + ".kml");
	//
    //}

    /**
     * This is the part of the boot process where each agent controller reads
     * how the simulation is set up across the machines.
     */
    protected void readConfigurations() {
        try {
            Boot.loadMachineConfigurations("config/agentControllerConfig");
        } catch (FileNotFoundException ex) {
			//Logger.getLogger(PeopleCTA.class.getName()).log(Level.SEVERE, null,
			//		ex);
        } catch (IOException ex) {
			//Logger.getLogger(PeopleCTA.class.getName()).log(Level.SEVERE, null,
			//		ex);
        }
    }

  //TODO: Check if this function is required and implemented
    /*
	 * Function to save state. Overridden by every AC
	 */
	protected void saveCTAState() {
    }
	
	//TODO: Check if this function is required and implemented
    /*
     * Function to restore state, from a file indicated by the parameter
     * @param filename 
     */
    protected void restoreState(String filename) {
    }

	/**
	 * @return the agentControllerName
	 */
	public String getAgentControllerName() {
		return agentControllerName;
	}

	/**
	 * @param agentControllerName the agentControllerName to set
	 */
	public void setAgentControllerName(String agentControllerName) {
		this.agentControllerName = agentControllerName;
	}
}
