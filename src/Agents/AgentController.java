package Agents;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import Communication.ACNetwork;
import Communication.Message;
import Communication.QueueManager;
import Communication.QueueParameters;
import Communication.QueueUser;
import GlobalData.Constants;
import System.Boot;

/**
 * The Default Agent Controller. All types of Agent Controllers extend this
 * class. The properties and methods of this class is shared among all care
 * taker agents.
 */
public abstract class AgentController implements QueueUser, Serializable {

    /**
	 * The current TICK number. TICK number indicates the steps of simulation
	 * and is used for synchronisation of various CTAs.
	 */
    public static int currentTickNumber;
    /**
	 * This flag when set will direct the AC to start saving its state
	 */
    public static boolean initiateStateSave = false;
    /**
	 * This is the tick number at which other ACs will save their states
	 */
    public static int tickNumberForStateSave = -1;

    /**
	 * A list of all agents handled by the Agent Controller
	 */
    public List<Agent> agents;
    /**
     *
     */
    public QueueManager queueManager;
    /**
     *
     */
    public static Map<String, Integer> ACStatus;

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
     *
     */
    public abstract void activateAgentBehaviour();

    /**
     *
     * @return
     */
	public boolean checkIfAllACsReadyForNextTick(boolean flag) {
        if (ACStatus.size() == 0) {
            System.Log.logger.info("I am the only host");
            return true;
        }
        System.Log.logger.info("Holdmessages status:" + flag);
		if (ACStatus.containsValue(ACNetwork.AC_COMPUTING)) {
            System.Log.logger.info("Some hosts are busy");
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
            System.Log.logger.info("I am the only host");
            return true;
        }

		if (ACStatus.containsValue(ACNetwork.AC_COMPUTING)) {
            System.Log.logger.info("Some hosts are busy");
            return false;
        } else {
            return true;
        }
    }

    /**
	 * This function checks if every AC in the system has finished saving their
	 * states.
	 * 
	 * @return true if each AC is ready, false otherwise
	 */
	public boolean checkIfAllCTAsDoneWithStateSave() {
        if (ACStatus.size() == 0) {
            System.Log.logger.info("I am the only host");
            return true;
        }

		if (ACStatus.containsValue(ACNetwork.AC_COMPUTING)
				|| ACStatus.containsValue(ACNetwork.AC_SAVING_STATE)) {
            System.Log.logger.info("Some hosts are busy");
            return false;
        } else {
            return true;
        }
    }

    /**
	 * This function checks if every CTA in the system has completed restoring
	 * their states from an input file.
	 * 
	 * @return true if each CTA has completed, false otherwise
	 */
	public boolean checkIfAllACsDoneWithRestoreState() {
        if (ACStatus.size() == 0) {
            System.Log.logger.info("I am the only host");
            return true;
        }

		if (ACStatus.containsValue(ACNetwork.AC_COMPUTING)
				|| ACStatus.containsValue(ACNetwork.AC_SAVING_STATE)) {
            System.Log.logger.info("Some hosts are busy");
            return false;
        } else {
            return true;
        }
    }

    /**
     *
     */
    public void addQueueListener() {
		QueueParameters queueParameters = ACNetwork.hostQueueMap
				.get(Constants.localHost);
        queueManager = QueueManager.getInstance(queueParameters, this);
        queueManager.start();

        //processMessage = new ProcessReceivedMessage(agents);
        //processMessage.start();
        processMessage.agents = agents;
    }

    /**
     *
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
     *
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

    /**
     *
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

    /**
     *
     */
    public void sendRestoreState() {
        for (String host : ACStatus.keySet()) {
            System.Log.logger.info("In Send Restore Method");
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

	// public void sendFinishedExecutingAC() {
	// for (String host : ACStatus.keySet()) {
	// //if (ACStatus.get(host) == ACNetwork.AC_COMPUTING ||
	// ACStatus.get(host) == ACNetwork.AC_READY_FOR_NEXT_TICK) {
	// //queueManager.send(host, ACNetwork.RMQ_TYPE_STATUS_UPDATE + ":" +
	// Constants.localHost + ":" + ACNetwork.AC_DONE_WITH_WORK);
//            Message sendTickMessage = new Message();
	// sendTickMessage.type = ACNetwork.RMQ_TYPE_STATUS_UPDATE;
//            sendTickMessage.hostName = Constants.localHost;
//
//            Message statusMessage = new Message();
	// statusMessage.type = ACNetwork.AC_COMPLETE_EXIT;
//            statusMessage.hostName = Constants.localHost;
//            statusMessage.messageObject = Constants.localHost;
//
//            sendTickMessage.messageObject = statusMessage;
//            queueManager.send(host, sendTickMessage);
//            // }
//        }
//    }
    /**
     *
     * @param host
     * @param status
     */
	public static void changeACStatus(String host, Integer status) {
		// if (ACStatus.containsKey(host)) {
		// ACStatus.remove(host);
//        }
        ACStatus.put(host, status);
    }

    /**
     *
     */
	public void buildACStatus() {
        System.Log.logger.info("Building list");
		Iterator<String> hosts = ACNetwork.hosts.iterator();
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
				System.Log.logger.info(host + " : AC has timed out");
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

    protected void writeKMLFile(String ctaName) {
        String stamp = new SimpleDateFormat("hh-mm-ss-aaa_dd-MMMMM-yyyy").format(new Date()).toString();
        //kmlUtility.writeFile();
        kmlUtility.writeFile("kml/" + ctaName + "_" + stamp + "_" + currentTickNumber + ".kml");

    }

    protected void readConfigurations() {
        // Configuration functions
        try {
            Boot.loadMachineConfigurations("config/machineConfig");
            Boot.loadAgentConfigurations("config/agentConfig");
            Boot.loadHospitalLocations("config/hospitalConfig");
            Boot.loadDisasterLocations("config/disasterConfig");
            Boot.loadCivilVehicleConfigurations("config/vehicleConfig");
            Boot.loadEmergencyVehicleConfigurations("config/emergencyVehicleConfig");
        } catch (FileNotFoundException ex) {
			Logger.getLogger(PeopleCTA.class.getName()).log(Level.SEVERE, null,
					ex);
        } catch (IOException ex) {
			Logger.getLogger(PeopleCTA.class.getName()).log(Level.SEVERE, null,
					ex);
        }
    }

    /**
	 * Function to save state. Overridden by every AC
	 */
	protected void saveCTAState() {
    }

    /**
     * Function to restore state, from a file indicated by the parameter
     * @param filename 
     */
    protected void restoreState(String filename) {
    }
}
